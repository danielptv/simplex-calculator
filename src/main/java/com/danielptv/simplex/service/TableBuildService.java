package com.danielptv.simplex.service;

import com.danielptv.simplex.number.CalculableImpl;
import com.danielptv.simplex.entity.Row;
import com.danielptv.simplex.entity.Table;
import com.danielptv.simplex.entity.TableDTO;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.danielptv.simplex.service.TableCalcService.getNegativeRows;
import static com.danielptv.simplex.service.TableCalcService.setPivot;

/**
 * Service-Class for building Simplex-Tables.
 */
public final class TableBuildService {
    private TableBuildService() {

    }

    /**
     * Method for building a Table.
     *
     * @param tableDTO DTO containing problem bounds and String-representation of the table.
     * @param <T>      Fraction or RoundedDecimal.
     * @return A Simplex-Table.
     */
    public static <T extends CalculableImpl<T>> Table<T> build(@NonNull final TableDTO<T> tableDTO) {

        if (tableDTO.table() == null) {
            throw new IllegalArgumentException();
        }
        final var inst = tableDTO.inst();
        final var table = buildTable(tableDTO);
        final var varCount = tableDTO.variablesCount();
        final var constraintCount = tableDTO.constraintCount();
        final var rHS = buildRHS(new ArrayList<>(table));
        final var lHS = buildLHS(new ArrayList<>(table), inst);
        final var columnHeaders = buildColumnHeaders(varCount, constraintCount);
        final var rowHeadersTemp = buildRowHeaders(constraintCount, getNegativeRows(rHS, inst));
        final var rowHeaders = enumerateRowHeaders(rowHeadersTemp, columnHeaders);
        final var pivot = setPivot(lHS, rHS, false, inst);

        return new Table<>(inst, " ", lHS, null, rHS, pivot, columnHeaders, rowHeaders, 0);
    }

    /**
     * Helper-Method for building the left-hand side of a Table.
     *
     * @param table String-representation of the table.
     * @param inst  Fraction or RoundedDecimal.
     * @param <T>   Fraction or RoundedDecimal.
     * @return The left-hand side.
     */
    static <T extends CalculableImpl<T>> @NonNull List<Row<T>> buildLHS(@NonNull final ArrayList<ArrayList<T>> table,
                                                                        @NonNull final T inst) {
        return table.stream()
                .map(row -> {
                    row.remove(row.size() - 1);
                    return new Row<>(row, inst);
                })
                .toList();
    }

    /**
     * Helper-Method for building the right-hand side of a Simplex-Table.
     *
     * @param table String-representation of the table.
     * @param <T>   Fraction or RoundedDecimal.
     * @return The right-hand side.
     */
    static <T extends CalculableImpl<T>> @NonNull List<T> buildRHS(@NonNull final ArrayList<ArrayList<T>> table) {
        return table.stream()
                .map(row -> {
                    final var size = row.size();
                    return row.get(size - 1);
                })
                .toList();
    }

    /**
     * Helper-Method for building the column headers.
     *
     * @param varCount        Number of variables.
     * @param constraintCount Number of constraints.
     * @return The column headers as List of Strings.
     */
    static @NonNull List<String> buildColumnHeaders(final int varCount, final int constraintCount) {
        return IntStream.range(1, varCount + constraintCount + 2)
                .mapToObj(i -> {
                    if (i < varCount + 1) {
                        return "x" + i;
                    }
                    if (i == varCount + constraintCount + 1) {
                        return "f";
                    }
                    return "s" + (i - varCount);
                })
                .toList();
    }

    /**
     * Helper-Method for building the row headers.
     *
     * @param constraintCount Number of constraints.
     * @param negativeRows    Indices of all rows with negative right-hand side.
     * @return The row headers.
     */
    static @NonNull List<String> buildRowHeaders(final int constraintCount,
                                                 @NonNull final List<Integer> negativeRows) {
        return IntStream.range(0, constraintCount + 1)
                .mapToObj(i -> {
                    if (i == 0) {
                        return "z";
                    }
                    if (negativeRows.contains(i)) {
                        return "h" + (negativeRows.indexOf(i) + 1);
                    }
                    return "s" + i;
                })
                .toList();
    }

    /**
     * Helper-Method for building an ArrayList representation of a Simplex-Table.
     *
     * @param tableDTO Table as DTO containing the problem bounds.
     * @param <T>      Fraction or RoundedDecimal.
     * @return The Simplex-Table as ArrayList.
     */
    @SuppressWarnings({"MagicNumber", "CyclomaticComplexity", "NPathComplexity"})
    static <T extends CalculableImpl<T>> ArrayList<ArrayList<T>> buildTable(@NonNull final TableDTO<T> tableDTO) {
        final var inst = tableDTO.inst();
        final var minusOne = inst.create("-1");
        final var input = tableDTO.table();

        // create rows
        final var table = new ArrayList<>(input.stream()
                .map(x -> new ArrayList<T>())
                .toList());

        // fill with zeroes
        for (var row : table) {
            for (int i = 0; i < input.size() - 2 + input.get(0).size(); ++i) {
                row.add(inst);
            }
        }

        // feed values
        for (int row = 0; row < input.size(); ++row) {

            final var relationSign = input.get(row).get(input.get(row).size() - 1);

            for (int column = 0; column < input.get(1).size() - 2; ++column) {
                // objective function values
                final var currentVal = input.get(row).get(column);
                if (row == 0) {
                    table.get(0).set(column, inst.create(input.get(0).get(column)).multiply(minusOne));
                } else if (relationSign.equals(">") || relationSign.equals("=")) {
                    table.get(row).set(column, inst.create(currentVal).multiply(minusOne));
                } else {
                    table.get(row).set(column, inst.create(currentVal));
                }
            }

            // unit matrix
            for (int i = input.get(0).size() - 2; i < input.get(0).size() + input.size() - 1; ++i) {
                if (row == i - input.get(0).size() + 3 && !relationSign.equals("=")) {
                    table.get(row).set(i, inst.create("1"));
                }
            }

            // right side values
            var rHS = inst.create(input.get(row).get(input.get(row).size() - 2));
            if (row == 0) {
                rHS = inst.create("0");
            }
            if (relationSign.equals(">") || relationSign.equals("=")) {
                rHS = rHS.multiply(inst.create("-1"));
            }
            table.get(row).set(table.get(row).size() - 1, rHS);
        }
        return table;
    }

    /**
     * Helper-Method for enumerating row headers based on the column the restriction is in.
     *
     * @param rowHeaders    The row headers to be enumerated.
     * @param columnHeaders The column headers.
     * @return The enumerated row headers.
     */
    static List<String> enumerateRowHeaders(@NonNull final List<String> rowHeaders,
                                            @NonNull final List<String> columnHeaders) {
        final var count = new AtomicInteger();
        return rowHeaders.stream()
                .map(e -> {
                    if (e.contains("h")) {
                        return e + "[" + (columnHeaders.size() + count.getAndIncrement()) + "]";
                    }
                    if (!columnHeaders.contains(e)) {
                        return e;
                    }
                    return e + "[" + (columnHeaders.indexOf(e) + 1) + "]";
                })
                .toList();
    }
}
