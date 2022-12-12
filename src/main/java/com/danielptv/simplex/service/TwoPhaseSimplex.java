package com.danielptv.simplex.service;

import com.danielptv.simplex.entity.CalculableImpl;
import com.danielptv.simplex.entity.Phase;
import com.danielptv.simplex.entity.Row;
import com.danielptv.simplex.entity.Table;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.danielptv.simplex.entity.NoSolutionType.INFEASIBLE;
import static com.danielptv.simplex.entity.NoSolutionType.UNBOUNDED;
import static com.danielptv.simplex.presentation.OutputUtils.PHASE_1;
import static com.danielptv.simplex.presentation.OutputUtils.PHASE_2;
import static com.danielptv.simplex.presentation.OutputUtils.SIMPLEX_HEADLINE;
import static com.danielptv.simplex.service.TableCalcService.getPivotElement;
import static com.danielptv.simplex.service.TableCalcService.isOptimal;
import static com.danielptv.simplex.service.TableCalcService.isSimplexAcceptable;
import static com.danielptv.simplex.service.TableCalcService.setPivot;
import static com.danielptv.simplex.service.TableCalcService.updateRowHeaders;
import static com.danielptv.simplex.service.TableExtensionService.buildExtension;
import static com.danielptv.simplex.service.TableExtensionService.removeExtension;

/**
 * Service-Class for Simplex-Algorithm calculations.
 */
public final class TwoPhaseSimplex {

    private TwoPhaseSimplex() {

    }

