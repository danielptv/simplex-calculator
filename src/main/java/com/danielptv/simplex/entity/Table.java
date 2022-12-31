package com.danielptv.simplex.entity;

import com.danielptv.simplex.number.CalculableImpl;
import com.danielptv.simplex.presentation.OutputUtils;
import lombok.NonNull;

import java.util.List;

/**
 * Record representing a simplex table.
 *
 * @param inst          Instance of the number type to be calculated with.
 * @param title         Title of the table.
 * @param lHS           Left-hand side of the table.
 * @param rHS           Right-hand side of the table.
 * @param pivot         Pivot element.
 * @param columnHeaders Column headers.
 * @param rowHeaders    Row headers.
 * @param rows          Number of rows.
 * @param columns       Total number of columns.
 * @param helperColumns Number of helper columns.
 * @param <T>           Fraction or RoundedDecimal.
 */
@SuppressWarnings("RecordComponentNumber")
public record Table<T extends CalculableImpl<T>>(
        @NonNull T inst,
        @NonNull String title,
        @NonNull List<Row<T>> lHS,
        @NonNull List<T> rHS,
        @NonNull Pivot<T> pivot,
        @NonNull List<String> columnHeaders,
        @NonNull List<String> rowHeaders,
        int rows,
        int columns,
        int helperColumns
) {
    /**
     * Constructor for a table.
     *
     * @param inst          Instance of the number type to be calculated with.
     * @param title         Title of the table.
     * @param lHS           Left-hand side of the table.
     * @param rHS           Right-hand side of the table.
     * @param pivot         Pivot element.
     * @param columnHeaders Column headers.
     * @param rowHeaders    Row headers.
     * @param helperColumns Number of helper columns.
     */
    @SuppressWarnings("ParameterNumber")
    public Table(
            @NonNull final T inst,
            @NonNull final String title,
            @NonNull final List<Row<T>> lHS,
            @NonNull final List<T> rHS,
            @NonNull final Pivot<T> pivot,
            @NonNull final List<String> columnHeaders,
            @NonNull final List<String> rowHeaders,
            final int helperColumns
    ) {
        this(
                inst,
                title,
                lHS,
                rHS,
                pivot,
                columnHeaders,
                rowHeaders,
                lHS.size(),
                lHS.get(0).getEntries().size(),
                helperColumns
        );

    }

    /**
     * Copy-Constructor for a Table.
     *
     * @param table A Table.
     * @param title The new table title.
     */
    public Table(@NonNull final Table<T> table, @NonNull final String title) {
        this(
                table.inst,
                title,
                table.lHS,
                table.rHS,
                table.pivot,
                table.columnHeaders,
                table.rowHeaders,
                table.rows,
                table.columns,
                table.helperColumns
        );
    }

    @Override
    public String toString() {
        return OutputUtils.printTable(this).toString();
    }
}
