package ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ExperimentRunner {

    public static List<RunResult> run(String strategyName, RepairerFactory repairerFactory,
                                       ProblemInstance problem, ConstraintChecker checker,
                                       List<Long> seeds, int populationSize, int maxGenerations,
                                       double mutationRate, int tournamentSize, int elitismCount) {
        List<RunResult> results = new ArrayList<>(seeds.size());

        for (long seed : seeds) {
            GAConfig config = new GAConfig();
            config.populationSize = populationSize;
            config.maxGenerations = maxGenerations;
            config.mutationRate = mutationRate;
            config.tournamentSize = tournamentSize;
            config.elitismCount = elitismCount;
            config.randomSeed = seed;
           

            Repairer repairer = repairerFactory.create(new Random(seed));
            GeneticAlgorithm ga = new GeneticAlgorithm(problem, config, checker, repairer);
            Individual best = ga.run();

            int generationsToFeasible = -1;
            for (GenerationStats stats : ga.history) {
                if (stats.bestHardViolations == 0) {
                    generationsToFeasible = stats.generation;
                    break;
                }
            }

            results.add(new RunResult(
                    strategyName,
                    seed,
                    best.getScore().hardViolations,
                    best.getScore().softPenalty,
                    best.getScore().fitness(),
                    generationsToFeasible
            ));
        }
        return results;
    }
}
