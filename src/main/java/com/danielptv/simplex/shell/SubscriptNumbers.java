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
    NINE("₉");

    private final String value;

    SubscriptNumbers(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static String toValue(final int number) {
        final var digits = String.valueOf(number).toCharArray();
        final var result = new StringBuilder();
        for (char c : digits) {
            final var num = SubscriptNumbers.values()[Integer.parseInt(String.valueOf(c))];
            result.append(num);
        }
        return result.toString();
    }
}
