package com.danielptv.simplex.presentation;

import com.danielptv.simplex.entity.Row;
import com.danielptv.simplex.number.CalculableImpl;
import com.danielptv.simplex.entity.Phase;
import com.danielptv.simplex.entity.Table;
import com.danielptv.simplex.entity.TableDTO;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static com.danielptv.simplex.entity.SpecialSolutionType.MULTIPLE_SOLUTIONS;

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
     * Accentuate the pivot element during output.
     *
     * @param table The table.
     */
    @SuppressWarnings("ReturnCount")
    public static void accentuatePivot(@NonNull final Table<?> table) {
        table.lHS().get(table.pivot().row()).setPivotPos(table.pivot().column());
    }

    /**
     * Print a table.
     *
     * @param table The table.
     * @return The table as StringBuilder.
     */
    @SuppressWarnings("MagicNumber")
    public static StringBuilder printTable(@NonNull final Table<?> table) {
        setMaxEntryWidths(table);

        final var lHS = table.lHS();
        final var rHS = table.rHS();

        final var rHSWidth = getRhsWidth(rHS);
        final var entryWidths = new ArrayList<>(lHS.get(0).getEntryWidths());
        entryWidths.add(rHSWidth);

        final var line = new StringBuilder();
        line.append("  ").append("+--------");
        entryWidths.forEach(e -> line.append("+").append("-".repeat(e + 4)));
        line.append(String.format("+%n"));
        final var totalWidth = line.toString().length();

        final var sb = new StringBuilder();
        sb.append("  ").append("+").append("-".repeat(totalWidth - 6)).append(String.format("+%n"));
        sb.append("  | ");
        sb.append(table.title());
        sb.append(" ".repeat(totalWidth - table.title().length() - 7));
        sb.append(String.format("|%n"));
        sb.append(line);

        sb.append(printBody(table, line, entryWidths));
        sb.append(line);
        return sb;
    }

    /**
     * Print the problem bounds.
     *
     * @param tableDTO TableDTO containing the problem bounds.
     * @return The problem bounds as StringBuilder.
     */
    @SuppressWarnings({"LambdaBodyLength", "MagicNumber"})
    public static StringBuilder printTableDTO(@NonNull final TableDTO<?> tableDTO) {
        final var sb = new StringBuilder();
        @NonNull final var input = tableDTO.table();
        final var restrictCount = tableDTO.constraintCount();

        sb.append(String.format("%n")).append(FONT_GREEN).append(String.format("  INPUT:%n"));
        sb.append("  ZF: max ");
        IntStream.range(0, input.get(0).size() - 2).forEach(x -> {
            final var pos = input.get(0).size();
            final var number = input.get(0).get(x).contains("/") || input.get(0).get(x).contains("-")
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
                final var number = input.get(x).get(y).contains("/") || input.get(x).get(y).contains("-")
                        ? "(" + input.get(x).get(y) + ")"
                        : input.get(x).get(y);

                if (y == pos - 2) {
                    final var relationSign = input.get(x).get(pos - 1);
                    sb.append(relationSign.equals(">") ? " >= " : relationSign.equals("<") ? " <= " : " = ");
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
     * Print the final result of a simplex phase.
     *
     * @param phase A simplex phase.
     * @param <T>   Fraction or RoundedDecimal
     * @return The final result as StringBuilder.
     */
    @SuppressWarnings("MagicNumber")
    static <T extends CalculableImpl<T>> StringBuilder printPhaseResult(@NonNull final Phase<T> phase) {
        final var sb = new StringBuilder();
        final var table = phase.tables().get(phase.tables().size() - 1);

        if (phase.specialSolutionType() != null && phase.specialSolutionType() != MULTIPLE_SOLUTIONS) {
            sb.append(FONT_RED);
            sb.append(phase.specialSolutionType());
            sb.append(STYLE_RESET);
            return sb;
        }

        sb.append(FONT_GREEN);
        sb.append(phase.specialSolutionType() != null
                ? phase.specialSolutionType()
                : String.format("  The optimal solution is:%n"));
        sb.append("  f(x) = ").append(table.rHS().get(0).toDecimal().toPlainString()).append(String.format("%n"));
        final var variables = table.columnHeaders().stream()
                .filter(e -> e.contains("x"))
                .toList();
        final var newRowHeaders = table.rowHeaders().stream()
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
                sb.append(table.rHS().get(index)).append(String.format("%n"));
            } else {
                sb.append(String.format("0%n"));
            }
        });
        sb.append(STYLE_RESET);
        return sb;
    }

    /**
     * Print the body of a table.
     *
     * @param table       The table.
     * @param line        A divider-line with length equal to the table width.
     * @param entryWidths A list of maximal entry widths per column.
     * @return The table body as StringBuilder.
     */
    @SuppressWarnings("LambdaBodyLength")
    static StringBuilder printBody(@NonNull final Table<?> table,
                                   @NonNull final StringBuilder line,
                                   @NonNull final List<Integer> entryWidths) {
        final var lHS = table.lHS();
        final var rHS = table.rHS();
        final var rowHeaders = table.rowHeaders();
        final var columnHeaders = table.columnHeaders();
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
                    sb.append(String.format("|  %-" + getRhsWidth(rHS) + "s  |%n", rHS.get(e)));
                    if (e == 0 || e == 1 && table.helperColumns() != 0) {
                        sb.append(line);
                    }
                });
        return sb;
    }

    /**
     * Get the maximum entry width for the right-hand side of a table.
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
     * Set the maximal entry widths per column within a table.
     *
     * @param table The table.
     */
    static void setMaxEntryWidths(@NonNull final Table<?> table) {
        table.lHS().forEach(Row::setEntryWidths);
        final var maxEntryWidths = IntStream.range(0, table.columns())
                .mapToObj(i -> Collections.max(table.lHS().stream()
                        .map(entry -> entry.getEntryWidths().get(i))
                        .toList()))
                .toList();
        table.lHS().forEach(e -> e.setEntryWidths(maxEntryWidths));
    }
}
