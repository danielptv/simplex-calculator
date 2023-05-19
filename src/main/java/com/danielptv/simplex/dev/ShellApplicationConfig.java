package com.danielptv.simplex.dev;

import com.danielptv.simplex.shell.PromptColor;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.jline.PromptProvider;

@Configuration
public class ShellApplicationConfig implements PromptProvider {
    @Override
    public final AttributedString getPrompt() {
        return new AttributedString(
                "simplex:>",
                AttributedStyle.DEFAULT.foreground(PromptColor.YELLOW.toJlineAttributedStyle())
        );
    }
}
