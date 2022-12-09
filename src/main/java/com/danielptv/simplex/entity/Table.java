package com.danielptv.simplex.entity;

import com.danielptv.simplex.presentation.OutputUtils;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

/**
 * Class representing a table for the Two-Phase-Simplex Method.
 *
 * @param <T> Fraction or RoundedDecimal.
 */
public class Table<T extends CalculableImpl<T>> {
    @NonNull
    @Getter
    private final T inst;
    @NonNull
    @Getter
    private final String title;
    @NonNull
    @Getter
    private final List<Row<T>> lHS;
    @Getter
    private final List<Row<T>> extendedLHS;
    @NonNull
    @Getter
    private final List<T> rHS;
    @Getter
    private final Pivot pivot;
    @NonNull
    @Getter
    private final List<String> columnHeaders;
    @NonNull
    @Getter
    private final List<String> rowHeaders;
    @Getter
    private final int extensionSize;

    /**
     * Constructor for a Table.
     *
     * @param inst Instance of the number type to be calculated with.
     * @param title Title of the table.
     * @param lHS Left-hand side of the table.
     * @param extendedLHS Extended left-hand side of the table.
     * @param rHS Right-hand side of the table.
     * @param pivot Pivot element.
     * @param columnHeaders Column headers.
     * @param rowHeaders Row headers.
     * @param extensionSize Size of extended left-hand side.
     */
    @SuppressWarnings("ParameterNumber")
    public Table(@NonNull final T inst, @NonNull final String title, @NonNull final List<Row<T>> lHS,
                 final List<Row<T>> extendedLHS,
                 @NonNull final List<T> rHS, final Pivot pivot,
                 @NonNull final List<String> columnHeaders, @NonNull final List<String> rowHeaders,
                 final int extensionSize) {
        this.inst = inst;
        this.title = title;
        this.lHS = lHS;
        this.rHS = rHS;
        this.extendedLHS = extendedLHS;
        this.pivot = pivot;
        this.columnHeaders = columnHeaders;
        this.rowHeaders = rowHeaders;
        this.extensionSize = extensionSize;
    }

    /**
     * Constructor for a Table.
     *
     * @param table A Table.
     * @param title The new table title.
     */
    public Table(@NonNull final Table<T> table, @NonNull final String title) {
        this.inst = table.inst;
        this.title = title;
        this.lHS = table.lHS;
        this.rHS = table.rHS;
        this.extendedLHS = table.extendedLHS;
        this.pivot = table.pivot;
        this.columnHeaders = table.columnHeaders;
        this.rowHeaders = table.rowHeaders;
        this.extensionSize = table.extensionSize;
    }

    @Override
    public String toString() {
        return OutputUtils.printTable(this).toString();
    }
}
