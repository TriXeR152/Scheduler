package ga;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class CsvExporter {
    public static void writeRunResults(String path, List<RunResult> results) throws IOException {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write("strategy,seed,bestHardViolations,bestSoftPenalty,bestFitness,generationsToFeasible\n");
            for (RunResult r : results) {
                writer.write(String.format(java.util.Locale.US, "%s,%d,%d,%.4f,%.6f,%d%n",
                        r.strategyName.replace(",", " "), r.seed, r.bestHardViolations,
                        r.bestSoftPenalty, r.bestFitness, r.generationsToFeasible));
            }
        }
    }
}
