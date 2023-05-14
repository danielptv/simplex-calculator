package com.danielptv.simplex.service;

import com.danielptv.simplex.entity.Phase;
import com.danielptv.simplex.entity.Row;
import com.danielptv.simplex.entity.SimplexTable;
import com.danielptv.simplex.number.CalculableImpl;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.danielptv.simplex.entity.SpecialSolutionType.INFEASIBLE;
import static com.danielptv.simplex.entity.SpecialSolutionType.MULTIPLE_SOLUTIONS;
import static com.danielptv.simplex.entity.SpecialSolutionType.UNBOUNDED;

@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public final class TwoPhaseSimplex<T extends CalculableImpl<T>> {
    private final T generator;
    private final TableCalcService<T> calcService;
    private final TableExtensionService<T> extensionService;

    public List<Phase<T>> calc(final SimplexTable<T> simplexTable) {
        final var result = new ArrayList<Phase<T>>(2);
        var table = simplexTable;

        // optional phase 1
        if (calcService.isInvalid(table)) {
            final var phase = phase1(table);
            result.add(phase);

            if (phase.specialSolutionType() != null) {
                return result;
            }
            table = extensionService.removeExtension(phase.getLastTable());
        }

        // primary simplex algorithm
        result.add(phase2(table, result.isEmpty()));
        return result;
    }

    Phase<T> phase1(final SimplexTable<T> simplexTable) {
        final var tables = new ArrayList<SimplexTable<T>>();

        // add helper columns
        var table = extensionService.buildExtension(simplexTable);

        // transform table to its canonical form
        table = transformToCanonical(table);
        tables.add(new SimplexTable<>(table, "INITIAL TABLE"));

        // transform table until acceptable for primary simplex
        for (int count = 1; calcService.isInvalid(table); ++count) {
            table = transform(table);
            tables.add(new SimplexTable<>(table, "ITERATION " + count));

            if (calcService.isOptimal(table) && !table.rHS().get(0).equals(generator.create("0"))) {
                return new Phase<>(tables, INFEASIBLE, false);
            }
        }
        return new Phase<>(tables, null, false);
    }

    Phase<T> phase2(final SimplexTable<T> simplexTable, final boolean singlePhase) {
        final var tables = new ArrayList<SimplexTable<T>>();
        var table = simplexTable;

        tables.add(new SimplexTable<>(table, "INITIAL TABLE"));

        // transform the table until an optimal solution is found
        for (int count = 1; !calcService.isOptimal(table); ++count) {
            table = transform(table);
            tables.add(new SimplexTable<>(table, "ITERATION " + count));

            final var pivotElement = table.pivot();
            if (pivotElement.value().isInfinite()) {
                return new Phase<>(tables, UNBOUNDED, singlePhase);
            }
        }
        if (calcService.isDegenerate(table)) {
            return new Phase<>(tables, MULTIPLE_SOLUTIONS, singlePhase);
        }
        return new Phase<>(tables, null, singlePhase);
    }

    @SuppressWarnings("LambdaBodyLength")
    public SimplexTable<T> transform(final SimplexTable<T> table) {
        final var rowCount = table.rHS().size();
        final var columnHeaders = table.columnHeaders();
        final var pivot = table.pivot();

        var rowHeaders = table.rowHeaders();
        rowHeaders = calcService.updateRowHeaders(columnHeaders, table.rowHeaders(), pivot);

        // find divisor and divide
        final var divisor = pivot.value();
        final var lHS = new ArrayList<>(table.lHS());
        lHS.set(pivot.row(), lHS.get(pivot.row()).divideRow(divisor));

        final var rHS = new ArrayList<>(table.rHS());
        rHS.set(pivot.row(), rHS.get(pivot.row()).divide(divisor));

        //find factors
        final List<T> factors;
        factors = lHS.stream()
                .map(e -> e.getElement(pivot.column()).multiply(generator.create("-1")))
                .toList();

        //iterate
        final List<T> finalFactors = factors;
        IntStream.range(0, rowCount)
                .forEach(row -> {
                    if (row != pivot.row()) {
                        final var pivotTemp = new Row<>(lHS.get(pivot.row()));
                        final var currRow = new Row<>(lHS.get(row));

                        lHS.set(row, pivotTemp.multiplyRow(pivotTemp, finalFactors.get(row)));
                        lHS.set(row, lHS.get(row).addRow(currRow));

                        final var currVal = table.rHS().get(row);
                        rHS.set(row, rHS.get(pivot.row()).multiply(finalFactors.get(row)).add(currVal));
                    }
                });

        final var newPivot = calcService.setPivot(lHS, rHS, table.helperColumns() != 0);
        return new SimplexTable<>(
                table.title(),
                lHS,
                rHS,
                newPivot,
                columnHeaders,
                rowHeaders,
                table.helperColumns());
    }

    public SimplexTable<T> transformToCanonical(final SimplexTable<T> table) {

        if (table.helperColumns() == 0) {
            throw new IllegalArgumentException();
        }

        final var extensionSize = table.helperColumns();
        final var lHs = new ArrayList<>(table.lHS());
        final var rHS = new ArrayList<>(table.rHS());
        final var rowHeaders = table.rowHeaders();

        IntStream.range(0, rowHeaders.size())
                .forEach(i -> {
                    if (rowHeaders.get(i).contains("h")) {
                        lHs.set(0, lHs.get(0).addRow(lHs.get(i).invertRow()));
                        rHS.set(0, rHS.get(0).add(rHS.get(i).multiply(generator.create("-1"))));
                    }
                });

        final var pivot = calcService.setPivot(lHs, rHS, true);
        return new SimplexTable<>(
                table.title(),
                lHs,
                rHS,
                pivot,
                table.columnHeaders(),
                rowHeaders,
                extensionSize
        );
    }
}
