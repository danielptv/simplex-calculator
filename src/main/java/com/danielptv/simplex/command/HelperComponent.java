package com.danielptv.simplex.command;

import com.danielptv.simplex.shell.EditType;
import com.danielptv.simplex.shell.InputResult;
import com.danielptv.simplex.shell.SimplexInput;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.style.TemplateExecutor;

import java.util.Map;
import java.util.Optional;

@SuppressFBWarnings("EI_EXPOSE_REP2")
@ShellComponent
@RequiredArgsConstructor
public class HelperComponent {
    private static final String EDIT_PROBLEM_TITLE = "Edit problem";
    private static final Map<String, String> EDIT_PROBLEM_SELECTION = Map.of(
            EditType.CONTINUE.toString(),
            "Continue to calculation",
            EditType.EDIT.toString(),
            "Edit the linear problem"
    );
    private final Terminal terminal;
    private final ResourceLoader resourceLoader;
    private final ObjectProvider<TemplateExecutor> templateExecutorProvider;

    public String singleSelector(final String name, final Map<String, String> data) {
        final var items = data.entrySet().stream()
                .map(entry -> SelectorItem.of(entry.getKey(), entry.getValue()))
                .toList();
        final var component = new SingleItemSelector<>(terminal,
                items, name, null);
        component.setResourceLoader(resourceLoader);
        component.setTemplateExecutor(templateExecutorProvider.getObject());
        final var context = component.run(SingleItemSelector.SingleItemSelectorContext.empty());
        return context.getResultItem().flatMap(si -> Optional.ofNullable(si.getName())).orElse(null);
    }

    public EditType editProblem() {
        final var result = singleSelector(EDIT_PROBLEM_TITLE, EDIT_PROBLEM_SELECTION);
        return result.equals(EditType.CONTINUE.toString()) ? EditType.CONTINUE : EditType.EDIT;
    }

    public InputResult simplexInput(
            final String name,
            final int varCount,
            final boolean isObjFunction,
            final boolean minimize
    ) {
        return simplexInput(name, varCount, isObjFunction, minimize, null);
    }

    public InputResult simplexInput(
            final String name,
            final int varCount,
            final boolean isObjFunction,
            final boolean minimize,
            final InputResult defaultValue
    ) {
        final var component = new SimplexInput(terminal, name, varCount, isObjFunction, minimize, defaultValue);
        component.setResourceLoader(resourceLoader);
        component.setTemplateExecutor(templateExecutorProvider.getObject());
        final var context = component.run(SimplexInput.SimplexInputContext.empty());
        return context.getResultValue();
    }
}
