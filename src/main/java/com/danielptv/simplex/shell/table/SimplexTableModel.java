package com.danielptv.simplex.shell.table;

import com.danielptv.simplex.entity.SimplexTable;
import org.springframework.shell.table.TableModel;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class SimplexTableModel extends TableModel {
    private final ArrayList<ArrayList<String>> data;

    public SimplexTableModel(final SimplexTable<?> table, final boolean accentPivot) {
        this.data = tableToArray(table, accentPivot);
        final int width = data.size() > 0 ? data.get(0).size() : 0;
        for (ArrayList<String> datum : data) {
            Assert.isTrue(width == datum.size(), "All rows of list data must be of same length");
        }
    }

    public int getRowCount() {
        return data.size();
    }

    public int getColumnCount() {
        return data.size() > 0 ? data.get(0).size() : 0;
    }

    public Object getValue(final int row, final int column) {
        return data.get(row).get(column);
    }

    public int[] getColumnsMaxWidths() {
        final var result = new int[data.get(0).size()];
        Arrays.fill(result, 0);

        for (ArrayList<String> datum : data) {
            for (int column = 0; column < datum.size(); column++) {
                result[column] = Math.max(result[column], datum.get(column).length());
            }
        }
        return result;
    }

    @SuppressWarnings("LambdaBodyLength")
    private ArrayList<ArrayList<String>> tableToArray(final SimplexTable<?> table, final boolean accentPivot) {
        final var result = new ArrayList<ArrayList<String>>();

        final var columns = new ArrayList<String>();
        columns.add(" J ");
        table.columnHeaders().forEach(header -> columns.add(" " + header + " "));
        result.add(columns);

        final var pivotRow = table.pivot().row();
        final var pivotColumn = table.pivot().column();

        IntStream.range(0, table.rows()).forEach(r -> {
            final var row = new ArrayList<String>();
            row.add(" " + table.rowHeaders().get(r) + " ");

            IntStream.range(0, table.columns()).forEach(c -> {
                final var entry = table.lHS().get(r).entries().get(c);
                if (accentPivot && r == pivotRow && c == pivotColumn) {
                    row.add(" [" + entry.toString() + "] ");
                } else {
                    row.add(" " + entry.toString() + " ");
                }
            });
            final var rHS = " " + table.rHS().get(r).toString() + " ";
            row.add(rHS);
            result.add(row);
        });
        return result;
    }
}
