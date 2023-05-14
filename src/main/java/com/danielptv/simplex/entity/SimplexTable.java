package com.danielptv.simplex.entity;

import com.danielptv.simplex.number.CalculableImpl;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

@SuppressWarnings("RecordComponentNumber")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record SimplexTable<T extends CalculableImpl<T>>(
        String title,
        List<Row<T>> lHS,
        List<T> rHS,
        Pivot<T> pivot,
        List<String> columnHeaders,
        List<String> rowHeaders,
        int rows,
        int columns,
        int helperColumns
) {
    @SuppressWarnings("ParameterNumber")
    public SimplexTable(
            final String title,
            final List<Row<T>> lHS,
            final List<T> rHS,
            final Pivot<T> pivot,
            final List<String> columnHeaders,
            final List<String> rowHeaders,
            final int helperColumns
    ) {
        this(
                title,
                lHS,
                rHS,
                pivot,
                columnHeaders,
                rowHeaders,
                lHS.size(),
                lHS.get(0).entries().size(),
                helperColumns
        );

    }

    public SimplexTable(final SimplexTable<T> table, final String title) {
        this(
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
        return "Table: {lHS = " + lHS + ", rHS = " + rHS + ", pivot = " + pivot + "}";
    }
}
