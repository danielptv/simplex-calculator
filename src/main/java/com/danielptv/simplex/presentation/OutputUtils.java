package com.danielptv.simplex.presentation;

import com.danielptv.simplex.entity.Phase;
import com.danielptv.simplex.entity.Table;
import com.danielptv.simplex.entity.TableDTO;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Utility-Class for displaying output.
 */
@SuppressWarnings("AvoidEscapedUnicodeCharacters")
public final class OutputUtils {
    /**
     * String for resetting font and background colour.
     */
    public static final String STYLE_RESET = "\u001B[0m";
    /**
     * String for setting the font colour to green.
     */
    public static final String FONT_GREEN = "\u001B[32m";
    /**
     * String for setting the font colour to red.
     */
    public static final String FONT_RED = "\u001B[31m";
    /**
     * String for setting the background colour to green.
     */
    public static final String BACKGROUND_GREEN = "\u001B[42m";
    /**
     * String representing the application headline.
     */
    @SuppressWarnings("LineLength")
    public static final String APPLICATION_HEADLINE = """
              
                ______   __                          __                               ______             __          \s
               /      \\ |  \\                        |  \\                             /      \\           |  \\         \s
              |  $$$$$$\\ \\$$ ______ ____    ______  | $$  ______   __    __         |  $$$$$$\\  ______  | $$  _______\s
              | $$___\\$$|  \\|      \\    \\  /      \\ | $$ /      \\ |  \\  /  \\ ______ | $$   \\$$ |      \\ | $$ /       \\
               \\$$    \\ | $$| $$$$$$\\$$$$\\|  $$$$$$\\| $$|  $$$$$$\\ \\$$\\/  $$|      \\| $$        \\$$$$$$\\| $$|  $$$$$$$
               _\\$$$$$$\\| $$| $$ | $$ | $$| $$  | $$| $$| $$    $$  >$$  $$  \\$$$$$$| $$   __  /      $$| $$| $$     \s
              |  \\__| $$| $$| $$ | $$ | $$| $$__/ $$| $$| $$$$$$$$ /  $$$$\\         | $$__/  \\|  $$$$$$$| $$| $$_____\s
               \\$$    $$| $$| $$ | $$ | $$| $$    $$| $$ \\$$     \\|  $$ \\$$\\         \\$$    $$ \\$$    $$| $$ \\$$     \\
                \\$$$$$$  \\$$ \\$$  \\$$  \\$$| $$$$$$$  \\$$  \\$$$$$$$ \\$$   \\$$          \\$$$$$$   \\$$$$$$$ \\$$  \\$$$$$$$
                                          | $$                                                                       \s
                                          | $$                                                                       \s
                                           \\$$                                                                       \s
            """;
    /**
     * String representing the headline for the single-phase Simplex-Algorithm.
     */
    public static final String SIMPLEX_HEADLINE = """

                ______   __                          __                    \s
               /      \\ |  \\                        |  \\                   \s
              |  $$$$$$\\ \\$$ ______ ____    ______  | $$  ______   __    __\s
              | $$___\\$$|  \\|      \\    \\  /      \\ | $$ /      \\ |  \\  /  \\
               \\$$    \\ | $$| $$$$$$\\$$$$\\|  $$$$$$\\| $$|  $$$$$$\\ \\$$\\/  $$
               _\\$$$$$$\\| $$| $$ | $$ | $$| $$  | $$| $$| $$    $$  >$$  $$\s
              |  \\__| $$| $$| $$ | $$ | $$| $$__/ $$| $$| $$$$$$$$ /  $$$$\\\s
               \\$$    $$| $$| $$ | $$ | $$| $$    $$| $$ \\$$     \\|  $$ \\$$\\
                \\$$$$$$  \\$$ \\$$  \\$$  \\$$| $$$$$$$  \\$$  \\$$$$$$$ \\$$   \\$$
                                          | $$                             \s
                                          | $$                             \s
                                           \\$$                             \s
            """;
    /**
     * String representing the headline for the first phase of the Two-Phase-Simplex Method.
     */
    public static final String PHASE_1 = """
              
               _______   __                                              __  \s
              |       \\ |  \\                                           _/  \\ \s
              | $$$$$$$\\| $$____    ______    _______   ______        |   $$ \s
              | $$__/ $$| $$    \\  |      \\  /       \\ /      \\        \\$$$$ \s
              | $$    $$| $$$$$$$\\  \\$$$$$$\\|  $$$$$$$|  $$$$$$\\        | $$ \s
              | $$$$$$$ | $$  | $$ /      $$ \\$$    \\ | $$    $$        | $$ \s
              | $$      | $$  | $$|  $$$$$$$ _\\$$$$$$\\| $$$$$$$$       _| $$_\s
              | $$      | $$  | $$ \\$$    $$|       $$ \\$$     \\      |   $$ \\
               \\$$       \\$$   \\$$  \\$$$$$$$ \\$$$$$$$   \\$$$$$$$       \\$$$$$$
            """;
    /**
     * String representing the headline for the second phase of the Two-Phase-Simplex Method.
     */
    public static final String PHASE_2 = """
              
               _______   __                                             ______ \s
              |       \\ |  \\                                           /      \\\s
              | $$$$$$$\\| $$____    ______    _______   ______        |  $$$$$$\\
              | $$__/ $$| $$    \\  |      \\  /       \\ /      \\        \\$$__| $$
              | $$    $$| $$$$$$$\\  \\$$$$$$\\|  $$$$$$$|  $$$$$$\\       /      $$
              | $$$$$$$ | $$  | $$ /      $$ \\$$    \\ | $$    $$      |  $$$$$$\s
              | $$      | $$  | $$|  $$$$$$$ _\\$$$$$$\\| $$$$$$$$      | $$_____\s
              | $$      | $$  | $$ \\$$    $$|       $$ \\$$     \\      | $$     \\
               \\$$       \\$$   \\$$  \\$$$$$$$ \\$$$$$$$   \\$$$$$$$       \\$$$$$$$$
            """;

