package com.danielptv.simplex.number;

import java.math.BigDecimal;

/**
 * Interface for Calculations.
 *
 * @param <T> Class implementing CalculableImpl.
 */
public interface CalculableImpl<T extends CalculableImpl<T>> extends Comparable<T> {
    /**
     * Multiply two values.
     *
     * @param c Fraction or RoundedDecimal.
     * @return Fraction or RoundedDecimal.
     */
    T multiply(T c);

    /**
     * Divide two values.
     *
     * @param c Fraction or RoundedDecimal.
     * @return Fraction or RoundedDecimal.
     */
    T divide(T c);

    /**
     * Add two values.
     *
     * @param c Fraction or RoundedDecimal.
     * @return Fraction or RoundedDecimal.
     */
    T add(T c);

    /**
     * Create a new Fraction or RoundedDecimal from an existing one.
     *
     * @param s String representation of the number.
     * @return Fraction or RoundedDecimal.
     */
    T create(String s);

    /**
     * Get the value as BigDecimal.
     *
     * @return The value as BigDecimal.
     */
    BigDecimal toDecimal();

    /**
     * Set the value to infinity.
     *
     * @param infinityType Positive or negative infinity.
     * @return Infinity.
     */
    T toInfinity(InfinityType infinityType);

    /**
     * Determine whether a value is infinite.
     *
     * @return True if infinite else false.
     */
    boolean isInfinite();
}
