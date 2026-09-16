package demo;

import ga.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ExperimentMain {
    public static void main(String[] args) throws IOException {
        ProblemInstance problem = SampleDataFactory.build();
        ConstraintChecker checker = new ConstraintChecker();

        List<Long> seeds = new ArrayList<>();
        for (long s = 1; s <= 20; s++) seeds.add(s); // 20 seeds per strategy

        Map<String, RepairerFactory> strategies = new LinkedHashMap<>();
        strategies.put("Baseline GA (no repair)", rng -> Repairer.NONE);
        strategies.put("Hybrid GA (greedy first-fit repair)", GreedyConflictRepairer::new);
        strategies.put("Hybrid GA (min-conflicts repair)", rng -> new MinConflictsRepairer(30, rng));
        strategies.put("Hybrid GA (simulated annealing repair)",
                rng -> new SimulatedAnnealingRepairer(30, 5.0, 0.95, rng));

        List<RunResult> allResults = new ArrayList<>();
        List<StatsSummary> summaries = new ArrayList<>();

        for (Map.Entry<String, RepairerFactory> entry : strategies.entrySet()) {
            List<RunResult> results = ExperimentRunner.run(
                    entry.getKey(), entry.getValue(), problem, checker, seeds,
                    /*populationSize*/ 80, /*maxGenerations*/ 300,
                    /*mutationRate*/ 0.02, /*tournamentSize*/ 5, /*elitismCount*/ 4);
            allResults.addAll(results);
            summaries.add(StatsSummary.of(entry.getKey(), results));
        }

        System.out.println("=== Summary across " + seeds.size() + " seeds per strategy ===");
        for (StatsSummary s : summaries) {
            System.out.println(s);
        }

        String outputPath = "experiment_results.csv";
        CsvExporter.writeRunResults(outputPath, allResults);
        System.out.println();
        System.out.println("Raw per-run results written to " + outputPath);
    }
}
