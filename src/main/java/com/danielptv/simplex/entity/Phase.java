package com.danielptv.simplex.entity;

import com.danielptv.simplex.presentation.OutputUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.danielptv.simplex.presentation.OutputUtils.FONT_GREEN;
import static com.danielptv.simplex.presentation.OutputUtils.STYLE_RESET;

/**
 * Entity-Class representing a phase of the Two-Phase-Simplex Method.
 *
 * @param <T> Fraction or RoundedDecimal
 */
public class Phase<T extends CalculableImpl<T>> {
    @Getter
    @NonNull
    private final List<Table<T>> tables;
    @NonNull
    @Getter
    private final String title;
    @Getter
    @Setter
    private NoSolutionType noSolutionType;

    /**
     * Constructor for a Phase.
     *
     * @param title Title of the phase.
     */
    public Phase(@NonNull final String title) {
        this.title = title;
        tables = new ArrayList<>();
    }

    /**
     * Method for adding a table to the phase.
     *
     * @param table The table.
     */
    public void addTable(@NonNull final Table<T> table) {
        tables.add(table);
    }

    /**
     * Method for getting the last table of the phase.
     *
     * @return The last table.
     */
    public Table<T> getLastTable() {
        return tables.get(tables.size() - 1);
    }

    @Override
    public String toString() {
        IntStream.range(0, tables.size() - 1)
                .forEach(i -> OutputUtils.accentuatePivot(tables.get(i)));

        final var sb = new StringBuilder();
        sb.append("  ");
        sb.append(FONT_GREEN);
        sb.append(title);
        sb.append(STYLE_RESET);
        tables.forEach(t -> sb.append(String.format("%n")).append(t));
        return sb.toString();
    }
}
