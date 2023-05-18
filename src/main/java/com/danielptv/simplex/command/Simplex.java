package com.danielptv.simplex.command;

import com.danielptv.simplex.entity.Phase;
import com.danielptv.simplex.number.CalculableImpl;
import com.danielptv.simplex.number.Fraction;
import com.danielptv.simplex.number.RoundedDecimal;
import com.danielptv.simplex.service.TableBuildService;
import com.danielptv.simplex.service.TableCalcService;
import com.danielptv.simplex.service.TableExtensionService;
import com.danielptv.simplex.service.TwoPhaseSimplex;
import com.danielptv.simplex.shell.EditType;
import com.danielptv.simplex.shell.InputResult;
import com.danielptv.simplex.shell.OutputHelper;
import com.danielptv.simplex.shell.PromptColor;
import com.danielptv.simplex.shell.SimplexOutput;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@ShellComponent
@RequiredArgsConstructor
public class Simplex {
    private static final String ROUND_HELP = "Mantissa length to round to, i.e. [2]. Pass [false] to disable rounding.";
    private static final String MIN_HELP = "Pass to minimize the problem, omit otherwise.";
    private static final String ROUND_PATTERN = "^(false|\\d{1,2})$";
    private static final int MIN_COUNT = 1;
    private static final int MAX_COUNT = 10;
    private final OutputHelper outputHelper;
    private final SimplexOutput simplexOutput;

    private final HelperComponent helperComponent;

    @ShellMethod(key = {"calculate", "calc"}, value = "Calculate Simplex")
    public void calc(
            @ShellOption(value = {"-v", "--var"}, help = "Number of variables, i.e. [2].")
            @Min(MIN_COUNT) @Max(MAX_COUNT) final int varCount,
            @ShellOption(value = {"-c", "--const"}, help = "Number of constraints, i. e. [3].")
            @Min(MIN_COUNT) @Max(MAX_COUNT) final int constCount,
            @ShellOption(value = {"-r", "--round"}, defaultValue = "false", help = ROUND_HELP)
            @Pattern(regexp = ROUND_PATTERN) final String roundMode,
            @ShellOption(value = {"-m", "--min"}, help = MIN_HELP) final boolean minimize
    ) {
        var objectiveFunction = helperComponent.simplexInput(
                "Objective function:",
                varCount,
                true,
                minimize
        );
        var constraints = IntStream.range(0, constCount)
                .mapToObj(c -> helperComponent.simplexInput(String.format("Constraint %d:", c + 1),
                        varCount,
                        false,
                        minimize
                ))
                .toList();
        outputHelper.print(simplexOutput.displayProblem(objectiveFunction, constraints).toString(), PromptColor.GREEN);

        var edit = helperComponent.editProblem();
        while (!edit.equals(EditType.CONTINUE)) {
            objectiveFunction = helperComponent.simplexInput(
                    "Objective function:",
                    varCount,
                    true,
                    minimize,
                    objectiveFunction
            );
            final var finalConstraints = constraints;
            constraints = IntStream.range(0, constCount)
                    .mapToObj(c -> helperComponent.simplexInput(String.format("Constraint %d:", c + 1),
                            varCount,
                            false,
                            minimize,
                            finalConstraints.get(c)
                    ))
                    .toList();
            outputHelper.print(simplexOutput.displayProblem(objectiveFunction, constraints)
                    .toString(), PromptColor.GREEN);
            edit = helperComponent.editProblem();
        }

        outputHelper.print(String.format("%n"));
        final List<Phase<? extends CalculableImpl<?>>> phases;
        if ("false".equals(roundMode)) {
            final var number = new Fraction();
            phases = executeSimplex(
                    number,
                    varCount,
                    constCount,
                    minimize,
                    objectiveFunction.getValues(),
                    constraints.stream().map(InputResult::getValues).toList()
            );
        } else {
            final var number = new RoundedDecimal(Integer.parseInt(roundMode));
            phases = executeSimplex(
                    number,
                    varCount,
                    constCount,
                    minimize,
                    objectiveFunction.getValues(),
                    constraints.stream().map(InputResult::getValues).toList()
            );
        }
        outputHelper.print(simplexOutput.printResult(phases).toString());
    }

    <T extends CalculableImpl<T>> List<Phase<? extends CalculableImpl<?>>> executeSimplex(
            final T number,
            final int varCount,
            final int constCount,
            final boolean minimize,
            final List<String> objectiveFunction,
            final List<List<String>> constraints
    ) {
        final var calcService = new TableCalcService<>(number);
        final var extensionService = new TableExtensionService<>(number, calcService);
        final var buildService = new TableBuildService<>(number, varCount, constCount, minimize, calcService);
        final var simplex = new TwoPhaseSimplex<>(number, calcService, extensionService);
        final var table = buildService.build(objectiveFunction, constraints);
        final var result = simplex.calc(table);
        return new ArrayList<>(result);
    }
}
