package com.danielptv.simplex.dev;

import com.danielptv.simplex.shell.InputReader;
import com.danielptv.simplex.shell.PromptColor;
import com.danielptv.simplex.shell.ShellHelper;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
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

    @Bean
    ShellHelper shellHelper(@Lazy final Terminal terminal) {
        return new ShellHelper(terminal);
    }

    @Bean
    InputReader inputReader(@Lazy final Terminal terminal, @Lazy final LineReader lineReader) {
        return new InputReader(lineReader, new ShellHelper(terminal));
    }
}
