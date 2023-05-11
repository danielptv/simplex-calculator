package com.danielptv.simplex.entity;

import com.danielptv.simplex.number.CalculableImpl;

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
 * @param problemType Problem type (min/max)
 */
public record TableDTO<T extends CalculableImpl<T>>(
        T inst,
        List<List<String>> table,
        int variablesCount,
        int constraintCount,
        ProblemType problemType
) {

    /**
     * Constructor for a TableDTO.
     *
     * @param inst            Fraction or RoundedDecimal.
     * @param variablesCount  Number of variables.
     * @param constraintCount Number of constraints.
     */
    public TableDTO(final T inst, final int variablesCount, final int constraintCount, final ProblemType problemType) {
        this(inst, null, variablesCount, constraintCount, problemType);
    }

    @Override
    public String toString() {
        if (table == null) {
            return "TableDTO without table";
        }
        return printTableDTO(this).toString();
    }
}
