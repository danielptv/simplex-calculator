package com.danielptv.simplex.command;

import com.danielptv.simplex.dev.Banner;
import com.danielptv.simplex.shell.OutputHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Version;

@RequiredArgsConstructor
@ShellComponent
public class AppVersion implements Version.Command {
    private final OutputHelper outputHelper;
    @ShellMethod(key = {"version", "info"}, value = "Show version info.", group = "Built-In Commands")
    public void version() {
        outputHelper.print(Banner.TEXT);
    }
}
