package com.danielptv.simplex.number;

/**
 * Enum for the types of infinity.
 */
public enum InfinityType {
    /**
     * Positive infinity.
     */
    POSITIVE("inf"),
    /**
     * Negative infinity.
     */
    NEGATIVE("minf");
    private final String value;

    InfinityType(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @SuppressWarnings("ReturnCount")
    static int compare(final InfinityType o1, final InfinityType o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 != null && o2 != null) {
            return o1.equals(o2) ? 0 : o1.equals(POSITIVE) ? 1 : -1;
        }
        if (o1 != null) {
            return o1.equals(POSITIVE) ? 1 : -1;
        }
        return o2.equals(NEGATIVE) ? 1 : -1;
    }
}
