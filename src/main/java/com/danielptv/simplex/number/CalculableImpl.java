package com.danielptv.simplex.number;

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
    T add(T c);

    /**
     * Method for creating new Fraction or RoundedDecimal from an existing one.
     *
     * @param s String representation of the number.
     * @return Fraction or RoundedDecimal.
     */
    T create(String s);

    /**
     * Method for getting the value as BigDecimal.
     *
     * @return The value as BigDecimal.
     */
    BigDecimal toDecimal();

    /**
     * Method for setting the value of a number to infinity.
     *
     * @param infinityType Positive or negative infinity.
     * @return Infinity.
     */
    T toInfinity(InfinityType infinityType);

    /**
     * Method for determining whether a number is infinite.
     *
     * @return True if infinite else false.
     */
    boolean isInfinite();
}
