package ga;


public class RunResult {
    public final String strategyName;
    public final long seed;
    public final int bestHardViolations;
    public final double bestSoftPenalty;
    public final double bestFitness;
    public final int generationsToFeasible; // -1 if never reached within maxGenerations

    public RunResult(String strategyName, long seed, int bestHardViolations, double bestSoftPenalty,
                      double bestFitness, int generationsToFeasible) {
        this.strategyName = strategyName;
        this.seed = seed;
        this.bestHardViolations = bestHardViolations;
        this.bestSoftPenalty = bestSoftPenalty;
        this.bestFitness = bestFitness;
        this.generationsToFeasible = generationsToFeasible;
    }
}
