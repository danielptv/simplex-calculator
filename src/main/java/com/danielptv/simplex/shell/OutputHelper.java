package com.danielptv.simplex.shell;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
@Component
public class OutputHelper {
    @Value("${shell.out.info}")
    private String infoColor;
    @Value("${shell.out.success}")
    private String successColor;
    @Value("${shell.out.warning}")
    private String warningColor;
    @Value("${shell.out.error}")
    private String errorColor;
    private final Terminal terminal;

    public String getColored(final String message, final PromptColor color) {
        return (new AttributedStringBuilder())
                .append(message, AttributedStyle.DEFAULT.foreground(color.toJlineAttributedStyle()))
                .toAnsi();
    }

    public String getInfoMessage(final String message) {
        return getColored(message, PromptColor.valueOf(infoColor));
    }
    public String getSuccessMessage(final String message) {
        return getColored(message, PromptColor.valueOf(successColor));
    }
    public String getWarningMessage(final String message) {
        return getColored(message, PromptColor.valueOf(warningColor));
    }
    public String getErrorMessage(final String message) {
        return getColored(message, PromptColor.valueOf(errorColor));
    }

    public void print(final String message) {
        print(message, null);
    }
    public void print(final String message, final PromptColor color) {
        String toPrint = message;
        if (color != null) {
            toPrint = getColored(message, color);
        }
        terminal.writer().println(toPrint);
        terminal.flush();
    }
}
