package com.danielptv.simplex.service;

import com.danielptv.simplex.number.CalculableImpl;
import com.danielptv.simplex.entity.Row;
import com.danielptv.simplex.entity.Table;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.danielptv.simplex.service.TableCalcService.getNegativeRows;
import static com.danielptv.simplex.service.TableCalcService.setPivot;

/**
 * Service-Class for adding/removing the extended left-hand side of a Two-Phase-Simplex-Table.
 */
final class TableExtensionService {
    private TableExtensionService() {

    }

    /**
     * Method for extending the left-hand side of a Two-Phase-Simplex-Table.
     *
     * @param table A not-extended Table.
     * @param <T>   Fraction or RoundedDecimal.
     * @return The extended Table.
     * @throws UnsupportedOperationException When the input table is already extended.
     */
    static <T extends CalculableImpl<T>> Table<T> buildExtension(@NonNull final Table<T> table) {
        if (table.extendedLHS() != null) {
            throw new UnsupportedOperationException("Table is already extended!");
        }

        final var inst = table.inst();
        final var negativeRows = getNegativeRows(table.rHS(), inst);
        var lHS = invertNegativeRowsLHS(table.lHS(), negativeRows);
        var rHS = invertNegativeRowsRHS(table.rHS(), negativeRows, inst);
        lHS = addCriterionLineLHS(lHS, inst);
        rHS = addCriterionLineRHS(rHS, inst);
        final var extendedLHS = addExtensionColumns(inst, negativeRows, rHS.size());
        final var extensionSize = negativeRows.size();

        final var columnHeaders = new ArrayList<>(table.columnHeaders());
        IntStream.range(1, extensionSize + 1)
                .forEach(i -> {
                    final var size = columnHeaders.size();
                    columnHeaders.add(size - 1, "h" + i);
                });
        final var rowHeaders = new ArrayList<>(table.rowHeaders());
        rowHeaders.add(0, "z'");

        final var pivot = setPivot(lHS, rHS, true, inst);

        return new Table<>(
                inst,
                table.title(),
                lHS,
                extendedLHS,
                rHS,
                pivot,
                columnHeaders,
                rowHeaders,
                extensionSize);
    }

    /**
     * Method for removing the extended left-hand side of a Two-Phase-Simplex-Table.
     *
     * @param table An extended Table.
     * @param <T>   Fraction or RoundedDecimal.
     * @return A not-extended Table.
     * @throws UnsupportedOperationException When the input table is not extended.
     */
    static <T extends CalculableImpl<T>> Table<T> removeExtension(@NonNull final Table<T> table) {
        if (table.extendedLHS() == null) {
            throw new UnsupportedOperationException("Table is not extended!");
        }

        final var inst = table.inst();
        final var lHS = new ArrayList<>(table.lHS());
        lHS.remove(0);
        final var rHS = new ArrayList<>(table.rHS());
        rHS.remove(0);
        final var pivot = setPivot(lHS, rHS, false, inst);

        final var columnHeaders = new ArrayList<>(table.columnHeaders());
        IntStream.range(0, table.extensionSize())
                .forEach(i -> {
                    final var size = columnHeaders.size();
                    columnHeaders.remove(size - 2);
                });
        final var rowHeaders = new ArrayList<>(table.rowHeaders());
        rowHeaders.remove(0);
        return new Table<>(
                inst,
                table.title(),
                lHS,
                null,
                rHS,
                pivot,
                columnHeaders,
                rowHeaders,
                0
        );
    }

