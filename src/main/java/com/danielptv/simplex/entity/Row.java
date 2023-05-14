package com.danielptv.simplex.entity;

import com.danielptv.simplex.number.CalculableImpl;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@SuppressFBWarnings("EI_EXPOSE_REP")
public record Row<T extends CalculableImpl<T>>(
        List<T> entries,
        T generator
) {
    public Row(final List<T> entries, final T generator) {
        this.generator = generator;
        this.entries = new ArrayList<>();
        this.entries.addAll(entries);
    }

    public Row(final Row<T> row) {
        this(row.entries, row.generator);
    }

    public Row<T> invertRow() {
        return new Row<>(entries.stream()
                .map(e -> e.multiply(generator.create("-1")))
                .toList(), generator);
    }

    public Row<T> multiplyRow(final Row<T> row, final T factor) {
        return new Row<>(row.entries.stream()
                .map(e -> e.multiply(factor))
                .toList(), row.generator);
    }

    public Row<T> divideRow(final T divisor) {
        return new Row<>(entries.stream()
                .map(e -> {
                    if (divisor.equals(generator.create("0"))) {
                        return e;
                    }
                    return e.divide(divisor);
                })
                .toList(), generator);
    }

    public Row<T> addRow(final Row<T> addends) {
        if (addends.entries.size() != entries.size()) {
            throw new IllegalArgumentException();
        }

        return new Row<>(IntStream.range(0, addends.entries.size())
                .mapToObj(e -> entries.get(e).add(addends.entries.get(e)))
                .toList(), generator);
    }

    public void addVal(final String entry) {
        entries.add(generator.create(entry));
    }

    public boolean isPositive() {
        return Collections.min(entries).compareTo(generator.create("0")) >= 0;
    }

    public Integer getMinIndex() {
        final var min = Collections.min(entries);
        return entries.indexOf(min);
    }

    public T getElement(final int index) {
        return entries.get(index);
    }

    @Override
    public String toString() {
        return entries.toString();
    }
}
