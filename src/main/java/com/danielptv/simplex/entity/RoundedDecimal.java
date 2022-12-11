package com.danielptv.simplex.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Entity-Class representing rounded decimals.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class RoundedDecimal implements CalculableImpl<RoundedDecimal> {
    @EqualsAndHashCode.Include
    @Getter
    private final BigDecimal value;
    @Getter
    private final int mantissaLength;

    /**
     * Constructor for a RoundedDecimal.
     *
     * @param mantissaLength Int specifying the desired mantissa length.
     */
    public RoundedDecimal(final int mantissaLength) {
        value = new BigDecimal("0");
        this.mantissaLength = mantissaLength;
    }

    /**
     * Constructor for a RoundedDecimal.
     *
     * @param s              String representation of the RoundedDecimal.
     * @param mantissaLength The desired mantissa length.
     */
    @SuppressWarnings("MagicNumber")
    public RoundedDecimal(@NonNull final String s, final int mantissaLength) {
        this.mantissaLength = mantissaLength;
        if (s.contains("/")) {
            final var split = s.split("/");
            if (split.length != 2 || new BigDecimal("0").equals(new BigDecimal(split[1]))) {
                throw new IllegalArgumentException();
            }
            value = round(new BigDecimal(split[0]).divide(new BigDecimal(split[1]),
                    mantissaLength + 20, RoundingMode.HALF_EVEN));
            return;
        }
        value = round(new BigDecimal(s));
    }

    /**
     * Constructor for a RoundedDecimal.
     *
     * @param b              A BigDecimal.
     * @param mantissaLength The desired mantissa length.
     */
    public RoundedDecimal(@NonNull final BigDecimal b, final int mantissaLength) {
        this.mantissaLength = mantissaLength;
        value = round(b);
    }

    private RoundedDecimal(@NonNull final Long l) {
        value = BigDecimal.valueOf(l);
        this.mantissaLength = 0;
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }

    @Override
    public int compareTo(@NonNull final RoundedDecimal o) {
        return value.compareTo(o.value);
    }

    /**
     * Method for rounding a BigDecimal.
     *
     * @param d A BigDecimal.
     * @return The rounded BigDecimal.
     */
    @SuppressWarnings("MagicNumber")
    private BigDecimal round(@NonNull final BigDecimal d) {

        if (d.doubleValue() == 0) {
            return new BigDecimal("0");
        }

        if (d.doubleValue() >= 1 || d.doubleValue() <= -1) {
            final var factor = BigDecimal.valueOf(Math.pow(10, d.precision() - d.scale()));
            final var rounded = d.divide(factor, mantissaLength, RoundingMode.HALF_EVEN);
            return rounded.multiply(factor).setScale(mantissaLength, RoundingMode.HALF_EVEN).stripTrailingZeros();
        } else {

            final var fracDigits = d.abs().toPlainString().split("\\.")[1];
            var digits = 0;
            for (int i = 0; i < fracDigits.length(); ++i) {
                if (fracDigits.charAt(i) != '0') {
                    break;
                }
                digits += 1;
            }
            return d.setScale(digits + mantissaLength, RoundingMode.HALF_EVEN).stripTrailingZeros();
        }
    }

    /**
     * Method for multiplying RoundedDecimals.
     *
     * @param d A RoundedDecimal.
     * @return The result.
     */
    @Override
    public RoundedDecimal multiply(@NonNull final RoundedDecimal d) {
        return new RoundedDecimal(value.multiply(d.value), d.mantissaLength);
    }

    /**
     * Method for dividing RoundedDecimals.
     *
     * @param d A RoundedDecimal.
     * @return The result.
     */
    @Override
    @SuppressWarnings("MagicNumber")
    public RoundedDecimal divide(@NonNull final RoundedDecimal d) {
        return new RoundedDecimal(value.divide(d.value,
                d.value.precision() + value.precision() + 20, RoundingMode.HALF_EVEN), d.mantissaLength);
    }

    /**
     * Method for adding RoundedDecimals.
     *
     * @param d A RoundedDecimal.
     * @return The result.
     */
    @Override
    public RoundedDecimal addUp(@NonNull final RoundedDecimal d) {
        return new RoundedDecimal(value.add(d.value), d.mantissaLength);
    }

    /**
     * Method for creating a new RoundedDecimal from an existing one.
     *
     * @param s String representation of the number.
     * @return A RoundedDecimal.
     */
    @Override
    public RoundedDecimal create(@NonNull final String s) {
        return new RoundedDecimal(s, this.mantissaLength);
    }

    /**
     * Method for creating an artificial MaxValue.
     *
     * @return A large RoundedDecimal.
     */
    @Override
    public RoundedDecimal maxValue() {
        return new RoundedDecimal(Long.MAX_VALUE);
    }

    /**
     * Method for getting the value as BigDecimal.
     *
     * @return The value as BigDecimal rounded to 2 decimal places.
     */
    @Override
    public BigDecimal toDecimal() {
        return value.setScale(2, RoundingMode.HALF_EVEN).stripTrailingZeros();
    }

}
