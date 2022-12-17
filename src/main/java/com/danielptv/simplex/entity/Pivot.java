package com.danielptv.simplex.entity;

import com.danielptv.simplex.number.CalculableImpl;

/**
 * Record representing a pivot element for the simplex algorithm.
 *
 * @param column Column with pivot element.
 * @param row    Row with pivot element.
 * @param value  The value of the pivot element.
 * @param <T> Fraction or RoundedDecimal
 */
public record Pivot<T extends CalculableImpl<T>>(int column, int row, T value) {
    @Override
    public String toString() {
        return "Pivot: {row = " + row + ", column = " + column + ", value = " + value + "}";
    }
}
