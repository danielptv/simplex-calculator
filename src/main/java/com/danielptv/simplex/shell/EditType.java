package com.danielptv.simplex.shell;

public enum EditType {
    EDIT("Edit"),
    CONTINUE("Continue");
    private final String value;

    EditType(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
