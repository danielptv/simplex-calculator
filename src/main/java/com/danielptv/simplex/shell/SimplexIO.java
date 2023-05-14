package com.danielptv.simplex.shell;

import com.danielptv.simplex.dev.Banner;
import com.danielptv.simplex.entity.Phase;
import com.danielptv.simplex.entity.SpecialSolutionType;
import com.danielptv.simplex.number.CalculableImpl;
import com.danielptv.simplex.shell.table.SimplexTableBuilder;
import com.danielptv.simplex.shell.table.SimplexTableModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class SimplexIO {
    static final String NUMBER_PATTERN = "-?((\\d+(\\.\\d+)?)|(\\d+(/[0-9]*[1-9][0-9]*)?))";
    private final ShellHelper shellHelper;
    private final InputReader inputReader;
    private final SimplexTableBuilder tableBuilder;

    public List<String> getObjectiveFunction(final int varCount) {
        final var pattern = "^" + NUMBER_PATTERN + ("," + NUMBER_PATTERN).repeat(varCount - 1) + "$";
        final var answer = inputReader.promptUntilValid("Objective function", pattern);
        return new ArrayList<>(Arrays.stream(answer.split(",")).toList());
    }

    public List<List<String>> getConstraints(final int varCount, final int constraintCount) {
        final var pattern1 = "^" + NUMBER_PATTERN + ("," + NUMBER_PATTERN).repeat(varCount) + "$";
        final var pattern2 = "^" + NUMBER_PATTERN + ("," + NUMBER_PATTERN).repeat(varCount - 1) +
                "[<>=]" + NUMBER_PATTERN + "$";
        final var result = new ArrayList<List<String>>();

        for (int i = 1; i < constraintCount + 1; ++i) {
            final var answer = inputReader.promptUntilValid("R" + i, pattern1, pattern2);
            if (answer.matches(pattern1)) {
                final var restriction = new ArrayList<>(Arrays.stream(answer.split(",")).toList());
                restriction.add("<");
                result.add(restriction);
                continue;
            }
            if (answer.matches(pattern2)) {
                final var sign = answer.contains(">") ? ">" : answer.contains("<") ? "<" : "=";
                final var newIn = answer.contains(">")
                        ? answer.replace(">", ",")
                        : answer.contains("<")
                        ? answer.replace("<", ",")
                        : answer.replace("=", ",");
                final var restriction = new ArrayList<>(Arrays.stream(newIn.split(",")).toList());
                restriction.add(sign);
                result.add(restriction);
                continue;
            }
            throw new IllegalArgumentException();
        }
        return result;
    }

    @SuppressWarnings({"CyclomaticComplexity", "NPathComplexity", "LambdaBodyLength", "MagicNumber"})
    public StringBuilder displayProblem(
            final List<String> objectiveFunction,
            final List<List<String>> constraints,
            final boolean minimize
    ) {
        final var sb = new StringBuilder();
        sb.append(String.format("%nINPUT%n"));
        sb.append(String.format(minimize ? "min f(x) = " : "max f(x) = "));
        IntStream.range(0, objectiveFunction.size()).forEach(value -> {
            final var number = objectiveFunction.get(value).contains("/") || objectiveFunction.get(value).contains("-")
                    ? "(" + objectiveFunction.get(value) + ")"
                    : objectiveFunction.get(value);
            sb.append(number).append("•").append(String.format("x%s", SubscriptNumbers.values()[value + 1]));
            if (value < objectiveFunction.size() - 1) {
                sb.append(" + ");
            }
        });
        sb.append(String.format("%n")).append(String.format("%n"));
        IntStream.range(0, constraints.size()).forEach(constraint -> {
            IntStream.range(0, constraints.get(0).size() - 1).forEach(value -> {
                final var pos = constraints.get(0).size();
                final var number = constraints.get(constraint).get(value).contains("/") ||
                        constraints.get(constraint).get(value).contains("-")
                        ? "(" + constraints.get(constraint).get(value) + ")"
                        : constraints.get(constraint).get(value);
                sb.append(number);

                if (value < pos - 3) {
                    sb.append("•").append(String.format("x%s", SubscriptNumbers.values()[value + 1])).append(" + ");
                }
                if (value == pos - 3) {
                    sb.append("•").append(String.format("x%s", SubscriptNumbers.values()[value + 1])).append(" ");
                    final var relationSign = constraints.get(constraint).get(constraints.get(constraint).size() - 1);
                    sb.append(relationSign.equals("=") ? "=" : relationSign.equals("<") ? "≤" : "≥").append(" ");
                }
            });
            sb.append(String.format("%n"));
        });
        IntStream.range(1, objectiveFunction.size()).forEach(variable -> sb.append("x")
                .append(SubscriptNumbers.values()[variable])
                .append(","));
        sb.append("x").append(SubscriptNumbers.values()[objectiveFunction.size()]);
        sb.append(String.format(" ≥ 0%n"));
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
                sb.append(shellHelper.getErrorMessage(specialSolution.toString()));
                return sb;
            }
            sb.append(shellHelper.getWarningMessage(specialSolution.toString()));
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
            final var variableNumber = SubscriptNumbers.values()[Integer.parseInt(String.valueOf(e.charAt(1)))];
            solution.append(String.format("x%s˟ = ", variableNumber));
            if (newRowHeaders.contains(e)) {
                final var index = newRowHeaders.indexOf(e);
                solution.append(lastTable.rHS().get(index)).append(String.format("%n"));
            } else {
                solution.append(String.format("0%n"));
            }
        });
        sb.append(shellHelper.getSuccessMessage(solution.toString()));
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
