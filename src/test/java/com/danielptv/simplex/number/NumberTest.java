package com.danielptv.simplex.number;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
@Tag("number")
@DisplayName("Numbers Test")
final class NumberTest {
    public static final String INFINITY = "inf";
    public static final String MIN_INFINITY = "minf";

    private NumberTest() {

    }

    @Nested
    @DisplayName("Calculations with RoundedDecimal")
    class RoundedDecimalTest {
        private static final String DECIMAL_1 = "23.45345";
        private static final String DECIMAL_2 = "140.4565";
        private static final String ADD_RESULT = "164";
        private static final String MULTIPLY_RESULT = "3290";
        private static final String DIVIDE_RESULT_1 = "0.168";
        private static final String DIVIDE_RESULT_2 = "5.96";
        private static final int MANTISSA = 3;

        @Test
        @DisplayName("Add two RoundedDecimal")
        void add() {
            // arrange
            final var dec1 = new RoundedDecimal(DECIMAL_1, MANTISSA);
            final var dec2 = new RoundedDecimal(DECIMAL_2, MANTISSA);

            // act
            final var result1 = dec1.add(dec2);
            final var result2 = dec2.add(dec1);

            // assert
            assertThat(result1).isNotNull();
            assertThat(result1.isInfinite()).isFalse();
            assertThat(result2).isNotNull();
            assertThat(result2.isInfinite()).isFalse();

            assertThat(result1).isEqualTo(result2);
            assertThat(result1.toString()).isEqualTo(ADD_RESULT);
        }

        @Test
        @DisplayName("Multiply two RoundedDecimal")
        void multiply() {
            // arrange
            final var dec1 = new RoundedDecimal(DECIMAL_1, MANTISSA);
            final var dec2 = new RoundedDecimal(DECIMAL_2, MANTISSA);

            // act
            final var result1 = dec1.multiply(dec2);
            final var result2 = dec2.multiply(dec1);

            // assert
            assertThat(result1).isNotNull();
            assertThat(result1.isInfinite()).isFalse();
            assertThat(result2).isNotNull();
            assertThat(result2.isInfinite()).isFalse();

            assertThat(result1).isEqualTo(result2);
            assertThat(result1.toString()).isEqualTo(MULTIPLY_RESULT);
        }

        @Test
        @DisplayName("Divide two RoundedDecimal")
        void divide() {
            // arrange
            final var dec1 = new RoundedDecimal(DECIMAL_1, MANTISSA);
            final var dec2 = new RoundedDecimal(DECIMAL_2, MANTISSA);

            // act
            final var result1 = dec1.divide(dec2);
            final var result2 = dec2.divide(dec1);

            // assert
            assertThat(result1).isNotNull();
            assertThat(result1.isInfinite()).isFalse();
            assertThat(result2).isNotNull();
            assertThat(result2.isInfinite()).isFalse();

            assertThat(result1).isNotEqualTo(result2);
            assertThat(result1.toString()).isEqualTo(DIVIDE_RESULT_1);
            assertThat(result2.toString()).isEqualTo(DIVIDE_RESULT_2);
        }

        @Test
        @DisplayName("Calculate with infinity for RoundedDecimal")
        void calculateWithInfinity() {
            // arrange
            final var dec = new RoundedDecimal(DECIMAL_1, MANTISSA);
            final var inf = dec.toInfinity(InfinityType.POSITIVE);
            final var minf = dec.toInfinity(InfinityType.NEGATIVE);

            // act
            final var addInf = dec.add(inf);
            final var addMinf = dec.add(minf);
            final var multiplyInf = dec.multiply(inf);
            final var multiplyMinf = minf.multiply(dec);
            final var divideInf = inf.divide(dec);
            final var divideMinf = minf.divide(dec);

            // assert
            assertThat(addInf).isNotNull();
            assertThat(addMinf).isNotNull();
            assertThat(multiplyInf).isNotNull();
            assertThat(multiplyMinf).isNotNull();
            assertThat(divideInf).isNotNull();
            assertThat(divideMinf).isNotNull();

            assertThat(addInf).isEqualTo(multiplyInf).isEqualTo(divideInf);
            assertThat(addMinf).isEqualTo(multiplyMinf).isEqualTo(divideMinf);
            assertThat(addInf.toString()).isEqualTo(INFINITY);
            assertThat(addMinf.toString()).isEqualTo(MIN_INFINITY);
        }
    }

