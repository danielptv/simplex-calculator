package com.danielptv.simplex.shell;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@AllArgsConstructor
@RequiredArgsConstructor
public final class InputResult implements Serializable {
    @Getter
    @Setter
    private List<String> values = new ArrayList<>();
    @Getter
    @Setter
    private String representation = "";
    @Getter
    @Setter
    private String rawInput = "";

    @Override
    public String toString() {
        return representation;
    }
}