    private OutputUtils() {

    }

    /**
     * Method for accentuating the pivot element during output.
     *
     * @param table The corresponding Simplex-Table.
     */
    @SuppressWarnings("ReturnCount")
    public static void accentuatePivot(@NonNull final Table<?> table) {
        if (table.getPivot() == null) {
            return;
        }

        table.getLHS().get(table.getPivot().row()).setPivotPos(table.getPivot().column());
    }

    /**
     * Method for printing a Simplex-Table.
     *
     * @param table The corresponding Simplex-Table.
     * @return The String-Representation of the table as StringBuilder.
     */
    @SuppressWarnings("MagicNumber")
    public static StringBuilder printTable(@NonNull final Table<?> table) {
        setMaxEntryWidths(table);

        final var lHS = table.getLHS();
        final var extendedLHS = table.getExtendedLHS();
        final var rHS = table.getRHS();

        final var rHSWidth = getRhsWidth(rHS);
        final var entryWidths = new ArrayList<>(lHS.get(0).getEntryWidths());
        if (table.getExtendedLHS() != null) {
            entryWidths.addAll(extendedLHS.get(0).getEntryWidths());
        }
        entryWidths.add(rHSWidth);

        final var line = new StringBuilder();
        line.append("  ").append("+--------");
        entryWidths.forEach(e -> line.append("+").append("-".repeat(e + 4)));
        line.append(String.format("+%n"));
        final var totalWidth = line.toString().length();

        final var sb = new StringBuilder();
        sb.append("  ").append("+").append("-".repeat(totalWidth - 6)).append(String.format("+%n"));
        sb.append("  | ");
        sb.append(table.getTitle());
        sb.append(" ".repeat(totalWidth - table.getTitle().length() - 7));
        sb.append(String.format("|%n"));
        sb.append(line);

        sb.append(printBody(table, line, entryWidths));
        sb.append(line);
        return sb;
    }

    /**
     * Method for printing the problem bounds.
     *
     * @param tableDTO TableDTO containing the problem bounds.
     * @return The problem bounds as StringBuilder.
     */
    @SuppressWarnings({"LambdaBodyLength", "MagicNumber"})
    public static StringBuilder printTableDTO(@NonNull final TableDTO<?> tableDTO) {
        final var sb = new StringBuilder();
        @NonNull final var input = tableDTO.getTable();
        final var restrictCount = tableDTO.getConstraintCount();

        sb.append(String.format("%n")).append(FONT_GREEN).append(String.format("  INPUT:%n"));
        sb.append("  ZF: max ");
        IntStream.range(0, input.get(0).size() - 2).forEach(x -> {
            final var pos = input.get(0).size();
            final var number = input.get(0).get(x).contains("/")
                    ? "(" + input.get(0).get(x) + ")"
                    : input.get(0).get(x);
            sb.append(number).append("*").append("x").append(x + 1);
            if (x != pos - 3) {
                sb.append(" + ");
            }
        });
        sb.append(String.format("%n"));

        IntStream.range(1, restrictCount + 1).forEach(x -> {
            sb.append("  R").append(x).append(": ");
            IntStream.range(0, input.get(x).size() - 1).forEach(y -> {
                final var pos = input.get(x).size();
                final var number = input.get(x).get(y).contains("/")
                        ? "(" + input.get(x).get(y) + ")"
                        : input.get(x).get(y);

                if (y == pos - 2) {
                    sb.append(input.get(x).get(pos - 1).equals(">") ? " >= " : " <= ");
                    sb.append(number);
                } else if (y == pos - 3) {
                    sb.append(number).append("*x").append(y + 1);
                } else {
                    sb.append(number).append("*x").append(y + 1).append(" + ");
                }
            });
            sb.append(String.format("%n"));
        });
        sb.append(STYLE_RESET);
        return sb;
    }