    @Nested
    @DisplayName("Calculations with Fraction")
    class FractionTest {
        private static final String FRACTION_1 = "23/45";
        private static final String FRACTION_2 = "-140/65";
        private static final String ADD_RESULT = "-961/585";
        private static final String MULTIPLY_RESULT = "-644/585";
        private static final String DIVIDE_RESULT_1 = "-299/1260";
        private static final String DIVIDE_RESULT_2 = "-1260/299";

        @Test
        @DisplayName("Add two Fraction")
        void add() {
            // arrange
            final var frac1 = new Fraction(FRACTION_1);
            final var frac2 = new Fraction(FRACTION_2);

            // act
            final var result1 = frac1.add(frac2);
            final var result2 = frac2.add(frac1);

            // assert
            assertThat(result1).isNotNull();
            assertThat(result1.isInfinite()).isFalse();
            assertThat(result2).isNotNull();
            assertThat(result2.isInfinite()).isFalse();

            assertThat(result1).isEqualTo(result2);
            assertThat(result1.toString()).isEqualTo(ADD_RESULT);
        }

        @Test
        @DisplayName("Multiply two Fraction")
        void multiply() {
            // arrange
            final var frac1 = new Fraction(FRACTION_1);
            final var frac2 = new Fraction(FRACTION_2);

            // act
            final var result1 = frac1.multiply(frac2);
            final var result2 = frac2.multiply(frac1);

            // assert
            assertThat(result1).isNotNull();
            assertThat(result1.isInfinite()).isFalse();
            assertThat(result2).isNotNull();
            assertThat(result2.isInfinite()).isFalse();

            assertThat(result1).isEqualTo(result2);
            assertThat(result1.toString()).isEqualTo(MULTIPLY_RESULT);
        }

        @Test
        @DisplayName("Divide two Fraction")
        void divide() {
            // arrange
            final var frac1 = new Fraction(FRACTION_1);
            final var frac2 = new Fraction(FRACTION_2);

            // act
            final var result1 = frac1.divide(frac2);
            final var result2 = frac2.divide(frac1);

            // assert
            assertThat(result1).isNotNull();
            assertThat(result1.isInfinite()).isFalse();
            assertThat(result2).isNotNull();
            assertThat(result2.isInfinite()).isFalse();

            assertThat(result1).isNotEqualTo(result2);
            assertThat(result1.toString()).isEqualTo(DIVIDE_RESULT_1);
            assertThat(result2.toString()).isEqualTo(DIVIDE_RESULT_2);
        }

        @Test
        @DisplayName("Calculate with infinity for RoundedDecimal")
        void calculateWithInfinity() {
            // arrange
            final var fraction = new Fraction(FRACTION_1);
            final var inf = fraction.toInfinity(InfinityType.POSITIVE);
            final var minf = fraction.toInfinity(InfinityType.NEGATIVE);

            // act
            final var addInf = fraction.add(inf);
            final var addMinf = fraction.add(minf);
            final var multiplyInf = fraction.multiply(inf);
            final var multiplyMinf = minf.multiply(fraction);
            final var divideInf = inf.divide(fraction);
            final var divideMinf = minf.divide(fraction);

            // assert
            assertThat(addInf).isNotNull();
            assertThat(addMinf).isNotNull();
            assertThat(multiplyInf).isNotNull();
            assertThat(multiplyMinf).isNotNull();
            assertThat(divideInf).isNotNull();
            assertThat(divideMinf).isNotNull();

            assertThat(addInf).isEqualTo(multiplyInf).isEqualTo(divideInf);
            assertThat(addMinf).isEqualTo(multiplyMinf).isEqualTo(divideMinf);
            assertThat(addInf.toString()).isEqualTo(INFINITY);
            assertThat(addMinf.toString()).isEqualTo(MIN_INFINITY);
        }
    }
}
