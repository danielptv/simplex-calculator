package com.danielptv.simplex.service;

import com.danielptv.simplex.entity.CalculableImpl;
import com.danielptv.simplex.entity.Pivot;
import com.danielptv.simplex.entity.Row;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Service-Class for calculations on Simplex-Tables.
 */
final class TableCalcService {
    private TableCalcService() {

    }

    /**
     * Method for retrieving the pivot element of a table.
     *
     * @param pivot Pivot containing column and row of the pivot element.
     * @param lHS   The left-hand side of the table.
     * @param <T>   Fraction or RoundedDecimal.
     * @return The pivot element.
     */
    static <T extends CalculableImpl<T>> @NonNull T getPivotElement(@NonNull final Pivot pivot,
                                                                    @NonNull final List<Row<T>> lHS) {
        return lHS.get(pivot.row()).getElementByIndex(pivot.column());
    }

    /**
     * Method for updating row headers.
     *
     * @param columnHeaders Current column headers.
     * @param rowHeaders    Current row headers.
     * @param pivot         Pivot element of the previous table.
     * @return The updated row headers.
     */
    static @NonNull List<String> updateRowHeaders(@NonNull final List<String> columnHeaders,
                                                  @NonNull final List<String> rowHeaders,
                                                  final Pivot pivot) {
        if (pivot == null) {
            return rowHeaders;
        }

        final var result = new ArrayList<>(rowHeaders);
        result.set(pivot.row(), columnHeaders.get(pivot.column()) + "[" + (pivot.column() + 1) + "]");
        return result;
    }

    /**
     * Method for retrieving the indices of all rows with negative right-hand side.
     *
     * @param rHS  The right-hand side of the table.
     * @param inst Fraction or RoundedDecimal.
     * @param <T>  Fraction or RoundedDecimal.
     * @return A List with indices of all negative rows.
     */
    static <T extends CalculableImpl<T>> @NonNull List<Integer> getNegativeRows(@NonNull final List<T> rHS,
                                                                                @NonNull final T inst) {
        final var indices = new ArrayList<Integer>();
        IntStream.range(0, rHS.size())
                .forEach(e -> {
                    if (rHS.get(e).compareTo(inst.create("0")) < 0) {
                        indices.add(e);
                    }
                });
        return indices;
    }

    /**
     * Method for setting the pivot element of a table.
     *
     * @param lHS         The left-hand side of the table.
     * @param rHS         The right-hand side of the table.
     * @param extendedLHS The extended left-hand side of the table.
     * @param inst        Fraction or RoundedDecimal.
     * @param <T>         Fraction or RoundedDecimal.
     * @return The pivot element.
     */
    @SuppressWarnings("ReturnCount")
    static <T extends CalculableImpl<T>> Pivot setPivot(@NonNull final List<Row<T>> lHS,
                                                        @NonNull final List<T> rHS,
                                                        final List<Row<T>> extendedLHS,
                                                        @NonNull final T inst
    ) {
        final var column = lHS.get(0).getMinIndex();
        final var row = getPivotRow(lHS, rHS, extendedLHS != null, column, inst);
        return new Pivot(column, row);
    }

    /**
     * Helper-Method for retrieving the pivot row using "best first search".
     *
     * @param lHS         The left-hand side of the table.
     * @param rHS         The right-hand side of table.
     * @param pivotColumn The pivot column.
     * @param isExtended  Whether the table is extended.
     * @param inst        Fraction or RoundedDecimal.
     * @param <T>         Fraction or RoundedDecimal.
     * @return The index of the pivot row.
     */
    static <T extends CalculableImpl<T>> int getPivotRow(@NonNull final List<Row<T>> lHS,
                                                         @NonNull final List<T> rHS, final boolean isExtended,
                                                         final int pivotColumn, @NonNull final T inst) {
        final var pivots = IntStream.range(0, rHS.size())
                .mapToObj(i -> {
                    final var divisor = lHS.get(i).getElementByIndex(pivotColumn);
                    if (i == 0 || isExtended && i == 1) {
                        return inst.maxValue();
                    }
                    if (divisor.compareTo(inst.create("0")) <= 0) {
                        return inst.maxValue();
                    }
                    return rHS.get(i).divide(divisor);
                })
                .toList();
        return pivots.indexOf(Collections.min(pivots));
    }

    /**
     * Method for determining whether a table meets the criteria for a Simplex-Table.
     *
     * @param rHS        The right-hand side of the table.
     * @param isExtended Whether the table is extended.
     * @param inst       Fraction or RoundedDecimal.
     * @param <T>        Fraction or RoundedDecimal.
     * @return True if table is a valid Simplex-Table else false.
     */
    static <T extends CalculableImpl<T>> boolean isSimplexAcceptable(final List<T> rHS, final boolean isExtended,
                                                                     @NonNull final T inst) {

        for (int i = 0; i < rHS.size(); ++i) {
            if (!isExtended && i != 0 && rHS.get(i).compareTo(inst.create("0")) < 0) {
                return false;
            }

            if (isExtended && i != 1 && rHS.get(i).compareTo(inst.create("0")) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method for determining whether a table is an optimal Simplex-Table.
     *
     * @param lHS         The left-hand side of the table.
     * @param extendedLHS The extended left-hand side of the table.
     * @param <T>         Fraction or RoundedDecimal.
     * @return True if the table is optimal else false.
     */
    static <T extends CalculableImpl<T>> boolean isOptimal(@NonNull final List<Row<T>> lHS,
                                                           final List<Row<T>> extendedLHS) {
        return lHS.get(0).isPositive() && (extendedLHS == null || extendedLHS.get(0).isPositive());
    }
}
