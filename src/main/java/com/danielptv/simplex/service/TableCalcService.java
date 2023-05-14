package com.danielptv.simplex.service;

import com.danielptv.simplex.entity.Pivot;
import com.danielptv.simplex.entity.Row;
import com.danielptv.simplex.entity.SimplexTable;
import com.danielptv.simplex.number.CalculableImpl;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static com.danielptv.simplex.number.InfinityType.POSITIVE;

@RequiredArgsConstructor
public final class TableCalcService<T extends CalculableImpl<T>> {
    private final T generator;

    List<String> updateRowHeaders(
            final List<String> columnHeaders,
            final List<String> rowHeaders,
            final Pivot<T> pivot
    ) {
        final var result = new ArrayList<>(rowHeaders);
        result.set(pivot.row(), columnHeaders.get(pivot.column()) + "[" + (pivot.column() + 1) + "]");
        return result;
    }

    List<Integer> getNegativeRows(final List<T> rHS) {
        final var indices = new ArrayList<Integer>();
        IntStream.range(0, rHS.size())
                .forEach(e -> {
                    if (rHS.get(e).compareTo(generator.create("0")) < 0) {
                        indices.add(e);
                    }
                });
        return indices;
    }

    Pivot<T> setPivot(final List<Row<T>> lHS, final List<T> rHS, final boolean isExtended) {
        final int column = lHS.get(0).getMinIndex();

        final var pivots = IntStream.range(0, rHS.size())
                .mapToObj(i -> {
                    final var divisor = lHS.get(i).getElement(column);
                    if (i == 0 || isExtended && i == 1) {
                        return generator.toInfinity(POSITIVE);
                    }
                    if (divisor.compareTo(generator.create("0")) <= 0) {
                        return generator.toInfinity(POSITIVE);
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

    boolean isInvalid(final SimplexTable<T> table) {
        final var isExtended = table.helperColumns() != 0;
        if (isExtended && !table.rHS().get(0).equals(generator.create("0"))) {
            return true;
        }

        final var size = table.rHS().size();
        for (int row = isExtended ? 2 : 1; row < size; ++row) {
            final var isNegative = table.rHS().get(row).compareTo(generator.create("0")) < 0;
            if (isNegative) {
                return true;
            }
        }
        return false;
    }

    boolean isOptimal(final SimplexTable<T> table) {
        return table.lHS().get(0).isPositive();
    }

    boolean isDegenerate(final SimplexTable<T> table) {
        if (!isOptimal(table) || table.helperColumns() != 0) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < table.columnHeaders().size(); ++i) {
            final var variable = table.columnHeaders().get(i);
            if (variable.contains("s") && !table.rowHeaders().contains(variable)) {
                final var index = table.columnHeaders().indexOf(variable);
                if (!table.lHS().get(0).getElement(index).equals(generator.create("0"))) {
                    return false;
                }
            }
        }
        return true;
    }
}
