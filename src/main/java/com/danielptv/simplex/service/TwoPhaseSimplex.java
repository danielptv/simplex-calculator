package com.danielptv.simplex.service;

import com.danielptv.simplex.entity.CalculableImpl;
import com.danielptv.simplex.entity.Phase;
import com.danielptv.simplex.entity.Row;
import com.danielptv.simplex.entity.Table;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

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
    @SuppressWarnings("ExecutableStatementCount")
    public static <T extends CalculableImpl<T>> List<Phase> calc(@NonNull final Table<T> simplexTable) {
        Phase phase = null;
        final var inst = simplexTable.getInst();
        final var result = new ArrayList<Phase>();
        var count = 1;

        var table = simplexTable;
        if (!isSimplexAcceptable(table.getRHS(), false, inst)) {
            phase = new Phase(PHASE_1);
            table = buildExtension(table);

            table = transformToCanonical(table);
            phase.addTable(new Table<>(table, "INITIAL TABLE"));

            var isSimplexAcceptable = isSimplexAcceptable(table.getRHS(), true, inst);
            while (!isSimplexAcceptable) {
                table = transform(table);
                phase.addTable(new Table<>(table, "ITERATION " + count));
                count++;
                isSimplexAcceptable = isSimplexAcceptable(table.getRHS(), true, inst);

                if (isOptimal(table.getLHS(), table.getExtendedLHS()) &&
                        !table.getRHS().get(0).equals(inst.create("0"))) {
                    phase.setSolvable(false);
                    result.add(phase);
                    return result;
                }
            }
            table = removeExtension(table);
        }

        final Phase phase2;
        if (phase != null) {
            result.add(phase);
            phase2 = new Phase(PHASE_2);
        } else {
            phase2 = new Phase(SIMPLEX_HEADLINE);
        }
        phase2.addTable(new Table<>(table, "INITIAL TABLE"));
        count = 1;

        var isOptimal = isOptimal(table.getLHS(), table.getExtendedLHS());
        while (!isOptimal) {
            table = transform(table);
            phase2.addTable(new Table<>(table, "ITERATION " + count));
            isOptimal = isOptimal(table.getLHS(), table.getExtendedLHS());
            count++;
        }
        result.add(phase2);
        return result;
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
        final var inst = table.getInst();
        final var rowCount = table.getRHS().size();
        final var columnHeaders = table.getColumnHeaders();
        final var pivot = table.getPivot();

        if (pivot == null) {
            throw new IllegalArgumentException();
        }
        var rowHeaders = table.getRowHeaders();
        rowHeaders = updateRowHeaders(columnHeaders, table.getRowHeaders(), pivot);

        // find divisor and divide
        final var divisor = getPivotElement(pivot, table.getLHS());
        final var lHS = new ArrayList<>(table.getLHS());
        lHS.set(pivot.row(), lHS.get(pivot.row()).divideRow(divisor));
        var extendedLHS = table.getExtendedLHS();

        final var rHS = new ArrayList<>(table.getRHS());
        rHS.set(pivot.row(), rHS.get(pivot.row()).divide(divisor));
        if (table.getExtendedLHS() != null) {
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
        if (table.getExtendedLHS() != null) {
            extendedLHS = new ArrayList<>(extendedLHS);
        }
        final var finalExtendedLHS = extendedLHS;
        IntStream.range(0, rowCount)
                .forEach(row -> {
                    if (row != pivot.row()) {
                        var pivotTemp = new Row<>(lHS.get(pivot.row()));
                        var currRow = new Row<>(lHS.get(row));

                        lHS.set(row, pivotTemp.multiplyRow(pivotTemp, finalFactors.get(row)));
                        lHS.set(row, lHS.get(row).addUpRow(currRow));

                        if (table.getExtendedLHS() != null) {
                            pivotTemp = new Row<>(finalExtendedLHS.get(pivot.row()));
                            currRow = new Row<>(finalExtendedLHS.get(row));

                            finalExtendedLHS.set(row, pivotTemp.multiplyRow(pivotTemp, finalFactors.get(row)));
                            finalExtendedLHS.set(row, finalExtendedLHS.get(row).addUpRow(currRow));
                        }
                        final var currVal = table.getRHS().get(row);
                        rHS.set(row, rHS.get(pivot.row()).multiply(finalFactors.get(row)).addUp(currVal));
                    }
                });

        final var newPivot = setPivot(
                lHS,
                rHS,
                finalExtendedLHS,
                inst);

        return new Table<>(
                inst,
                table.getTitle(),
                lHS,
                finalExtendedLHS,
                rHS,
                newPivot,
                columnHeaders,
                rowHeaders,
                table.getExtensionSize());
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

        if (table.getExtendedLHS() == null) {
            throw new IllegalArgumentException();
        }

        final var inst = table.getInst();
        final var extensionSize = table.getExtensionSize();
        final var lHs = new ArrayList<>(table.getLHS());
        final var extendedLHS = new ArrayList<>(table.getExtendedLHS());
        final var rHS = new ArrayList<>(table.getRHS());

        for (int i = 0; i < extensionSize; ++i) {

            final var finalI = i;
            final var optionalInt = IntStream.range(2, rHS.size())
                    .filter(e -> Objects.equals(extendedLHS.get(e).getEntries().get(finalI), inst.create("1")))
                    .findFirst();
            if (optionalInt.isEmpty()) {
                throw new UnsupportedOperationException();
            }
            final var row = optionalInt.getAsInt();

            final var newRowLeft = lHs.get(row).invertRow().addUpRow(lHs.get(0));
            lHs.set(0, newRowLeft);
            final var newRowExt = extendedLHS.get(row).invertRow().addUpRow(extendedLHS.get(0));
            extendedLHS.set(0, newRowExt);
            final var newValRHS = rHS.get(row).multiply(inst.create("-1")).addUp(rHS.get(0));
            rHS.set(0, newValRHS);
        }

        final var pivot = setPivot(lHs, rHS, extendedLHS, inst);

        return new Table<>(
                inst,
                table.getTitle(),
                lHs,
                extendedLHS,
                rHS,
                pivot,
                table.getColumnHeaders(),
                table.getRowHeaders(),
                extensionSize);
    }
}
