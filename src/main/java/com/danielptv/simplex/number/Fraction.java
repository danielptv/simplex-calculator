package com.danielptv.simplex.number;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.javatuples.Pair;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Class representing a fraction.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Fraction implements CalculableImpl<Fraction> {
    @EqualsAndHashCode.Include
    @Getter
    private final BigInteger numerator;
    @EqualsAndHashCode.Include
    @Getter
    private final BigInteger denominator;
    @EqualsAndHashCode.Include
    @Getter
    private final InfinityType infinityType;

    /**
     * Constructor for a Fraction.
     */
    public Fraction() {
        numerator = new BigInteger("0");
        denominator = new BigInteger("1");
        infinityType = null;
    }

    /**
     * Constructor for a Fraction.
     *
     * @param fraction String representation of the numerator.
     */
    @SuppressWarnings({"ReturnCount", "MagicNumber"})
    public Fraction(@NonNull final String fraction) {
        infinityType = null;
        if (fraction.contains("/")) {
            final var split = fraction.split("/");
            if (split.length != 2 || new BigInteger("0").equals(new BigInteger(split[1]))) {
                throw new IllegalArgumentException();
            }
            final var simplified = simplify(new BigInteger(split[0]), new BigInteger(split[1]));
            numerator = simplified.getValue0();
            denominator = simplified.getValue1();
            return;
        }
        if (fraction.contains(".")) {
            final var split = fraction.split("\\.");
            if (split.length != 2) {
                throw new IllegalArgumentException();
            }
            final var num = new BigInteger(fraction.replace(".", ""));
            final var denom = BigDecimal.valueOf(Math.pow(10, split[1].length())).toBigInteger();
            final var simplified = simplify(num, denom);
            numerator = simplified.getValue0();
            denominator = simplified.getValue1();
            return;
        }
        numerator = new BigInteger(fraction);
        denominator = new BigInteger("1");
    }

    /**
     * Constructor for a Fraction.
     *
     * @param num   String representation of the numerator
     * @param denom String representation of the denominator.
     */
    public Fraction(@NonNull final BigInteger num, @NonNull final BigInteger denom) {
        if (denom.equals(new BigInteger("0"))) {
            throw new ArithmeticException();
        }
        final var simplified = simplify(num, denom);
        numerator = simplified.getValue0();
        denominator = simplified.getValue1();
        infinityType = null;
    }

    private Fraction(@NonNull final InfinityType infinityType) {
        numerator = null;
        denominator = null;
        this.infinityType = infinityType;
    }

    /**
     * Multiply two Fractions.
     *
     * @param f A Fraction.
     * @return The multiplied Fraction.
     */
    @Override
    public Fraction multiply(@NonNull final Fraction f) {
        return new Fraction(numerator.multiply(f.numerator), denominator.multiply(f.denominator));
    }

    /**
     * Divide two Fractions.
     *
     * @param f A Fraction.
     * @return The divided Fraction.
     */
    @Override
    public Fraction divide(@NonNull final Fraction f) {
        return new Fraction(numerator.multiply(f.denominator), denominator.multiply(f.numerator));
    }

    /**
     * Add two Fractions.
     *
     * @param f A Fraction.
     * @return The added Fraction.
     */
    @Override
    public Fraction add(@NonNull final Fraction f) {
        final var num = (numerator.multiply(f.denominator)).add(f.numerator.multiply(denominator));
        return new Fraction(num, denominator.multiply(f.denominator));
    }

    /**
     * Create a new Fraction from an existing one.
     *
     * @param s String representation of the number.
     * @return The new Fraction.
     */
    @Override
    public Fraction create(@NonNull final String s) {
        return new Fraction(s);
    }

    /**
     * Get the value as BigDecimal.
     *
     * @return The value as BigDecimal rounded to 2 decimal places.
     */
    @Override
    public BigDecimal toDecimal() {
        final var num = new BigDecimal(numerator);
        final var denom = new BigDecimal(denominator);
        return num.divide(denom, 2, RoundingMode.HALF_EVEN).stripTrailingZeros();
    }

    @Override
    public Fraction toInfinity(@NonNull final InfinityType type) {
        return new Fraction(type);
    }

    @Override
    public boolean isInfinite() {
        return infinityType != null;
    }

    @Override
    @SuppressWarnings("MagicNumber")
    public int compareTo(@NonNull final Fraction f) {
        if (infinityType != null || f.infinityType != null) {
            return InfinityType.compare(infinityType, f.infinityType);
        }
        final var currV = new BigDecimal(numerator).divide(new BigDecimal(denominator), 20, RoundingMode.HALF_EVEN);
        final var newV = new BigDecimal(f.numerator).divide(new BigDecimal(f.denominator), 20, RoundingMode.HALF_EVEN);
        return currV.compareTo(newV);
    }

    @Override
    @SuppressWarnings("ReturnCount")
    public String toString() {

        if (infinityType != null) {
            return infinityType.toString();
        }
        if (numerator.intValue() == 0) {
            return numerator.toString();
        }

        if ((numerator.doubleValue() / denominator.doubleValue()) < 0) {
            if (denominator.abs().intValue() == 1) {
                return (numerator.abs()).negate().toString();
            }
            return (numerator.abs()).negate() + "/" + denominator.abs();
        }
        if (denominator.abs().intValue() == 1) {
            return numerator.abs().toString();
        }
        return numerator.abs() + "/" + denominator.abs();
    }

    private Pair<BigInteger, BigInteger> simplify(@NonNull final BigInteger num, @NonNull final BigInteger denom) {

        if (num.equals(new BigInteger("0"))) {
            return new Pair<>(new BigInteger("0"), new BigInteger("1"));
        }

        var gcd = new BigInteger("2");
        var result = new Pair<>(num, denom);
        while (gcd.intValue() > 1) {
            gcd = gcd(result.getValue0(), result.getValue1());

            final var newNum = result.getValue0().divide(gcd);
            final var newDenom = result.getValue1().divide(gcd);
            result = new Pair<>(newNum, newDenom);
        }
        return result;
    }

    private BigInteger gcd(@NonNull final BigInteger num, @NonNull final BigInteger denom) {

        var first = num.abs();
        var second = denom.abs();
        final int c = first.compareTo(second);
        if (c == 0) {
            return first;
        } else if (c < 0) {
            first = denom.abs();
            second = num.abs();
        }

        BigInteger result;
        while (true) {
            final var remainder = first.subtract((first.divide(second)).multiply(second));
            first = second;
            result = second;
            second = remainder;

            if (remainder.equals(new BigInteger("0"))) {
                break;
            }
        }
        return result;
    }
}
