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
 */
public class Phase {
    @Getter
    private final List<Table<?>> tables;
    @NonNull
    @Getter
    private final String title;
    @Getter
    @Setter
    private boolean solvable = true;

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
    public void addTable(@NonNull final Table<?> table) {
        tables.add(table);
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
