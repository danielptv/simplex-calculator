package com.danielptv.simplex.presentation;

import static com.danielptv.simplex.presentation.InputUtils.getProblemBounds;
import static com.danielptv.simplex.presentation.InputUtils.parseInput;
import static com.danielptv.simplex.presentation.OutputUtils.ANSI_GREEN;
import static com.danielptv.simplex.presentation.OutputUtils.ANSI_RESET;
import static com.danielptv.simplex.presentation.OutputUtils.APPLICATION_HEADLINE;
import static com.danielptv.simplex.presentation.OutputUtils.printPhaseResult;
import static com.danielptv.simplex.service.TableBuildService.build;
import static com.danielptv.simplex.service.TwoPhaseSimplex.calc;
import static java.lang.System.out;

/**
 * Class containing main-method of the application.
 */
public final class Application {
    private Application() {

    }

    /**
     * Method for starting the application.
     *
     * @param args Optional parameters for starting the application.
     * @throws Exception Throws IOException if an I/O error occurs.
     */
    public static void main(final String[] args) throws Exception {
        out.println(ANSI_GREEN + APPLICATION_HEADLINE + ANSI_RESET);

        final var problemBounds = getProblemBounds();
        final var problem = parseInput(problemBounds);
        out.println(problem);
        final var table = build(problem);
        final var result = calc(table);
        result.forEach(out::println);
        out.println(printPhaseResult(result.get(result.size() - 1)));

        System.in.read();
    }
}
