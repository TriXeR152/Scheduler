package ga;

public class GenerationStats {
    public final int generation;
    public final int bestHardViolations;
    public final double bestSoftPenalty;
    public final double bestFitness;
    public final double avgFitness;

    public GenerationStats(int generation, int bestHardViolations, double bestSoftPenalty,
                            double bestFitness, double avgFitness) {
        this.generation = generation;
        this.bestHardViolations = bestHardViolations;
        this.bestSoftPenalty = bestSoftPenalty;
        this.bestFitness = bestFitness;
        this.avgFitness = avgFitness;
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US, "gen=%d bestHard=%d bestSoft=%.2f bestFitness=%.6f avgFitness=%.6f",
                generation, bestHardViolations, bestSoftPenalty, bestFitness, avgFitness);
    }
}