    /**
     * Method for calculating a linear problem.
     *
     * @param simplexTable The initial Simplex-Table.
     * @param <T>          Fraction or RoundedDecimal.
     * @return A List containing the resulting Simplex-Phases.
     */
    public static <T extends CalculableImpl<T>> List<Phase<T>> calc(@NonNull final Table<T> simplexTable) {
        final var result = new ArrayList<Phase<T>>(2);
        var table = simplexTable;

        // optional phase 1
        if (!isSimplexAcceptable(table.rHS(), false, table.inst())) {
            final var phase = phase1(table);
            result.add(phase);

            if (phase.getNoSolutionType() != null) {
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
     * Method for the first phase of the 2-Phase-Simplex method.
     *
     * @param simplexTable The initial simplex table.
     * @param <T>          Fraction or RoundedDecimal.
     * @return A Phase containing all intermediary tables.
     */
    static <T extends CalculableImpl<T>> Phase<T> phase1(@NonNull final Table<T> simplexTable) {
        final var phase = new Phase<T>(PHASE_1);
        final var inst = simplexTable.inst();

        // add helper columns
        var table = buildExtension(simplexTable);

        // transform table to its canonical form
        table = transformToCanonical(table);
        phase.addTable(new Table<>(table, "INITIAL TABLE"));

        // transform table until acceptable for primary simplex
        var count = 1;
        var isSimplexAcceptable = isSimplexAcceptable(table.rHS(), true, inst);
        while (!isSimplexAcceptable) {
            table = transform(table);
            phase.addTable(new Table<>(table, "ITERATION " + count));
            isSimplexAcceptable = isSimplexAcceptable(table.rHS(), true, inst);
            count++;

            if (isOptimal(table.lHS(), table.extendedLHS()) && !table.rHS().get(0).equals(inst.create("0"))) {
                phase.setNoSolutionType(INFEASIBLE);
                return phase;
            }
        }
        return phase;
    }

    /**
     * Method for the primary simplex.
     *
     * @param simplexTable The initial simplex table.
     * @param headline     The headline for the phase.
     * @param <T>          Fraction or RoundedDecimal.
     * @return A Phase containing all intermediary tables.
     */
    static <T extends CalculableImpl<T>> Phase<T> phase2(@NonNull final Table<T> simplexTable,
                                                         @NonNull final String headline) {
        final var phase = new Phase<T>(headline);
        final var inst = simplexTable.inst();
        var table = simplexTable;
        phase.addTable(new Table<>(table, "INITIAL TABLE"));

        // transform the table until an optimal solution is found
        var count = 1;
        var isOptimal = isOptimal(table.lHS(), table.extendedLHS());
        while (!isOptimal) {
            table = transform(table);
            phase.addTable(new Table<>(table, "ITERATION " + count));

            final var pivotElement = table.lHS().get(table.pivot().row()).getElementByIndex(table.pivot().column());
            if (pivotElement.compareTo(inst.create("0")) < 0) {
                phase.setNoSolutionType(UNBOUNDED);
                return phase;
            }

            isOptimal = isOptimal(table.lHS(), table.extendedLHS());
            count++;
        }
        return phase;
    }

    /**
     * Method for transforming a Simplex-Table during an iteration.
     *
     * @param table A Simplex-Table.
     * @param <T>   Fraction or RoundedDecimal.
     * @return The resulting Simplex-Table.
     */
    @SuppressWarnings("LambdaBodyLength")
    public static <T extends CalculableImpl<T>> @NonNull Table<T> transform(
            @NonNull final Table<T> table) {
        final var inst = table.inst();
        final var rowCount = table.rHS().size();
        final var columnHeaders = table.columnHeaders();
        final var pivot = table.pivot();

        var rowHeaders = table.rowHeaders();
        rowHeaders = updateRowHeaders(columnHeaders, table.rowHeaders(), pivot);

        // find divisor and divide
        final var divisor = getPivotElement(pivot, table.lHS());
        final var lHS = new ArrayList<>(table.lHS());
        lHS.set(pivot.row(), lHS.get(pivot.row()).divideRow(divisor));
        var extendedLHS = table.extendedLHS();

        final var rHS = new ArrayList<>(table.rHS());
        rHS.set(pivot.row(), rHS.get(pivot.row()).divide(divisor));
        if (table.extendedLHS() != null) {
            extendedLHS = new ArrayList<>(extendedLHS);
            extendedLHS.set(pivot.row(), extendedLHS.get(pivot.row()).divideRow(divisor));
        }

        //find factors
        final List<T> factors;
        factors = lHS.stream()
                .map(e -> e.getElementByIndex(pivot.column()).multiply(inst.create("-1")))
                .toList();

        //iterate
        final List<T> finalFactors = factors;
        if (table.extendedLHS() != null) {
            extendedLHS = new ArrayList<>(extendedLHS);
        }
        final var finalExtendedLHS = extendedLHS;
        IntStream.range(0, rowCount)
                .forEach(row -> {
                    if (row != pivot.row()) {
                        var pivotTemp = new Row<>(lHS.get(pivot.row()));
                        var currRow = new Row<>(lHS.get(row));

                        lHS.set(row, pivotTemp.multiplyRow(pivotTemp, finalFactors.get(row)));
                        lHS.set(row, lHS.get(row).addRow(currRow));

                        if (table.extendedLHS() != null) {
                            pivotTemp = new Row<>(finalExtendedLHS.get(pivot.row()));
                            currRow = new Row<>(finalExtendedLHS.get(row));

                            finalExtendedLHS.set(row, pivotTemp.multiplyRow(pivotTemp, finalFactors.get(row)));
                            finalExtendedLHS.set(row, finalExtendedLHS.get(row).addRow(currRow));
                        }
                        final var currVal = table.rHS().get(row);
                        rHS.set(row, rHS.get(pivot.row()).multiply(finalFactors.get(row)).add(currVal));
                    }
                });

        final var newPivot = setPivot(
                lHS,
                rHS,
                finalExtendedLHS,
                inst);

        return new Table<>(
                inst,
                table.title(),
                lHS,
                finalExtendedLHS,
                rHS,
                newPivot,
                columnHeaders,
                rowHeaders,
                table.extensionSize());
    }

    /**
     * Method for transforming a table to its canonical form.
     *
     * @param table A Simplex-Table.
     * @param <T>   Fraction or RoundedDecimal
     * @return The transformed table.
     */
    public static <T extends CalculableImpl<T>> @NonNull Table<T> transformToCanonical(
            @NonNull final Table<T> table) {

        if (table.extendedLHS() == null) {
            throw new IllegalArgumentException();
        }

        final var inst = table.inst();
        final var extensionSize = table.extensionSize();
        final var lHs = new ArrayList<>(table.lHS());
        final var extendedLHS = new ArrayList<>(table.extendedLHS());
        final var rHS = new ArrayList<>(table.rHS());
        final var rowHeaders = table.rowHeaders();

        IntStream.range(0, rowHeaders.size())
                .forEach(i -> {
                    if (rowHeaders.get(i).contains("h")) {
                        lHs.set(0, lHs.get(0).addRow(lHs.get(i).invertRow()));
                        extendedLHS.set(0, extendedLHS.get(0).addRow(extendedLHS.get(i).invertRow()));
                        rHS.set(0, rHS.get(0).add(rHS.get(i).multiply(inst.create("-1"))));
                    }
                });

        final var pivot = setPivot(lHs, rHS, extendedLHS, inst);
        return new Table<>(
                inst,
                table.title(),
                lHs,
                extendedLHS,
                rHS,
                pivot,
                table.columnHeaders(),
                rowHeaders,
                extensionSize
        );
    }
}
