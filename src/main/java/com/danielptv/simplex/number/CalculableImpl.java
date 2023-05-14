package com.danielptv.simplex.number;

import java.math.BigDecimal;

public interface CalculableImpl<T extends CalculableImpl<T>> extends Comparable<T> {
    T multiply(T c);
    T divide(T c);
    T add(T c);
    T create(String s);
    BigDecimal toDecimal();
    T toInfinity(InfinityType infinityType);
    boolean isInfinite();
}
