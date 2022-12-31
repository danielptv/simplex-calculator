package com.danielptv.simplex.presentation;

import com.danielptv.simplex.number.CalculableImpl;
import com.danielptv.simplex.number.Fraction;
import com.danielptv.simplex.number.RoundedDecimal;
import com.danielptv.simplex.entity.TableDTO;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static com.danielptv.simplex.presentation.OutputUtils.FONT_RED;
import static com.danielptv.simplex.presentation.OutputUtils.STYLE_RESET;
import static java.lang.Integer.parseInt;
import static java.lang.System.out;

/**
 * Utility-Class for parsing user input.
 */
public final class InputUtils {
    static final String NUMBER_PATTERN = "-?((\\d+(\\.\\d+)?)|(\\d+(/[0-9]*[1-9][0-9]*)?))";
    private InputUtils() {
    }

    /**
     * Parse user input into a TableDTO.
     *
     * @param tableDTO TableDTO containing problem bounds.
     * @param <T>      Fraction or RoundedDecimal.
     * @return TableDTO containing problem bounds and table parsed from user input.
     */
    static <T extends CalculableImpl<T>> @NonNull TableDTO<T> parseInput(@NonNull final TableDTO<T> tableDTO) {

        final var varCount = tableDTO.variablesCount();
        final var restrictCount = tableDTO.constraintCount();
        final var input = new ArrayList<List<String>>();

        out.println();
        input.add(getObjectiveFunc(varCount));
        input.addAll(getRestrictions(varCount, restrictCount));

        return new TableDTO<>(tableDTO.inst(), input, varCount, restrictCount);
    }

    /**
     * Get the problem bounds from user input.
     *
     * @return TableDTO containing bounds necessary for building the table.
     */
    @SuppressWarnings({"NPathComplexity", "CyclomaticComplexity", "ExecutableStatementCount"})
    static @NonNull TableDTO<?> getProblemBounds() {
        final var scan = new Scanner(System.in);
        final String calcMode;
        int mantissaLength = 0;

        var falseInputCount = 0;
        while (true) {
            if (falseInputCount > 0) {
                out.print(FONT_RED + "  Rounded results? [y/n] " + STYLE_RESET);
            } else {
                out.print("  Rounded results? [y/n] ");
            }
            final var in = scan.nextLine();
            if (in.matches("^[yn]$")) {
                calcMode = in;
                break;
            }
            falseInputCount++;
        }

        if ("y".equals(calcMode)) {
            falseInputCount = 0;
            while (true) {
                if (falseInputCount > 0) {
                    out.print(FONT_RED + "  Mantissa length: " + STYLE_RESET);
                } else {
                    out.print("  Mantissa length: ");
                }
                final var in = scan.nextLine();
                if (in.matches("^\\d$")) {
                    mantissaLength = parseInt(in);
                    break;
                }
                falseInputCount++;
            }
        }

        final var varCount = getCount("variables");
        final var constraintCount = getCount("constraints");
        if ("n".equals(calcMode)) {
            return new TableDTO<>(new Fraction(), varCount, constraintCount);
        }
        return new TableDTO<>(new RoundedDecimal(mantissaLength), varCount, constraintCount);
    }

    /**
     * Get the number of variables or constraints from user input.
     *
     * @param varOrConstraint String "variables" or "restrictions"
     * @return The number of variables or constraints.
     */
    static int getCount(@NonNull final String varOrConstraint) {
        final var scan = new Scanner(System.in);
        final var modePattern = "^\\d$";
        int count;
        var falseInputCount = 0;

        while (true) {
            if (falseInputCount > 0) {
                out.print(FONT_RED + "  Number of " + varOrConstraint + ": " + STYLE_RESET);
            } else {
                out.print("  Number of " + varOrConstraint + ": ");
            }
            final var in = scan.nextLine();
            if (in.matches(modePattern)) {
                count = parseInt(in);
                break;
            }
            falseInputCount++;
        }
        return count;
    }

    /**
     * Get the objective function from user input.
     *
     * @param varCount The expected Number of variables.
     * @return The objective function as List of Strings.
     */
    static List<String> getObjectiveFunc(final int varCount) {
        final var scan = new Scanner(System.in);
        final var pattern = "^" + NUMBER_PATTERN + ("," + NUMBER_PATTERN).repeat(varCount - 1) + "$";
        final ArrayList<String> result;

        var falseInputCount = 0;
        while (true) {
            if (falseInputCount > 0) {
                out.print(FONT_RED + "  Objective function: " + STYLE_RESET);
            } else {
                out.print("  Objective function: ");
            }
            final var in = scan.nextLine();
            if (in.matches(pattern)) {
                final var objectiveFunc = new ArrayList<>(Arrays.stream(in.split(",")).toList());
                objectiveFunc.add("0");
                objectiveFunc.add("max");
                result = objectiveFunc;
                break;
            }
            falseInputCount++;
        }
        return result;
    }

    /**
     * Get the restrictions from user input.
     *
     * @param varCount      The expected number of variables.
     * @param restrictCount The expected number of restrictions.
     * @return The restrictions as a List of Strings.
     */
    static List<List<String>> getRestrictions(final int varCount, final int restrictCount) {
        final var scan = new Scanner(System.in);
        final var pattern1 = "^" + NUMBER_PATTERN + ("," + NUMBER_PATTERN).repeat(varCount) + "$";
        final var pattern2 = "^" + NUMBER_PATTERN + ("," + NUMBER_PATTERN).repeat(varCount - 1) +
                "[<>=]" + NUMBER_PATTERN + "$";
        final var result = new ArrayList<List<String>>();

        for (int i = 1; i < restrictCount + 1; ++i) {
            var falseInputCount = 0;
            while (true) {
                if (falseInputCount > 0) {
                    out.print(FONT_RED + "  R" + i + ": " + STYLE_RESET);
                } else {
                    out.print("  R" + i + ": ");
                }
                final var in = scan.nextLine();
                if (in.matches(pattern1)) {
                    final var restriction = new ArrayList<>(Arrays.stream(in.split(",")).toList());
                    restriction.add("<");
                    result.add(restriction);
                    break;
                }
                if (in.matches(pattern2)) {
                    final var sign = in.contains(">") ? ">" : in.contains("<") ? "<" : "=";
                    final var newIn = in.contains(">")
                                    ? in.replace(">", ",")
                                    : in.contains("<")
                                    ? in.replace("<", ",")
                                    : in.replace("=", ",");
                    final var restriction = new ArrayList<>(Arrays.stream(newIn.split(",")).toList());
                    restriction.add(sign);
                    result.add(restriction);
                    break;
                }
                falseInputCount++;
            }
        }
        return result;
    }
}
