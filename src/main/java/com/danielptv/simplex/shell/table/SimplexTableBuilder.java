package com.danielptv.simplex.shell.table;

import org.springframework.shell.table.AbsoluteWidthSizeConstraints;
import org.springframework.shell.table.Aligner;
import org.springframework.shell.table.BorderSpecification;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.CellMatcher;
import org.springframework.shell.table.CellMatchers;
import org.springframework.shell.table.DefaultFormatter;
import org.springframework.shell.table.DelimiterTextWrapper;
import org.springframework.shell.table.Formatter;
import org.springframework.shell.table.SizeConstraints;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableModel;
import org.springframework.shell.table.TextWrapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.springframework.shell.table.BorderSpecification.BOTTOM;
import static org.springframework.shell.table.BorderSpecification.INNER_VERTICAL;
import static org.springframework.shell.table.BorderSpecification.LEFT;
import static org.springframework.shell.table.BorderSpecification.OUTLINE;
import static org.springframework.shell.table.SimpleHorizontalAligner.center;

@Component
public class SimplexTableBuilder {
    private final Constructor<Table> tableConstructor = getTableConstructor();
    private final Constructor<BorderSpecification> borderConstructor = getBorderConstructor();

    @SuppressWarnings("MagicNumber")
    public Table build(final TableModel model, final boolean twoObjFunc, final int[] columnWidths) {
        try {
            // formatters
            final var formatters = new LinkedHashMap<CellMatcher, Formatter>();
            formatters.put(CellMatchers.table(), new DefaultFormatter());

            // size constraints
            if (columnWidths.length != model.getColumnCount()) {
                throw new IllegalArgumentException();
            }
            final var sizeConstraints = new LinkedHashMap<CellMatcher, SizeConstraints>();
            for (int i = 0; i < model.getColumnCount(); ++i) {
                sizeConstraints.put(CellMatchers.column(i), new AbsoluteWidthSizeConstraints(columnWidths[i]));
            }

            // wrappers
            final var wrappers = new LinkedHashMap<CellMatcher, TextWrapper>();
            wrappers.put(CellMatchers.table(), new DelimiterTextWrapper());

            // aligners
            final var aligners = new LinkedHashMap<CellMatcher, Aligner>();
            aligners.put(CellMatchers.table(), center);

            // borders
            final var basicBorder1 = borderConstructor.newInstance(
                    0,
                    0,
                    model.getRowCount(),
                    model.getColumnCount(),
                    OUTLINE,
                    BorderStyle.fancy_light
            );
            final var basicBorder2 = borderConstructor.newInstance(
                    0,
                    0,
                    model.getRowCount(),
                    model.getColumnCount(),
                    INNER_VERTICAL,
                    BorderStyle.fancy_light
            );
            final var rHSBorder = borderConstructor.newInstance(
                    0,
                    model.getColumnCount() - 1,
                    model.getRowCount(),
                    model.getColumnCount(),
                    LEFT,
                    BorderStyle.fancy_double
            );
            final var columnHeadersBorder = borderConstructor.newInstance(
                    0,
                    0,
                    1,
                    model.getColumnCount(),
                    BOTTOM,
                    BorderStyle.fancy_double
            );
            final var objFuncBorder = borderConstructor.newInstance(
                    0,
                    0,
                    2,
                    model.getColumnCount(),
                    BOTTOM,
                    BorderStyle.fancy_double
            );
            final var borders = new ArrayList<>(List.of(
                    basicBorder1,
                    basicBorder2,
                    rHSBorder,
                    columnHeadersBorder,
                    objFuncBorder
            ));
            if (twoObjFunc) {
                borders.add(borderConstructor.newInstance(
                        0,
                        0,
                        3,
                        model.getColumnCount(),
                        BOTTOM,
                        BorderStyle.fancy_double
                ));
            }

            return tableConstructor.newInstance(
                    model,
                    formatters,
                    sizeConstraints,
                    wrappers,
                    aligners,
                    borders
            );
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor<Table> getTableConstructor() {
        try {
            final var constructor = Table.class.getDeclaredConstructor(
                    TableModel.class,
                    LinkedHashMap.class,
                    LinkedHashMap.class,
                    LinkedHashMap.class,
                    LinkedHashMap.class,
                    List.class
            );
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor<BorderSpecification> getBorderConstructor() {
        try {
            final var constructor = BorderSpecification.class.getDeclaredConstructor(
                    int.class,
                    int.class,
                    int.class,
                    int.class,
                    int.class,
                    BorderStyle.class
            );
            constructor.setAccessible(true);
            return constructor;

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
