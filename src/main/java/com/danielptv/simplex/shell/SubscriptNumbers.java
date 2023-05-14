package com.danielptv.simplex.shell;

public enum SubscriptNumbers {
    ZERO("₀"),
    ONE("₁"),
    TWO("₂"),
    THREE("₃"),
    FOUR("₄"),
    FIVE("₅"),
    SIX("₆"),
    SEVEN("₇"),
    EIGHT("₈"),
    NINE("₉"),
    TEN("₁₀");

    private final String value;

    SubscriptNumbers(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
