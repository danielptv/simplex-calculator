package com.danielptv.simplex.entity;

public enum SpecialSolutionType {
    MULTIPLE_SOLUTIONS(String.format("The problem has multiple solutions for the decision variables.%n") +
            String.format("An optimal solution has been found and there are non-basic variables with reduced " +
                    "cost equal to 0, so there are multiple values for the decision variables that allow " +
                    "obtaining the optimal value of f(x).%n") +
            String.format("  One of the solutions is:%n")),
    INFEASIBLE(String.format("The problem has no solution (infeasible).%n" +
            "The iterations of the first phase have been completed and there are artificial variables in " +
            "the base with values strictly greater than 0.")),
    UNBOUNDED(String.format("The problem has an unbounded solution (not limited).%n" +
            "A variable must enter the base but no variable can leave."));
    private final String value;

    SpecialSolutionType(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
