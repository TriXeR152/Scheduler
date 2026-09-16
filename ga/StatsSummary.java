package ga;

import java.util.List;
import java.util.OptionalDouble;


public class StatsSummary {
    public final String strategyName;
    public final int totalRuns;
    public final double feasibleRate; // fraction of runs reaching 0 hard violations
    public final double meanFitness;
    public final double stdDevFitness;
    public final double meanHardViolations;
    public final double stdDevHardViolations;
    public final double meanSoftPenalty;
    public final Double meanGenerationsToFeasible; // null if no run converged

    private StatsSummary(String strategyName, int totalRuns, double feasibleRate,
                          double meanFitness, double stdDevFitness,
                          double meanHardViolations, double stdDevHardViolations,
                          double meanSoftPenalty, Double meanGenerationsToFeasible) {
        this.strategyName = strategyName;
        this.totalRuns = totalRuns;
        this.feasibleRate = feasibleRate;
        this.meanFitness = meanFitness;
        this.stdDevFitness = stdDevFitness;
        this.meanHardViolations = meanHardViolations;
        this.stdDevHardViolations = stdDevHardViolations;
        this.meanSoftPenalty = meanSoftPenalty;
        this.meanGenerationsToFeasible = meanGenerationsToFeasible;
    }
    
    double meanHard, stdDevHard, meanSoft;

    public static StatsSummary of(String strategyName, List<RunResult> results) {
        int n = results.size();

        double meanFitness = results.stream().mapToDouble(r -> r.bestFitness).average().orElse(0);
        double stdDevFitness = stdDev(results.stream().mapToDouble(r -> r.bestFitness).toArray(), meanFitness);

        double meanHard = results.stream().mapToInt(r -> r.bestHardViolations).average().orElse(0);
        double stdDevHard = stdDev(results.stream().mapToDouble(r -> r.bestHardViolations).toArray(), meanHard);

        double meanSoft = results.stream().mapToDouble(r -> r.bestSoftPenalty).average().orElse(0);

        long feasibleCount = results.stream().filter(r -> r.bestHardViolations == 0).count();
        double feasibleRate = (double) feasibleCount / n;

        OptionalDouble avgGen = results.stream()
                .filter(r -> r.generationsToFeasible >= 0)
                .mapToInt(r -> r.generationsToFeasible)
                .average();
        Double meanGenerations = avgGen.isPresent() ? avgGen.getAsDouble() : null;

        return new StatsSummary(strategyName, n, feasibleRate, meanFitness, stdDevFitness,
                meanHard, stdDevHard, meanSoft, meanGenerations);
    }

    private static double stdDev(double[] values, double mean) {
    	
        if (values.length == 0) return 0;
        double sumSq = 0;
        for (double v : values) sumSq += (v - mean) * (v - mean);
        return Math.sqrt(sumSq / values.length);
    }

    @Override
    public String toString() {
    	
        String genStr = meanGenerationsToFeasible == null ? "never" : String.format(java.util.Locale.US, "%.1f", meanGenerationsToFeasible);
        return String.format(java.util.Locale.US,
            "%-38s runs=%d feasibleRate=%.0f%% fitness=%.4f\u00b1%.4f hardViol=%.2f\u00b1%.2f softPenalty=%.2f avgGenToFeasible=%s",
            strategyName, totalRuns, feasibleRate * 100, meanFitness, stdDevFitness,
            meanHard, stdDevHard, meanSoft, genStr
        );
    }
}
