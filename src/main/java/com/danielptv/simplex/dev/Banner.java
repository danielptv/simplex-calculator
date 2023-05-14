package com.danielptv.simplex.dev;

import com.github.lalyos.jfiglet.FigletFont;

import java.io.IOException;

public final class Banner {
    public static final String TEXT = """
            %s
            Version              v2.0.0
            Author               %s
            GitHub               %s
            """
            .formatted(
                    getFiglet("simplex-calc"),
                    "Daniel Purtov",
                    "https://github.com/danielptv/simplex-calculator"
            );

    @SuppressWarnings("ImplicitCallToSuper")
    private Banner() {
    }

    public static String getFiglet(final String value) {
        try {
            return FigletFont.convertOneLine(value);
        } catch (final IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

}
