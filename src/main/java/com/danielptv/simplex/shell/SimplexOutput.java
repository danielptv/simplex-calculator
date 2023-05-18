package com.danielptv.simplex.shell;

import com.danielptv.simplex.dev.Banner;
import com.danielptv.simplex.entity.Phase;
import com.danielptv.simplex.entity.SpecialSolutionType;
import com.danielptv.simplex.number.CalculableImpl;
import com.danielptv.simplex.shell.table.SimplexTableBuilder;
import com.danielptv.simplex.shell.table.SimplexTableModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class SimplexOutput {
    private final OutputHelper outputHelper;
    private final SimplexTableBuilder tableBuilder;

    public StringBuilder displayProblem(
            final InputResult objectiveFunction,
            final List<InputResult> constraints
    ) {
        final var sb = new StringBuilder();
        sb.append(String.format("%nINPUT%n"));
        sb.append(String.format("%s%n%n", objectiveFunction));
        constraints.forEach(c -> sb.append(String.format("%s%n", c)));

        IntStream.range(1, objectiveFunction.getValues().size()).forEach(variable ->
                sb.append(String.format("x%s,", SubscriptNumbers.toValue(variable))));
        sb.append(String.format("x%s ≥ 0%n", SubscriptNumbers.toValue(objectiveFunction.getValues().size())));
        return sb;
    }

    @SuppressWarnings({"MagicNumber", "LambdaBodyLength"})
    public StringBuilder printResult(final List<Phase<? extends CalculableImpl<?>>> result) {
        final var sb = new StringBuilder();
        result.forEach(phase -> {
            final var phaseTitle = phase.singlePhase()
                    ? Banner.getFiglet("Simplex")
                    : result.indexOf(phase) == 0
                    ? Banner.getFiglet("Phase 1")
                    : Banner.getFiglet("Phase 2");
            sb.append(phaseTitle).append(String.format("%n"));

            final var phaseModels = phase.tables().stream()
                    .map(table -> new SimplexTableModel(table, !table.equals(phase.getLastTable())))
                    .toList();
            final var phaseColumnWidths = getPhaseColumnsMaxWidths(phaseModels);

            IntStream.range(0, phaseModels.size()).forEach(phaseTable -> {
                sb.append(phase.tables().get(phaseTable).title()).append(String.format("%n"));
                final var model = phaseModels.get(phaseTable);
                final var twoObjFunc = phase.getLastTable().rows() >
                        result.get(result.size() - 1).getLastTable().rows();
                final var printTable = tableBuilder.build(model, twoObjFunc, phaseColumnWidths);
                sb.append(printTable.render(100)).append(String.format("%n"));
            });
        });

        // special solutions
        final var specialSolution = result.get(result.size() - 1).specialSolutionType();
        if (specialSolution != null) {
            if (!specialSolution.equals(SpecialSolutionType.MULTIPLE_SOLUTIONS)) {
                sb.append(outputHelper.getErrorMessage(specialSolution.toString()));
                return sb;
            }
            sb.append(outputHelper.getWarningMessage(specialSolution.toString()));
        }

        // optimal solution
        // objective function
        final var solution = new StringBuilder();
        final var lastTable = result.get(result.size() - 1).getLastTable();
        solution.append("OPTIMAL SOLUTION").append(String.format("%n"));
        solution.append("f(x)˟ = ").append(lastTable.rHS().get(0).toDecimal().toPlainString());
        solution.append(String.format("%n"));

        // variables
        final var variables = lastTable.columnHeaders().stream()
                .filter(e -> e.contains("x"))
                .toList();
        final var newRowHeaders = lastTable.rowHeaders().stream()
                .map(e -> {
                    if (e.length() == "x1[1]".length()) {
                        return e.substring(0, e.length() - 3);
                    }
                    return e;
                })
                .toList();
        variables.forEach(e -> {
            final var variableNumber = SubscriptNumbers.toValue(Integer.parseInt(String.valueOf(e.charAt(1))));
            solution.append(String.format("x%s˟ = ", variableNumber));
            if (newRowHeaders.contains(e)) {
                final var index = newRowHeaders.indexOf(e);
                solution.append(lastTable.rHS().get(index)).append(String.format("%n"));
            } else {
                solution.append(String.format("0%n"));
            }
        });
        sb.append(outputHelper.getSuccessMessage(solution.toString()));
        return sb;
    }

    int[] getPhaseColumnsMaxWidths(final List<SimplexTableModel> models) {
        final var result = new int[models.get(0).getColumnCount()];
        Arrays.fill(result, 0);
        final var widths = models.stream()
                .map(SimplexTableModel::getColumnsMaxWidths)
                .toList();
        widths.forEach(tableWidths -> {
            for (int columnWidth = 0; columnWidth < tableWidths.length; columnWidth++) {
                result[columnWidth] = Math.max(tableWidths[columnWidth], result[columnWidth]);
            }
        });
        return result;
    }
}
