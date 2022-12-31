package com.danielptv.simplex.entity;

import com.danielptv.simplex.number.CalculableImpl;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static com.danielptv.simplex.presentation.OutputUtils.BACKGROUND_GREEN;
import static com.danielptv.simplex.presentation.OutputUtils.STYLE_RESET;
import static java.lang.String.format;

/**
 * Class representing a Row in a Simplex-Table.
 *
 * @param <T> Fraction or rounded BigDecimal.
 */
public class Row<T extends CalculableImpl<T>> {
    @Getter
    private final List<T> entries;
    @Getter
    @Setter
    private List<Integer> entryWidths;
    @Getter
    @Setter
    private Integer pivotPos;
    @Getter
    private final T inst;

    /**
     * Constructor for a Row.
     *
     * @param entries List of entries.
     * @param inst    Instance of the number to be calculated with.
     */
    public Row(@NonNull final List<T> entries, @NonNull final T inst) {
        this.inst = inst;
        this.entries = new ArrayList<>();
        this.entries.addAll(entries);
    }

    /**
     * Constructor for a Row.
     *
     * @param row A Row.
     */
    public Row(@NonNull final Row<T> row) {
        this.inst = row.inst;
        this.entries = new ArrayList<>();
        this.entries.addAll(row.entries);
        this.pivotPos = row.pivotPos;
        this.entryWidths = row.entryWidths;
    }

    /**
     * Invert a row.
     *
     * @return The inverted row.
     */
    public Row<T> invertRow() {
        return new Row<>(entries.stream()
                .map(e -> e.multiply(inst.create("-1")))
                .toList(), inst);
    }

    /**
     * Multiply a row.
     *
     * @param row    A row.
     * @param factor The factor.
     * @return The multiplied row.
     */
    public Row<T> multiplyRow(@NonNull final Row<T> row, @NonNull final T factor) {
        return new Row<>(row.entries.stream()
                .map(e -> e.multiply(factor))
                .toList(), row.inst);
    }

    /**
     * Divide a row.
     *
     * @param divisor The divisor.
     * @return The divided row.
     */
    public Row<T> divideRow(@NonNull final T divisor) {
        return new Row<>(entries.stream()
                .map(e -> {
                    if (divisor.equals(inst.create("0"))) {
                        return e;
                    }
                    return e.divide(divisor);
                })
                .toList(), inst);
    }

    /**
     * Add two rows.
     *
     * @param addends A row.
     * @return The resulting row.
     */
    public Row<T> addRow(@NonNull final Row<T> addends) {
        if (addends.entries.size() != entries.size()) {
            throw new IllegalArgumentException();
        }

        return new Row<>(IntStream.range(0, addends.entries.size())
                .mapToObj(e -> entries.get(e).add(addends.entries.get(e)))
                .toList(), inst);
    }

    /**
     * Add a value to a row.
     *
     * @param entry String representation of the value to be added.
     */
    public void addVal(@NonNull final String entry) {
        entries.add(inst.create(entry));
    }

    /**
     * Determine if all entries are positive.
     *
     * @return True if all entries are positive, else false.
     */
    public boolean isPositive() {
        return Collections.min(entries).compareTo(inst.create("0")) >= 0;
    }

    /**
     * Get the index of the smallest value.
     *
     * @return The index.
     */
    public Integer getMinIndex() {
        final var min = Collections.min(entries);
        return entries.indexOf(min);
    }

    /**
     * Get an entry by its index.
     *
     * @param index The index.
     * @return The entry.
     */
    public T getElement(final int index) {
        return entries.get(index);
    }

    @Override
    public String toString() {
        final var sb = new StringBuilder();

        if (entryWidths == null || entryWidths.size() != entries.size()) {
            setEntryWidths();
        }
        IntStream.range(0, entries.size())
                .forEach(e -> {
                    final var width = entryWidths.get(e);
                    final var entry = entries.get(e);
                    if (pivotPos != null && pivotPos > -1 && e == pivotPos) {
                        sb.append(format("|" + BACKGROUND_GREEN + "  %-" + width + "s  " + STYLE_RESET, entry));
                    } else {
                        sb.append(format("|  %-" + width + "s  ", entry));
                    }
                });
        return sb.toString();
    }

    /**
     * Set the entry widths.
     */
    public void setEntryWidths() {
        entryWidths = new ArrayList<>(entries.stream().map(entry -> Math.max(entry.toString().length(), 2))
                .toList());
    }
}
