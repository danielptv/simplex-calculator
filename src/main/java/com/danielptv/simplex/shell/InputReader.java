package com.danielptv.simplex.shell;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.jline.reader.LineReader;

@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class InputReader {
    private final LineReader lineReader;
    private final ShellHelper shellHelper;

    public String prompt(final String prompt, final PromptColor color) {
        if (color != null) {
            return lineReader.readLine(shellHelper.getColored(prompt + ": ", color));
        }
        return lineReader.readLine(prompt + ": ");
    }

    public String promptUntilValid(final String prompt, final String pattern, final String... patterns) {
        var validInput = true;
        var answer = "";
        while (true) {
            if (validInput) {
                answer = prompt(prompt, null);
            } else {
                answer = prompt(prompt, PromptColor.RED);
            }
            if (answer.matches(pattern)) {
                return answer;
            }
            for (String p : patterns) {
                if (answer.matches(p)) {
                    return answer;
                }
            }
            validInput = false;
        }
    }
}
