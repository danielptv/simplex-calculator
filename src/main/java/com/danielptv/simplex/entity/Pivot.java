package com.danielptv.simplex.entity;

/**
 * Record representing a pivot element for the Simplex-Algorithm.
 *
 * @param column Column with pivot element.
 * @param row Row with pivot element.
 */
public record Pivot(int column, int row) {
    @Override
    public String toString() {
        return "  -- Pivot -- Column: " + column + " Row: " + row + " --";
    }
}
