package com.danielptv.simplex.entity;

import com.danielptv.simplex.number.CalculableImpl;

public record Pivot<T extends CalculableImpl<T>>(int column, int row, T value) {
    @Override
    public String toString() {
        return "Pivot: {row = " + row + ", column = " + column + ", value = " + value + "}";
    }
}
