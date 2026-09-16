package ga;


public class ScoreReport {
    // weight applied to each hard violation so it always dominates soft penalty.
    public static final double HARD_WEIGHT = 1000.0;

    public int hardViolations = 0;
    public double softPenalty = 0.0;

    public double totalPenalty() {
        return HARD_WEIGHT * hardViolations + softPenalty;
    }

    // higher is better convenient for tournament selection / sorting.
    public double fitness() {
        return 1.0 / (1.0 + totalPenalty());
    }

    public boolean isFeasible() {
        return hardViolations == 0;
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US, "hardViolations=%d, softPenalty=%.2f, fitness=%.6f",
                hardViolations, softPenalty, fitness());
    }
}
