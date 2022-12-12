package com.danielptv.simplex.entity;

import java.util.List;

import static com.danielptv.simplex.presentation.OutputUtils.printTableDTO;

/**
 * Record representing a DTO for a simplex table.
 *
 * @param inst Fraction or RoundedDecimal.
 * @param table String representation of the simplex table.
 * @param variablesCount Number of variables.
 * @param constraintCount Number of constraints.
 * @param <T> Fraction or RoundedDecimal
 */
public record TableDTO<T extends CalculableImpl<T>>(
        T inst,
        List<List<String>> table,
        int variablesCount,
        int constraintCount
) {

    /**
     * Constructor for a TableDTO.
     *
     * @param inst            Fraction or RoundedDecimal.
     * @param variablesCount  Number of variables.
     * @param constraintCount Number of constraints.
     */
    public TableDTO(final T inst, final int variablesCount, final int constraintCount) {
        this(inst, null, variablesCount, constraintCount);
    }

    @Override
    public String toString() {
        if (table == null) {
            return "TableDTO without table";
        }
        return printTableDTO(this).toString();
    }
}
