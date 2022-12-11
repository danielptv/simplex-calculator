package com.danielptv.simplex.entity;

import lombok.Getter;
import lombok.NonNull;

import java.util.List;

import static com.danielptv.simplex.presentation.OutputUtils.printTableDTO;

/**
 * Entity-Class representing a data-transfer object for a Simplex-Table.
 *
 * @param <T> Fraction or RoundedDecimal.
 */
public class TableDTO<T extends CalculableImpl<T>> {
    /**
     * Fraction or RoundedDecimal.
     */
    @Getter
    private final T inst;
    /**
     * String representation of a Simplex-Table.
     */
    @Getter
    private List<List<String>> table;
    /**
     * Number of variables.
     */
    @Getter
    private final int variablesCount;
    /**
     * Number of constraints.
     */
    @Getter
    private final int constraintCount;

    /**
     * Constructor for a TableDTO.
     *
     * @param inst            Fraction or RoundedDecimal.
     * @param variablesCount  Number of variables.
     * @param constraintCount Number of constraints.
     */
    public TableDTO(final T inst, final int variablesCount, final int constraintCount) {
        this.inst = inst;
        this.variablesCount = variablesCount;
        this.constraintCount = constraintCount;
    }

    /**
     * Constructor for a TableDTO.
     *
     * @param inst            Fraction or RoundedDecimal.
     * @param variablesCount  Number of variables.
     * @param constraintCount Number of constraints.
     * @param table           ArrayList-Representation of the Simplex-Table.
     */
    public TableDTO(final T inst, final int variablesCount,
                    final int constraintCount,
                    @NonNull final List<List<String>> table) {
        this(inst, variablesCount, constraintCount);
        this.table = table;
    }

    @Override
    public String toString() {
        if (table == null) {
            return "TableDTO without table";
        }
        return printTableDTO(this).toString();
    }
}