    /**
     * Method for printing the final result of a Simplex-Phase.
     *
     * @param phase A Simplex-Phase.
     * @return The final result as StringBuilder.
     */
    @SuppressWarnings("MagicNumber")
    static StringBuilder printPhaseResult(@NonNull final Phase phase) {
        final var sb = new StringBuilder();
        final var table = phase.getTables().get(phase.getTables().size() - 1);

        if (!phase.isSolvable()) {
            sb.append(FONT_RED);
            sb.append(String.format("  The problem has no solution (infeasible).%n"));
            sb.append("  The iterations of the first phase have been completed and there are artificial variables in " +
                    "the base with values strictly greater than 0.");
            sb.append(STYLE_RESET);
            return sb;
        }

        sb.append(FONT_GREEN);
        sb.append(String.format("  OUTPUT:%n"));
        sb.append("  f(x) = ").append(table.getRHS().get(0).toDecimal().toPlainString()).append(String.format("%n"));
        final var variables = table.getColumnHeaders().stream()
                .filter(e -> e.contains("x"))
                .toList();
        final var newRowHeaders = table.getRowHeaders().stream()
                .map(e -> {
                    if (e.length() == "x1[1]".length()) {
                        return e.substring(0, e.length() - 3);
                    }
                    return e;
                })
                .toList();

        variables.forEach(e -> {
            sb.append("  ").append(e).append(" = ");
            if (newRowHeaders.contains(e)) {
                final var index = newRowHeaders.indexOf(e);
                sb.append(table.getRHS().get(index)).append(String.format("%n"));
            } else {
                sb.append(String.format("0%n"));
            }
        });
        sb.append(STYLE_RESET);
        return sb;
    }

    /**
     * Helper-Method for printing a Simplex-Table.
     *
     * @param table       The corresponding Simplex-Table.
     * @param line        A divider-line with length equal to the table width.
     * @param entryWidths A List of maximal entry widths per column.
     * @return The String-Representation of the table body as StringBuilder.
     */
    @SuppressWarnings("LambdaBodyLength")
    static StringBuilder printBody(@NonNull final Table<?> table,
                                   @NonNull final StringBuilder line,
                                   @NonNull final List<Integer> entryWidths) {
        final var lHS = table.getLHS();
        final var extendedLHS = table.getExtendedLHS();
        final var rHS = table.getRHS();
        final var rowHeaders = table.getRowHeaders();
        final var columnHeaders = table.getColumnHeaders();
        final var sb = new StringBuilder();

        sb.append("  | J      ");
        IntStream.range(0, columnHeaders.size())
                .forEach(e -> sb.append(String.format("|  %-" + entryWidths.get(e) + "s  ", columnHeaders.get(e))));
        sb.append(String.format("|%n"));
        sb.append(line);

        IntStream.range(0, rHS.size())
                .forEach(e -> {
                    sb.append(String.format("  | %-6s ", rowHeaders.get(e)));
                    sb.append(lHS.get(e));
                    if (extendedLHS != null) {
                        sb.append(extendedLHS.get(e));
                        sb.append("|");
                        sb.append(String.format("  %-" + getRhsWidth(rHS) + "s  |%n", rHS.get(e)));
                        if (e == 0 || e == 1) {
                            sb.append(line);
                        }
                    } else {
                        sb.append(String.format("|  %-" + getRhsWidth(rHS) + "s  |%n", rHS.get(e)));
                        if (e == 0) {
                            sb.append(line);
                        }
                    }
                });
        return sb;
    }

    /**
     * Helper-Method to get the maximum entry width for the right-hand side of a Simplex-Table.
     *
     * @param rHS The right-hand side.
     * @return The maximum entry width.
     */
    static int getRhsWidth(@NonNull final List<?> rHS) {
        return Collections.max(rHS.stream()
                .map(e -> e.toString().length())
                .toList());
    }

    /**
     * Method for setting the maximal entry widths per column.
     *
     * @param table The corresponding Table.
     */
    static void setMaxEntryWidths(@NonNull final Table<?> table) {
        final var maxEntryWidths = new ArrayList<Integer>();
        for (int i = 0; i < table.getLHS().get(0).getEntries().size(); ++i) {
            final var finalI = i;
            final var maxWidth = Collections.max(table.getLHS().stream()
                    .map(e -> {
                        e.setEntryWidths();
                        return e.getEntryWidths().get(finalI);
                    })
                    .toList());
            maxEntryWidths.add(maxWidth);
        }
        table.getLHS().forEach(e -> e.setEntryWidths(maxEntryWidths));

        if (table.getExtendedLHS() != null) {
            final var extensionMaxEntryWidths = new ArrayList<Integer>();
            for (int i = 0; i < table.getExtensionSize(); ++i) {
                final var finalI = i;
                final var maxWidth = Collections.max(table.getExtendedLHS().stream()
                        .map(e -> {
                            e.setEntryWidths();
                            return e.getEntryWidths().get(finalI);
                        })
                        .toList());
                extensionMaxEntryWidths.add(maxWidth);
            }
            table.getExtendedLHS().forEach(e -> e.setEntryWidths(extensionMaxEntryWidths));
        }
    }

}
