package com.danielptv.simplex.service;

import com.danielptv.simplex.number.CalculableImpl;
import com.danielptv.simplex.entity.Pivot;
import com.danielptv.simplex.entity.Row;
import com.danielptv.simplex.entity.Table;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static com.danielptv.simplex.number.InfinityType.POSITIVE;

/**
 * Service-Class for calculations on Simplex-Tables.
 */
final class TableCalcService {
    private TableCalcService() {

    }

    /**
     * Update the row headers.
     *
     * @param columnHeaders Current column headers.
     * @param rowHeaders    Current row headers.
     * @param pivot         Pivot element of the previous table.
     * @param <T>           Fraction or RoundedDecimal.
     * @return The updated row headers.
     */
    static <T extends CalculableImpl<T>> @NonNull List<String> updateRowHeaders(
            @NonNull final List<String> columnHeaders,
            @NonNull final List<String> rowHeaders,
            @NonNull final Pivot<T> pivot
    ) {
        final var result = new ArrayList<>(rowHeaders);
        result.set(pivot.row(), columnHeaders.get(pivot.column()) + "[" + (pivot.column() + 1) + "]");
        return result;
    }

    /**
     * Retrieve the indices of all rows with negative right-hand side.
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
     * Set the pivot element of a table.
     *
     * @param lHS        The left-hand side of the table.
     * @param rHS        The right-hand side of the table.
     * @param isExtended Whether the table is extended.
     * @param inst       Fraction or RoundedDecimal.
     * @param <T>        Fraction or RoundedDecimal.
     * @return The pivot element.
     */
    static <T extends CalculableImpl<T>> Pivot<T> setPivot(@NonNull final List<Row<T>> lHS,
                                                           @NonNull final List<T> rHS,
                                                           final boolean isExtended,
                                                           @NonNull final T inst
    ) {
        final var column = lHS.get(0).getMinIndex();

        final var pivots = IntStream.range(0, rHS.size())
                .mapToObj(i -> {
                    final var divisor = lHS.get(i).getElement(column);
                    if (i == 0 || isExtended && i == 1) {
                        return inst.toInfinity(POSITIVE);
                    }
                    if (divisor.compareTo(inst.create("0")) <= 0) {
                        return inst.toInfinity(POSITIVE);
                    }
                    return rHS.get(i).divide(divisor);
                })
                .toList();

        final var value = Collections.min(pivots);
        final var row = pivots.indexOf(value);

        if (value.isInfinite()) {
            return new Pivot<>(column, row, value);
        }

        return new Pivot<>(column, row, lHS.get(row).getElement(column));
    }

    /**
     * Determine whether a table meets the criteria for a primary simplex table.
     *
     * @param table The table.
     * @param <T>   Fraction or RoundedDecimal.
     * @return True if table is a valid Simplex-Table else false.
     */
    static <T extends CalculableImpl<T>> boolean isValid(@NonNull final Table<T> table) {

        final var isExtended = table.helperColumns() != 0;
        if (isExtended && !table.rHS().get(0).equals(table.inst().create("0"))) {
            return false;
        }

        final var size = table.rHS().size();
        for (int row = isExtended ? 2 : 1; row < size; ++row) {
            final var isNegative = table.rHS().get(row).compareTo(table.inst().create("0")) < 0;
            if (isNegative) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determine whether a table is an optimal simplex table.
     *
     * @param table The table.
     * @param <T>   Fraction or RoundedDecimal.
     * @return True if the table is optimal else false.
     */
    static <T extends CalculableImpl<T>> boolean isOptimal(@NonNull final Table<T> table) {
        return table.lHS().get(0).isPositive();
    }

    /**
     * Determine whether an optimal solution is degenerate.
     *
     * @param table The table.
     * @param <T>   Fraction or RoundedDecimal.
     * @return True if solution is degenerate else false.
     */
    static <T extends CalculableImpl<T>> boolean isDegenerate(@NonNull final Table<T> table) {
        if (!isOptimal(table) || table.helperColumns() != 0) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < table.columnHeaders().size(); ++i) {
            final var variable = table.columnHeaders().get(i);
            if (variable.contains("s") && !table.rowHeaders().contains(variable)) {
                final var index = table.columnHeaders().indexOf(variable);
                if (!table.lHS().get(0).getElement(index).equals(table.inst().create("0"))) {
                    return false;
                }
            }
        }
        return true;
    }
}
