package com.danielptv.simplex.service;

import com.danielptv.simplex.entity.Row;
import com.danielptv.simplex.entity.SimplexTable;
import com.danielptv.simplex.number.CalculableImpl;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public final class TableExtensionService<T extends CalculableImpl<T>> {
    private final T generator;
    private final TableCalcService<T> calcService;

    SimplexTable<T> buildExtension(final SimplexTable<T> table) {
        if (table.helperColumns() != 0) {
            throw new UnsupportedOperationException("Table is already extended!");
        }

        final var negativeRows = calcService.getNegativeRows(table.rHS());
        var lHS = invertNegativeRowsLHS(table.lHS(), negativeRows);
        var rHS = invertNegativeRowsRHS(table.rHS(), negativeRows);
        lHS = addCriterionLineLHS(lHS);
        rHS = addCriterionLineRHS(rHS);
        lHS = addHelperColumns(negativeRows, lHS);
        final var extensionSize = negativeRows.size();
        final var columnHeaders = new ArrayList<>(table.columnHeaders());
        IntStream.range(1, extensionSize + 1)
                .forEach(i -> {
                    final var size = columnHeaders.size();
                    columnHeaders.add(size - 1, "h" + i);
                });
        final var rowHeaders = new ArrayList<>(table.rowHeaders());
        rowHeaders.add(0, "z'");
        final var pivot = calcService.setPivot(lHS, rHS, true);

        return new SimplexTable<>(
                table.title(),
                lHS,
                rHS,
                pivot,
                columnHeaders,
                rowHeaders,
                extensionSize);
    }

    SimplexTable<T> removeExtension(final SimplexTable<T> table) {
        if (table.helperColumns() == 0) {
            throw new UnsupportedOperationException("Table is not extended!");
        }
        final var lHS = new ArrayList<>(removeHelperColumns(table.helperColumns(), table.lHS()));
        lHS.remove(0);
        final var rHS = new ArrayList<>(table.rHS());
        rHS.remove(0);
        final var pivot = calcService.setPivot(lHS, rHS, false);

        final var columnHeaders = new ArrayList<>(table.columnHeaders());
        IntStream.range(0, table.helperColumns())
                .forEach(i -> {
                    final var size = columnHeaders.size();
                    columnHeaders.remove(size - 2);
                });

        final var rowHeaders = new ArrayList<>(table.rowHeaders());
        rowHeaders.remove(0);

        return new SimplexTable<>(
                table.title(),
                lHS,
                rHS,
                pivot,
                columnHeaders,
                rowHeaders,
                0
        );
    }

    List<Row<T>> addHelperColumns(final List<Integer> negativeRows, final List<Row<T>> lHS) {
        IntStream.range(0, negativeRows.size())
                .forEach(i -> IntStream.range(0, lHS.size())
                        .forEach(e -> {
                            if (e == 0 || e == negativeRows.get(i) + 1) {
                                lHS.get(e).addVal("1");
                            } else {
                                lHS.get(e).addVal("0");
                            }
                        }));
        return lHS.stream().map(Row::new).toList();
    }

    List<Row<T>> removeHelperColumns(final int helperColumns, final List<Row<T>> lHS) {
        final var result = lHS.stream().map(Row::new).toList();
        IntStream.range(0, helperColumns)
                .forEach(i -> result.forEach(row -> row.entries().remove(row.entries().size() - 1)));
        return result;
    }

    List<Row<T>> addCriterionLineLHS(final List<Row<T>> lHS) {
        final var line = lHS.get(0).entries().stream()
                .map(e -> generator.create("0"))
                .toList();
        final var result = new ArrayList<>(lHS);
        result.add(0, new Row<>(line, generator));
        return result;
    }

    List<T> addCriterionLineRHS(final List<T> rHS) {
        final var result = new ArrayList<>(rHS);
        result.add(0, generator.create("0"));
        return result;
    }

    List<Row<T>> invertNegativeRowsLHS(final List<Row<T>> lHS, final List<Integer> negativeRows) {
        return IntStream.range(0, lHS.size())
                .mapToObj(i -> {
                    if (negativeRows.contains(i)) {
                        return lHS.get(i).invertRow();
                    }
                    return lHS.get(i);
                })
                .toList();
    }

    List<T> invertNegativeRowsRHS(final List<T> rHS, final List<Integer> negativeRows) {
        return IntStream.range(0, rHS.size())
                .mapToObj(i -> {
                    if (negativeRows.contains(i)) {
                        return rHS.get(i).multiply(generator.create("-1"));
                    }
                    return rHS.get(i);
                })
                .toList();
    }
}
