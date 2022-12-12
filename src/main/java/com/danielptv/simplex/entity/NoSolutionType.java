package com.danielptv.simplex.entity;


/**
 * Enum for the types of unsolvable simplex problems.
 */
public enum NoSolutionType {
    /**
     * Error message for infeasible problems.
     */
    INFEASIBLE(String.format("  The problem has no solution (infeasible).%n" +
                    "  The iterations of the first phase have been completed and there are artificial variables in " +
                    "the base with values strictly greater than 0.")),
    /**
     * Error message for unbounded problems.
     */
    UNBOUNDED(String.format("  The problem has an unbounded solution (not limited).%n" +
                    "  A variable must enter the base but no variable can leave."));
    private final String value;

    NoSolutionType(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
