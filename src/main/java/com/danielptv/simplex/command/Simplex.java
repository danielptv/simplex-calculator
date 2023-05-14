package com.danielptv.simplex.command;

import com.danielptv.simplex.entity.Phase;
import com.danielptv.simplex.number.CalculableImpl;
import com.danielptv.simplex.number.Fraction;
import com.danielptv.simplex.number.RoundedDecimal;
import com.danielptv.simplex.service.TableBuildService;
import com.danielptv.simplex.service.TableCalcService;
import com.danielptv.simplex.service.TableExtensionService;
import com.danielptv.simplex.service.TwoPhaseSimplex;
import com.danielptv.simplex.shell.PromptColor;
import com.danielptv.simplex.shell.ShellHelper;
import com.danielptv.simplex.shell.SimplexIO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.ArrayList;
import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class Simplex {
    private static final String ROUND_HELP = "Mantissa length to round to, i.e. [2]. Pass [false] to disable rounding.";
    private static final String MIN_HELP = "Pass to minimize the problem, omit otherwise.";
    private static final String ROUND_PATTERN = "^(false|\\d{1,2})$";
    private static final int MIN_COUNT = 1;
    private static final int MAX_COUNT = 10;
    private final ShellHelper shellHelper;
    private final SimplexIO simplexIO;

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
        final var objectiveFunction = simplexIO.getObjectiveFunction(varCount);
        final var constraints = simplexIO.getConstraints(varCount, constCount);
        shellHelper.print(simplexIO.displayProblem(objectiveFunction, constraints, minimize)
                .toString(), PromptColor.GREEN);

        final List<Phase<? extends CalculableImpl<?>>> phases;
        if ("false".equals(roundMode)) {
            final var number = new Fraction();
            phases = executeSimplex(number, varCount, constCount, minimize, objectiveFunction, constraints);
        } else {
            final var number = new RoundedDecimal(Integer.parseInt(roundMode));
            phases = executeSimplex(number, varCount, constCount, minimize, objectiveFunction, constraints);
        }
        shellHelper.print(simplexIO.printResult(phases).toString());
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
