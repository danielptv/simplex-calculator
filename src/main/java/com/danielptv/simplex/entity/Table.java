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
 * @param extendedLHS   Extended left-hand side of the table.
 * @param rHS           Right-hand side of the table.
 * @param pivot         Pivot element.
 * @param columnHeaders Column headers.
 * @param rowHeaders    Row headers.
 * @param extensionSize Size of extended left-hand side.
 * @param <T>           Fraction or RoundedDecimal.
 */
@SuppressWarnings("RecordComponentNumber")
public record Table<T extends CalculableImpl<T>>(
        @NonNull T inst,
        @NonNull String title,
        @NonNull List<Row<T>> lHS,
        List<Row<T>> extendedLHS,
        @NonNull List<T> rHS,
        @NonNull Pivot<T> pivot,
        @NonNull List<String> columnHeaders,
        @NonNull List<String> rowHeaders,
        int extensionSize
) {

    /**
     * Constructor for a Table.
     *
     * @param table A Table.
     * @param title The new table title.
     */
    public Table(@NonNull final Table<T> table, @NonNull final String title) {
        this(
                table.inst,
                title,
                table.lHS,
                table.extendedLHS,
                table.rHS,
                table.pivot,
                table.columnHeaders,
                table.rowHeaders,
                table.extensionSize
        );
    }

    @Override
    public String toString() {
        return OutputUtils.printTable(this).toString();
    }
}
