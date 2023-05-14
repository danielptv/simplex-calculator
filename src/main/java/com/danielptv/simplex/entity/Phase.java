package com.danielptv.simplex.entity;

import com.danielptv.simplex.number.CalculableImpl;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

@SuppressFBWarnings("EI_EXPOSE_REP")
public record Phase<T extends CalculableImpl<T>>(
        List<SimplexTable<T>> tables,
        SpecialSolutionType specialSolutionType,
        boolean singlePhase
) {
    public SimplexTable<T> getLastTable() {
        return tables.get(tables.size() - 1);
    }
}
