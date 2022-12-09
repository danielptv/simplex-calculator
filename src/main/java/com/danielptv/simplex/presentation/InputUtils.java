package com.danielptv.simplex.presentation;

import com.danielptv.simplex.entity.CalculableImpl;
import com.danielptv.simplex.entity.Fraction;
import com.danielptv.simplex.entity.RoundedDecimal;
import com.danielptv.simplex.entity.TableDTO;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static com.danielptv.simplex.entity.Fraction.FRACTION_PATTERN;
import static com.danielptv.simplex.entity.RoundedDecimal.ROUNDED_DECIMAL_PATTERN;
import static com.danielptv.simplex.presentation.OutputUtils.ANSI_RED;
import static com.danielptv.simplex.presentation.OutputUtils.ANSI_RESET;
import static java.lang.Integer.parseInt;
import static java.lang.System.out;

/**
 * Utility-Class for parsing user input.
 */
public final class InputUtils {
    private InputUtils() {
    }

    /**
     * Method for parsing user input into a TableDTO.
     *
     * @param tableDTO TableDTO containing problem bounds.
     * @param <T>      Fraction or RoundedDecimal.
     * @return TableDTO containing problem bounds and table parsed from user input.
     */
    static <T extends CalculableImpl<T>> @NonNull TableDTO<T> parseInput(@NonNull final TableDTO<T> tableDTO) {

        final var pattern = tableDTO.getPattern();
        final var varCount = tableDTO.getVariablesCount();
        final var restrictCount = tableDTO.getConstraintCount();
        final var input = new ArrayList<List<String>>();

        input.add(getObjectiveFunc(varCount, pattern));
        input.addAll(getRestrictions(varCount, restrictCount, pattern));

        return new TableDTO<>(tableDTO.getInst(), varCount, restrictCount, pattern, input);
    }

    /**
     * Method for getting problem bounds from user input.
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
                out.print(ANSI_RED + "  Rounded results? [y/n] " + ANSI_RESET);
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
                    out.print(ANSI_RED + "  Mantissa length: " + ANSI_RESET);
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
            return new TableDTO<>(new Fraction(), varCount, constraintCount, FRACTION_PATTERN);
        }
        return new TableDTO<>(new RoundedDecimal(mantissaLength), varCount, constraintCount, ROUNDED_DECIMAL_PATTERN);
    }

    /**
     * Helper-Method for getting the number of variables or constraints from user input.
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
                out.print(ANSI_RED + "  Number of " + varOrConstraint + ": " + ANSI_RESET);
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
     * Helper-Method for getting the objective function from user input.
     *
     * @param varCount The expected Number of variables.
     * @param regEx    RegEx pattern for the expected input datatype.
     * @return The objective function as List of Strings.
     */
    static List<String> getObjectiveFunc(final int varCount, @NonNull final String regEx) {
        final var scan = new Scanner(System.in);
        final var pattern = "^" + regEx + ("," + regEx).repeat(varCount - 1) + "$";
        final ArrayList<String> result;

        var falseInputCount = 0;
        while (true) {
            if (falseInputCount > 0) {
                out.print(ANSI_RED + "  Objective function: " + ANSI_RESET);
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
     * Helper-Method for getting the restrictions from user input.
     *
     * @param varCount      The expected number of variables.
     * @param restrictCount The expected number of restrictions.
     * @param regEx         RegEx pattern for the expected input datatype.
     * @return The restrictions as a List of Strings.
     */
    static List<List<String>> getRestrictions(final int varCount, final int restrictCount,
                                              @NonNull final String regEx) {
        final var scan = new Scanner(System.in);
        final var pattern1 = "^" + regEx + ("," + regEx).repeat(varCount) + "$";
        final var pattern2 = "^" + regEx + ("," + regEx).repeat(varCount - 1) + "[<>]" + regEx + "$";
        final var result = new ArrayList<List<String>>();

        for (int i = 1; i < restrictCount + 1; ++i) {
            var falseInputCount = 0;
            while (true) {
                if (falseInputCount > 0) {
                    out.print(ANSI_RED + "  R" + i + ": " + ANSI_RESET);
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
                    final var sign = in.contains(">") ? ">" : "<";
                    final var newIn = in.contains(">") ? in.replace(">", ",") : in.replace("<", ",");
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
