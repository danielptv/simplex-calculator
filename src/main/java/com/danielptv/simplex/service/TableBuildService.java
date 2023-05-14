package com.danielptv.simplex.service;

import com.danielptv.simplex.entity.Row;
import com.danielptv.simplex.entity.SimplexTable;
import com.danielptv.simplex.number.CalculableImpl;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public final class TableBuildService<T extends CalculableImpl<T>> {
    private final T generator;
    private final int varCount;
    private final int constCount;
    private final boolean minimize;
    private final TableCalcService<T> tableCalcService;

    public SimplexTable<T> build(final List<String> objectiveFunction, final List<List<String>> constraints) {
        final var table = buildTable(objectiveFunction, constraints);
        final var rHS = buildRHS(new ArrayList<>(table));
        final var lHS = buildLHS(new ArrayList<>(table));
        final var columnHeaders = buildColumnHeaders();
        final var rowHeadersTemp = buildRowHeaders(tableCalcService.getNegativeRows(rHS));
        final var rowHeaders = enumerateRowHeaders(rowHeadersTemp, columnHeaders);
        final var pivot = tableCalcService.setPivot(lHS, rHS, false);

        return new SimplexTable<>(" ", lHS, rHS, pivot, columnHeaders, rowHeaders, 0);
    }

    List<Row<T>> buildLHS(final ArrayList<ArrayList<T>> table) {
        final var rows = new ArrayList<>(table.stream()
                .map(row -> {
                    row.remove(row.size() - 1);
                    return new Row<>(row, generator);
                })
                .toList());
        if (minimize) {
            rows.set(0, rows.get(0).invertRow());
            return rows;
        }
        return rows;
    }

    List<T> buildRHS(final ArrayList<ArrayList<T>> table) {
        return table.stream()
                .map(row -> {
                    final var size = row.size();
                    return row.get(size - 1);
                })
                .toList();
    }

    List<String> buildColumnHeaders() {
        return IntStream.range(1, varCount + constCount + 2)
                .mapToObj(i -> {
                    if (i < varCount + 1) {
                        return "x" + i;
                    }
                    if (i == varCount + constCount + 1) {
                        return "f";
                    }
                    return "s" + (i - varCount);
                })
                .toList();
    }

    List<String> buildRowHeaders(final List<Integer> negativeRows) {
        return IntStream.range(0, constCount + 1)
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

    @SuppressWarnings({"MagicNumber", "CyclomaticComplexity", "NPathComplexity"})
    ArrayList<ArrayList<T>> buildTable(final List<String> objectiveFunction, final List<List<String>> constraints) {
        final var minusOne = generator.create("-1");
        final var input = new ArrayList<List<String>>();
        final var obj = new ArrayList<>(objectiveFunction);
        obj.add("0");
        input.add(obj);
        input.addAll(constraints);

        // create rows
        final var table = new ArrayList<>(input.stream()
                .map(x -> new ArrayList<T>())
                .toList());

        // fill with zeroes
        for (var row : table) {
            for (int i = 0; i < input.size() - 1 + input.get(0).size(); ++i) {
                row.add(generator.create("0"));
            }
        }

        // feed values
        for (int row = 0; row < input.size(); ++row) {

            final var relationSign = input.get(row).get(input.get(row).size() - 1);

            for (int column = 0; column < input.get(1).size() - 2; ++column) {
                // objective function values
                final var currentVal = input.get(row).get(column);
                if (row == 0) {
                    table.get(0).set(column, generator.create(input.get(0).get(column)).multiply(minusOne));
                } else if (relationSign.equals(">") || relationSign.equals("=")) {
                    table.get(row).set(column, generator.create(currentVal).multiply(minusOne));
                } else {
                    table.get(row).set(column, generator.create(currentVal));
                }
            }

            // unit matrix
            for (int i = input.get(1).size() - 2; i < input.get(1).size() + input.size() - 1; ++i) {
                if (row == i - input.get(1).size() + 3 && !relationSign.equals("=")) {
                    table.get(row).set(i, generator.create("1"));
                }
            }

            // right side values
            var rHS = generator.create(input.get(row).get(input.get(row).size() - 2));
            if (row == 0) {
                rHS = generator.create("0");
            }
            if (relationSign.equals(">") || relationSign.equals("=")) {
                rHS = rHS.multiply(generator.create("-1"));
            }
            table.get(row).set(table.get(row).size() - 1, rHS);
        }
        return table;
    }

    List<String> enumerateRowHeaders(final List<String> rowHeaders, final List<String> columnHeaders) {
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
