package com.danielptv.simplex.number;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class RoundedDecimal implements CalculableImpl<RoundedDecimal> {
    @EqualsAndHashCode.Include
    @Getter
    private final BigDecimal value;
    @EqualsAndHashCode.Include
    @Getter
    private final InfinityType infinityType;
    @Getter
    private final int mantissaLength;

    public RoundedDecimal(final int mantissaLength) {
        value = new BigDecimal("0");
        this.mantissaLength = mantissaLength;
        infinityType = null;
    }

    @SuppressWarnings("MagicNumber")
    public RoundedDecimal(final String s, final int mantissaLength) {
        infinityType = null;
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

    public RoundedDecimal(final BigDecimal b, final int mantissaLength) {
        this.mantissaLength = mantissaLength;
        value = round(b);
        infinityType = null;
    }

    private RoundedDecimal(final InfinityType infinityType) {
        value = null;
        this.infinityType = infinityType;
        mantissaLength = 0;
    }

    @Override
    public String toString() {
        if (infinityType != null) {
            return infinityType.toString();
        }
        return value.toPlainString();
    }

    @Override
    public int compareTo(@NonNull final RoundedDecimal o) {
        if (infinityType != null || o.infinityType != null) {
            return InfinityType.compare(infinityType, o.infinityType);
        }
        return value.compareTo(o.value);
    }

    @Override
    public RoundedDecimal multiply(final RoundedDecimal d) {
        if (this.isInfinite() || d.isInfinite()) {
            return new RoundedDecimal(InfinityType.calculate(infinityType, d.infinityType));
        }
        return new RoundedDecimal(value.multiply(d.value), d.mantissaLength);
    }

    @Override
    @SuppressWarnings("MagicNumber")
    public RoundedDecimal divide(final RoundedDecimal d) {
        if (this.isInfinite() || d.isInfinite()) {
            return new RoundedDecimal(InfinityType.calculate(infinityType, d.infinityType));
        }
        return new RoundedDecimal(value.divide(d.value,
                d.value.precision() + value.precision() + 20, RoundingMode.HALF_EVEN), d.mantissaLength);
    }

    @Override
    public RoundedDecimal add(final RoundedDecimal d) {
        if (this.isInfinite() || d.isInfinite()) {
            return new RoundedDecimal(InfinityType.calculate(infinityType, d.infinityType));
        }
        return new RoundedDecimal(value.add(d.value), d.mantissaLength);
    }

    @Override
    public RoundedDecimal create(final String s) {
        return new RoundedDecimal(s, this.mantissaLength);
    }

    @Override
    public BigDecimal toDecimal() {
        return value.setScale(2, RoundingMode.HALF_EVEN).stripTrailingZeros();
    }

    @Override
    public RoundedDecimal toInfinity(final InfinityType type) {
        return new RoundedDecimal(type);
    }

    @Override
    public boolean isInfinite() {
        return infinityType != null;
    }

    @SuppressWarnings("MagicNumber")
    private BigDecimal round(final BigDecimal d) {

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
}
