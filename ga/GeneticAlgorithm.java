package ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {

    private final ProblemInstance problem;
    private final GAConfig config;
    private final ConstraintChecker checker;
    private final Repairer repairer;
    private final Random rng;
    private final GeneticOperators operators;

    public final List<GenerationStats> history = new ArrayList<>();

    
    public GeneticAlgorithm(ProblemInstance problem, GAConfig config,
                             ConstraintChecker checker, Repairer repairer) {
        this.problem = problem;
        this.config = config;
        this.checker = checker;
        this.repairer = repairer;
        this.rng = new Random(config.randomSeed);
        this.operators = new GeneticOperators(problem, rng);
    }

    public Individual run() {
        List<Individual> population = initPopulation();

        Individual best = null;
        for (int gen = 0; gen < config.maxGenerations; gen++) {
            population.sort((a, b) -> Double.compare(b.getScore().fitness(), a.getScore().fitness()));
            best = population.get(0);

            recordStats(gen, population);

            if (config.stopOnFeasible && best.getScore().isFeasible()) {
                break;
            }

            population = nextGeneration(population);
        }
        return best;
    }

    private List<Individual> initPopulation() {
    	
        List<Individual> population = new ArrayList<>(config.populationSize);
        for (int i = 0; i < config.populationSize; i++) {
        	
            Timetable tt = Timetable.randomIndividual(problem, rng);
            repairer.repair(tt, problem); // no-op for baseline GA
            population.add(new Individual(tt, checker.evaluate(tt)));
        }
        return population;
    }

    private List<Individual> nextGeneration(List<Individual> current) {
        List<Individual> next = new ArrayList<>(config.populationSize);

        // elitism carry the best individuals forward unchanged.
        for (int i = 0; i < config.elitismCount && i < current.size(); i++) {
            next.add(current.get(i));
        }

        // fill the rest via selection + crossover + mutation (+ repair).
        while (next.size() < config.populationSize) {
            Individual parentA = operators.tournamentSelect(current, config.tournamentSize);
            Individual parentB = operators.tournamentSelect(current, config.tournamentSize);

            Timetable child = operators.crossover(parentA.getTimetable(), parentB.getTimetable());
            operators.mutate(child, config.mutationRate);
            repairer.repair(child, problem); // hybridization point

            next.add(new Individual(child, checker.evaluate(child)));
        }
        return next;
    }

    private void recordStats(int gen, List<Individual> sortedPopulation) {
    	
        Individual best = sortedPopulation.get(0);
        double avgFitness = sortedPopulation.stream()
                .mapToDouble(ind -> ind.getScore().fitness())
                .average().orElse(0.0);

        history.add(new GenerationStats(
                gen,
                best.getScore().hardViolations,
                best.getScore().softPenalty,
                best.getScore().fitness(),
                avgFitness
        ));
    }
}
