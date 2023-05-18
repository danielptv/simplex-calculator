package com.danielptv.simplex.shell;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.support.AbstractTextComponent;
import org.springframework.util.StringUtils;
import org.springframework.shell.component.support.AbstractTextComponent.TextComponentContext.MessageLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class SimplexInput extends AbstractTextComponent<InputResult, SimplexInput.SimplexInputContext> {
    static final String NUMBER_PATTERN = "-?((\\d+(\\.\\d+)?)|(\\d+(/[0-9]*[1-9][0-9]*)?))";
    private final String standardPattern;
    private final String relSignPattern;
    private SimplexInputContext currentContext;
    private final int varCount;
    private final boolean isObjFunction;
    private final boolean minimize;
    private final InputResult inputResult;
    private final InputResult defaultValue;

    public SimplexInput(
            final Terminal terminal,
            final String name,
            final int varCount,
            final boolean isObjFunction,
            final boolean minimize,
            final InputResult defaultValue
    ) {
        super(terminal, name, null);
        setRenderer(new DefaultRenderer());
        setTemplateLocation("classpath:BOOT-INF/classes/simplex-input-default.stg");

        this.varCount = varCount;
        this.isObjFunction = isObjFunction;
        this.minimize = minimize;
        this.inputResult = defaultValue != null ? defaultValue : new InputResult();
        this.defaultValue = defaultValue;
        if (isObjFunction) {
            this.standardPattern = String.format("^%s((,%s){0,%d})$", NUMBER_PATTERN, NUMBER_PATTERN, varCount - 1);
            this.relSignPattern = null;
        } else {
            this.standardPattern = String.format("^%s((,%s){0,%d})$", NUMBER_PATTERN, NUMBER_PATTERN, varCount);
            this.relSignPattern = String.format(
                    "^%s((,%s){0,%d})[<>=]%s$",
                    NUMBER_PATTERN,
                    NUMBER_PATTERN,
                    varCount - 1,
                    NUMBER_PATTERN
            );
        }
    }

    @Override
    public SimplexInputContext getThisContext(final ComponentContext<?> context) {
        if (context != null && currentContext == context) {
            return currentContext;
        }

        currentContext = SimplexInputContext.empty();
        currentContext.setName(getName());
        if (defaultValue != null) {
            currentContext.setInput(defaultValue.getRawInput());
        }
        if (context != null) {
            context.stream().forEach(e -> currentContext.put(e.getKey(), e.getValue()));
        }
        return currentContext;
    }

    @SuppressWarnings("CyclomaticComplexity")
    @Override
    protected boolean read(
            final BindingReader bindingReader,
            final KeyMap<String> keyMap,
            final SimplexInputContext context
    ) {
        final var operation = bindingReader.readBinding(keyMap);
        if (operation == null) {
            return true;
        }
        String input;
        switch (operation) {
            case OPERATION_CHAR -> {
                final var lastBinding = bindingReader.getLastBinding();
                input = context.getInput();
                if (input == null) {
                    input = lastBinding;
                } else {
                    input = input + lastBinding;
                }
                context.setInput(input);
                checkFunction(input, context);
                context.setResult(inputResult);
            }
            case OPERATION_BACKSPACE -> {
                input = context.getInput();
                if (StringUtils.hasLength(input)) {
                    input = input.length() > 1 ? input.substring(0, input.length() - 1) : null;
                }
                context.setInput(input);
                checkFunction(input, context);
                context.setResult(inputResult);
            }
            case OPERATION_EXIT -> {
                input = context.getInput();
                final var result = checkFunction(input, context);
                context.setResult(inputResult);
                final var expectedSize = isObjFunction ? varCount : varCount + 2;
                if (result && inputResult.getValues().size() == expectedSize) {
                    context.setResultValue(context.getResult());
                    return true;
                }
            }
            default -> {
            }
        }
        return false;
    }

    private boolean checkFunction(final String input, final SimplexInputContext context) {
        if (!StringUtils.hasText(input)) {
            context.setMessage(null);
            return false;
        }
        if (!input.matches(standardPattern) && isObjFunction ||
                !input.matches(standardPattern) && !input.matches(relSignPattern)) {
            inputResult.setRepresentation("Invalid function");
            context.setMessage("Invalid function", MessageLevel.ERROR);
            return false;
        } else {
            final var current = buildCurrent(input);
            inputResult.setRepresentation(current);
            context.setMessage(current, MessageLevel.INFO);
            return true;
        }
    }

    @SuppressWarnings({"CyclomaticComplexity", "NPathComplexity"})
    private String buildCurrent(final String input) {
        final var sb = new StringBuilder();
        if (isObjFunction) {
            final var parts = Arrays.stream(input.split(",")).toList();
            sb.append(String.format("%s f(x) = ", minimize ? "min" : "max"));
            for (int part = 0; part < parts.size(); ++part) {
                final var number = parts.get(part).contains("/") || parts.get(part).contains("-")
                        ? "(" + parts.get(part) + ")"
                        : parts.get(part);
                sb.append(String.format("%s•x%s", number, SubscriptNumbers.toValue(part + 1)));
                if (part < parts.size() - 1) {
                    sb.append(" + ");
                }
            }
            inputResult.setValues(parts);
            inputResult.setRawInput(input);
            return sb.toString();
        }

        final ArrayList<String> parts;
        if (input.matches(standardPattern)) {
            parts = new ArrayList<>(Arrays.stream(input.split(",")).toList());
            parts.add("<");
        } else {
            final var sign = input.contains(">") ? ">" : input.contains("<") ? "<" : "=";
            final var newInput = input.contains(">")
                    ? input.replace(">", ",")
                    : input.contains("<")
                    ? input.replace("<", ",")
                    : input.replace("=", ",");
            parts = new ArrayList<>(Arrays.stream(newInput.split(",")).toList());
            parts.add(sign);
        }
        inputResult.setValues(parts);

        for (int part = 0; part < parts.size() - 1; ++part) {
            final var number = parts.get(part).contains("/") || parts.get(part).contains("-")
                    ? "(" + parts.get(part) + ")"
                    : parts.get(part);
            if (part != 0 && part < varCount) {
                sb.append(" + ");
            }
            if (part != 0 && part == varCount) {
                final var relationSign = parts.get(parts.size() - 1);
                sb.append(String.format(" %s ", relationSign.equals("=") ? "=" : relationSign.equals("<") ? "≤" : "≥"));
            }
            sb.append(number);
            if (part < varCount) {
                sb.append(String.format("•x%s", SubscriptNumbers.toValue(part + 1)));
            }
        }
        inputResult.setRawInput(input);
        return sb.toString();
    }

    @SuppressWarnings("ClassMemberImpliedModifier")
    public interface SimplexInputContext
            extends AbstractTextComponent.TextComponentContext<InputResult, SimplexInputContext> {
        InputResult getResult();

        void setResult(InputResult result);

        static SimplexInputContext empty() {
            return new DefaultSimplexInputContext(new InputResult());
        }
    }

    private static class DefaultSimplexInputContext
            extends AbstractTextComponent.BaseTextComponentContext<InputResult, SimplexInput.SimplexInputContext>
            implements SimplexInputContext {
        private InputResult result;

        DefaultSimplexInputContext(final InputResult result) {
            this.result = result;
        }

        @Override
        public InputResult getResult() {
            return result;
        }

        @Override
        public void setResult(final InputResult result) {
            this.result = result;
        }

        @Override
        public Map<String, Object> toTemplateModel() {
            final Map<String, Object> attributes = super.toTemplateModel();
            attributes.put("result", getResult());
            final Map<String, Object> model = new HashMap<>();
            model.put("model", attributes);
            return model;
        }

    }

    private class DefaultRenderer implements Function<SimplexInputContext, List<AttributedString>> {
        @Override
        public List<AttributedString> apply(final SimplexInputContext context) {
            return renderTemplateResource(context.toTemplateModel());
        }
    }
}