    /**
     * Helper-Method for adding columns to the extended left-hand side of the table.
     *
     * @param inst         Fraction or RoundedDecimal.
     * @param negativeRows Indices of all rows with negativ right-hand side.
     * @param rowCount     Total number of rows of the table.
     * @param <T>          Fraction or RoundedDecimal.
     * @return The extended left-hand side.
     */
    static <T extends CalculableImpl<T>> @NonNull List<Row<T>> addExtensionColumns(
            @NonNull final T inst,
            @NonNull final List<Integer> negativeRows,
            final int rowCount) {
        var extendedLHS = IntStream.range(0, rowCount)
                .mapToObj(i -> {
                    if (i == 0 || i == negativeRows.get(0) + 1) {
                        return new Row<>(List.of(inst.create("1")), inst);
                    }
                    return new Row<>(List.of(inst.create("0")), inst);
                })
                .toList();
        extendedLHS = new ArrayList<>(extendedLHS);

        final var finalExtendedLHS = extendedLHS;
        IntStream.range(1, negativeRows.size())
                .forEach(i -> IntStream.range(0, rowCount)
                        .forEach(e -> {
                            if (e == 0 || e == negativeRows.get(i) + 1) {
                                finalExtendedLHS.get(e).addVal("1");
                            } else {
                                finalExtendedLHS.get(e).addVal("0");
                            }
                        }));
        return finalExtendedLHS;
    }

    /**
     * Helper-Method for adding the criterion line to the left-hand side of the table.
     *
     * @param lHS  The left-hand side of the table.
     * @param inst Fraction or RoundedDecimal.
     * @param <T>  Fraction or RoundedDecimal.
     * @return The left-hand side of the table with criterion line.
     */
    static <T extends CalculableImpl<T>> @NonNull List<Row<T>> addCriterionLineLHS(
            @NonNull final List<Row<T>> lHS,
            @NonNull final T inst) {
        final var line = lHS.get(0).getEntries().stream()
                .map(e -> inst.create("0"))
                .toList();
        final var result = new ArrayList<>(lHS);
        result.add(0, new Row<>(line, inst));
        return result;
    }

    /**
     * Helper-Method for adding the criterion line to the right-hand side of the table.
     *
     * @param rHS  The right-hand side of the table.
     * @param inst Fraction or RoundedDecimal.
     * @param <T>  Fraction or RoundedDecimal.
     * @return The right-hand side of the table with criterion line.
     */
    static <T extends CalculableImpl<T>> @NonNull List<T> addCriterionLineRHS(@NonNull final List<T> rHS,
                                                                              @NonNull final T inst) {
        final var result = new ArrayList<>(rHS);
        result.add(0, inst.create("0"));
        return result;
    }

    /**
     * Helper-Method for inverting the negative Rows of the left-hand side of the table.
     *
     * @param lHS          The left-hand side of the table.
     * @param negativeRows Indices of all rows with negative right-hand side.
     * @param <T>          Fraction or RoundedDecimal.
     * @return The left-hand side of the table with the negative rows inverted.
     */
    static <T extends CalculableImpl<T>> @NonNull List<Row<T>> invertNegativeRowsLHS(
            @NonNull final List<Row<T>> lHS,
            @NonNull final List<Integer> negativeRows) {
        return IntStream.range(0, lHS.size())
                .mapToObj(i -> {
                    if (negativeRows.contains(i)) {
                        return lHS.get(i).invertRow();
                    }
                    return lHS.get(i);
                })
                .toList();
    }

    /**
     * Helper-Function for inverting the negative entries of the right-hand side of the table.
     *
     * @param rHS          The right-hand side of the table.
     * @param negativeRows Indices of all negative entries.
     * @param inst         Fraction or RoundedDecimal.
     * @param <T>          Fraction or RoundedDecimal.
     * @return The right-hand side of the table with all negative entries inverted.
     */
    static <T extends CalculableImpl<T>> @NonNull List<T> invertNegativeRowsRHS(
            @NonNull final List<T> rHS,
            @NonNull final List<Integer> negativeRows,
            @NonNull final T inst) {
        return IntStream.range(0, rHS.size())
                .mapToObj(i -> {
                    if (negativeRows.contains(i)) {
                        return rHS.get(i).multiply(inst.create("-1"));
                    }
                    return rHS.get(i);
                })
                .toList();
    }
}
