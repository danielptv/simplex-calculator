package com.danielptv.simplex.entity;

import java.math.BigDecimal;

/**
 * Interface for Calculations.
 *
 * @param <T> Class implementing CalculableImpl.
 */
public interface CalculableImpl<T extends CalculableImpl<T>> extends Comparable<T> {
    /**
     * Method for multiplication.
     *
     * @param c Fraction or RoundedDecimal.
     * @return Fraction or RoundedDecimal.
     */
    T multiply(T c);

    /**
     * Method for division.
     *
     * @param c Fraction or RoundedDecimal.
     * @return Fraction or RoundedDecimal.
     */
    T divide(T c);

    /**
     * Method for addition.
     *
     * @param c Fraction or RoundedDecimal.
     * @return Fraction or RoundedDecimal.
     */
    T addUp(T c);

    /**
     * Method for creating new Fraction or RoundedDecimal from an existing one.
     *
     * @param s String representation of the number.
     * @return Fraction or RoundedDecimal.
     */
    T create(String s);

    /**
     * Method for creating an artificial MaxValue.
     *
     * @return Large Fraction or RoundedDecimal.
     */
    T maxValue();

    /**
     * Method for getting the value as BigDecimal.
     *
     * @return The value as BigDecimal.
     */
    BigDecimal toDecimal();
}
