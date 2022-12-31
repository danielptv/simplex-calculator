package com.danielptv.simplex.service;

import com.danielptv.simplex.number.CalculableImpl;
import com.danielptv.simplex.entity.Phase;
import com.danielptv.simplex.entity.Row;
import com.danielptv.simplex.entity.Table;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.danielptv.simplex.entity.SpecialSolutionType.INFEASIBLE;
import static com.danielptv.simplex.entity.SpecialSolutionType.MULTIPLE_SOLUTIONS;
import static com.danielptv.simplex.entity.SpecialSolutionType.UNBOUNDED;
import static com.danielptv.simplex.presentation.OutputUtils.PHASE_1;
import static com.danielptv.simplex.presentation.OutputUtils.PHASE_2;
import static com.danielptv.simplex.presentation.OutputUtils.SIMPLEX_HEADLINE;
import static com.danielptv.simplex.service.TableCalcService.isDegenerate;
import static com.danielptv.simplex.service.TableCalcService.isOptimal;
import static com.danielptv.simplex.service.TableCalcService.isValid;
import static com.danielptv.simplex.service.TableCalcService.setPivot;
import static com.danielptv.simplex.service.TableCalcService.updateRowHeaders;
import static com.danielptv.simplex.service.TableExtensionService.buildExtension;
import static com.danielptv.simplex.service.TableExtensionService.removeExtension;

/**
 * Service-Class for simplex calculations.
 */
public final class TwoPhaseSimplex {

    private TwoPhaseSimplex() {

    }

    /**
     * Calculate a linear problem.
     *
     * @param simplexTable The initial simplex table.
     * @param <T>          Fraction or RoundedDecimal.
     * @return A List containing the resulting simplex phases.
     */
    public static <T extends CalculableImpl<T>> List<Phase<T>> calc(@NonNull final Table<T> simplexTable) {
        final var result = new ArrayList<Phase<T>>(2);
        var table = simplexTable;

        // optional phase 1
        if (!isValid(table)) {
            final var phase = phase1(table);
            result.add(phase);

            if (phase.specialSolutionType() != null) {
                return result;
            }
            table = removeExtension(phase.getLastTable());
        }

        // primary simplex algorithm
        final var headline = result.isEmpty() ? SIMPLEX_HEADLINE : PHASE_2;
        result.add(phase2(table, headline));
        return result;
    }

    /**
     * Calculate the first phase of the two phase simplex method.
     *
     * @param simplexTable The initial simplex table.
     * @param <T>          Fraction or RoundedDecimal.
     * @return A Phase containing all intermediary tables.
     */
    static <T extends CalculableImpl<T>> Phase<T> phase1(@NonNull final Table<T> simplexTable) {
        final var tables = new ArrayList<Table<T>>();
        final var inst = simplexTable.inst();

        // add helper columns
        var table = buildExtension(simplexTable);

        // transform table to its canonical form
        table = transformToCanonical(table);
        tables.add(new Table<>(table, "INITIAL TABLE"));

        // transform table until acceptable for primary simplex
        for (int count = 1; !isValid(table); ++count) {
            table = transform(table);
            tables.add(new Table<>(table, "ITERATION " + count));

            if (isOptimal(table) && !table.rHS().get(0).equals(inst.create("0"))) {
                return new Phase<>(PHASE_1, tables, INFEASIBLE);
            }
        }
        return new Phase<>(PHASE_1, tables, null);
    }

    /**
     * The primary simplex.
     *
     * @param simplexTable The initial simplex table.
     * @param headline     The headline for the phase.
     * @param <T>          Fraction or RoundedDecimal.
     * @return A Phase containing all intermediary tables.
     */
    static <T extends CalculableImpl<T>> Phase<T> phase2(@NonNull final Table<T> simplexTable,
                                                         @NonNull final String headline) {
        final var tables = new ArrayList<Table<T>>();
        var table = simplexTable;

        tables.add(new Table<>(table, "INITIAL TABLE"));

        // transform the table until an optimal solution is found
        for (int count = 1; !isOptimal(table); ++count) {
            table = transform(table);
            tables.add(new Table<>(table, "ITERATION " + count));

            final var pivotElement = table.pivot();
            if (pivotElement.value().isInfinite()) {
                return new Phase<>(headline, tables, UNBOUNDED);
            }
        }
        if (isDegenerate(table)) {
            return new Phase<>(headline, tables, MULTIPLE_SOLUTIONS);
        }
        return new Phase<>(headline, tables, null);
    }

    /**
     * Transform a table during an iteration.
     *
     * @param table A table.
     * @param <T>   Fraction or RoundedDecimal.
     * @return The resulting table.
     */
    @SuppressWarnings("LambdaBodyLength")
    public static <T extends CalculableImpl<T>> @NonNull Table<T> transform(@NonNull final Table<T> table) {
        final var inst = table.inst();
        final var rowCount = table.rHS().size();
        final var columnHeaders = table.columnHeaders();
        final var pivot = table.pivot();

        var rowHeaders = table.rowHeaders();
        rowHeaders = updateRowHeaders(columnHeaders, table.rowHeaders(), pivot);

        // find divisor and divide
        final var divisor = pivot.value();
        final var lHS = new ArrayList<>(table.lHS());
        lHS.set(pivot.row(), lHS.get(pivot.row()).divideRow(divisor));

        final var rHS = new ArrayList<>(table.rHS());
        rHS.set(pivot.row(), rHS.get(pivot.row()).divide(divisor));

        //find factors
        final List<T> factors;
        factors = lHS.stream()
                .map(e -> e.getElement(pivot.column()).multiply(inst.create("-1")))
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

        final var newPivot = setPivot(
                lHS,
                rHS,
                table.helperColumns() != 0,
                inst);

        return new Table<>(
                inst,
                table.title(),
                lHS,
                rHS,
                newPivot,
                columnHeaders,
                rowHeaders,
                table.helperColumns());
    }

    /**
     * Transform a table to its canonical form.
     *
     * @param table A Simplex-Table.
     * @param <T>   Fraction or RoundedDecimal
     * @return The transformed table.
     */
    public static <T extends CalculableImpl<T>> @NonNull Table<T> transformToCanonical(@NonNull final Table<T> table) {

        if (table.helperColumns() == 0) {
            throw new IllegalArgumentException();
        }

        final var inst = table.inst();
        final var extensionSize = table.helperColumns();
        final var lHs = new ArrayList<>(table.lHS());
        final var rHS = new ArrayList<>(table.rHS());
        final var rowHeaders = table.rowHeaders();

        IntStream.range(0, rowHeaders.size())
                .forEach(i -> {
                    if (rowHeaders.get(i).contains("h")) {
                        lHs.set(0, lHs.get(0).addRow(lHs.get(i).invertRow()));
                        rHS.set(0, rHS.get(0).add(rHS.get(i).multiply(inst.create("-1"))));
                    }
                });

        final var pivot = setPivot(lHs, rHS, true, inst);
        return new Table<>(
                inst,
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
