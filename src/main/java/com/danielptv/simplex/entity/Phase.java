package com.danielptv.simplex.entity;

import com.danielptv.simplex.number.CalculableImpl;
import lombok.NonNull;

import java.util.List;
import java.util.stream.IntStream;

import static com.danielptv.simplex.presentation.OutputUtils.FONT_GREEN;
import static com.danielptv.simplex.presentation.OutputUtils.STYLE_RESET;
import static com.danielptv.simplex.presentation.OutputUtils.accentuatePivot;

/**
 * Entity-Class representing a phase of the Two-Phase-Simplex Method.
 *
 * @param title Title of the phase.
 * @param tables Tables of the phase.
 * @param specialSolutionType Solution type of the phase.
 * @param <T> Fraction or RoundedDecimal
 */
public record Phase<T extends CalculableImpl<T>>(
        @NonNull String title,
        @NonNull List<Table<T>> tables,
        SpecialSolutionType specialSolutionType
) {

    /**
     * Get the last table of the phase.
     *
     * @return The last table.
     */
    public Table<T> getLastTable() {
        return tables.get(tables.size() - 1);
    }

    @Override
    public String toString() {
        IntStream.range(0, tables.size() - 1)
                .forEach(i -> accentuatePivot(tables.get(i)));

        final var sb = new StringBuilder();
        sb.append("  ");
        sb.append(FONT_GREEN);
        sb.append(title);
        sb.append(STYLE_RESET);
        tables.forEach(t -> sb.append(String.format("%n")).append(t));
        return sb.toString();
    }
}
