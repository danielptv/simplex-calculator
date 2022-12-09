package com.danielptv.simplex.entity;

import com.danielptv.simplex.presentation.OutputUtils;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.danielptv.simplex.presentation.OutputUtils.ANSI_GREEN;
import static com.danielptv.simplex.presentation.OutputUtils.ANSI_RESET;

/**
 * Entity-Class representing a phase of the Two-Phase-Simplex Method.
 */
public class Phase {

    @Getter
    private final List<Table<?>> tables;
    @NonNull
    @Getter
    private final String title;

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
        sb.append(ANSI_GREEN);
        sb.append(title);
        sb.append(ANSI_RESET);
        tables.forEach(t -> sb.append(String.format("%n")).append(t));
        return sb.toString();
    }
}
