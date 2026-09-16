package ga;

public class GAConfig {
    public int populationSize = 80;
    public int maxGenerations = 500;
    public double mutationRate = 0.02;   // per-gene probability of mutation
    public int tournamentSize = 5;
    public int elitismCount = 4;         // top-N individuals copied unchanged each generation
    public boolean stopOnFeasible = false; // if true, stop as soon as hardViolations == 0
    public long randomSeed = 42L;        // fixed seed -> reproducible runs for the evaluation chapter
}
