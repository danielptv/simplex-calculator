package com.danielptv.simplex.command;

import com.danielptv.simplex.dev.Banner;
import com.danielptv.simplex.shell.ShellHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Version;

@RequiredArgsConstructor
@ShellComponent
public class AppVersion implements Version.Command {
    private final ShellHelper shellHelper;
    @ShellMethod(key = {"version", "info"}, value = "Show version info.", group = "Built-In Commands")
    public void version() {
        shellHelper.print(Banner.TEXT);
    }
}
