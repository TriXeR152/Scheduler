package demo;

import ga.*;
import model.StudentGroup;
import output.TimetableGridFormatter;
import output.TimetableHtmlExporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException {
    	
        ProblemInstance problem = SampleDataFactory.build();
        ConstraintChecker checker = new ConstraintChecker();

        
        // linkedHashMap keeps insertion order so the printed report reads top-to-bottom
        // from weakest to strongest as intended
        
        Map<String, Repairer> strategies = new LinkedHashMap<>();
        strategies.put("Baseline GA (no repair)", Repairer.NONE);
        
        strategies.put("Hybrid GA (greedy first-fit repair)",
                new GreedyConflictRepairer(new Random(new GAConfig().randomSeed)));
        
        strategies.put("Hybrid GA (min-conflicts repair)",
                new MinConflictsRepairer(30, new Random(new GAConfig().randomSeed)));
        
        strategies.put("Hybrid GA (simulated annealing repair)",
                new SimulatedAnnealingRepairer(30, /*initialTemp*/ 5.0, /*coolingRate*/ 0.95,
                        new Random(new GAConfig().randomSeed)));

        Individual overallBest = null;
        String overallBestLabel = null;

        for (Map.Entry<String, Repairer> entry : strategies.entrySet()) {
        	
            System.out.println("=== " + entry.getKey() + " ===");
            GAConfig config = new GAConfig();
            GeneticAlgorithm ga = new GeneticAlgorithm(problem, config, checker, entry.getValue());
            Individual best = ga.run();
            printSummary(ga.history, best);
            System.out.println();

            if (overallBest == null || best.getScore().fitness() > overallBest.getScore().fitness()) {
                overallBest = best;
                overallBestLabel = entry.getKey();
            }
        }

        System.out.println("=== Best overall: " + overallBestLabel + " ===");
        System.out.println();

        // weekly grid per student group, printed to console
        for (StudentGroup group : problem.getGroups()) {
            System.out.println(TimetableGridFormatter.forGroup(overallBest.getTimetable(), group));
        }

        // styled HTML page one grid per group and per teacher 
        Path resultsDir = Path.of("results");
        Files.createDirectories(resultsDir);
        Path htmlPath = resultsDir.resolve("timetable.html");
        TimetableHtmlExporter.export(htmlPath, overallBest.getTimetable(), problem);
        System.out.println("Weekly timetable HTML written to " + htmlPath.toAbsolutePath());
    }

    private static void printSummary(List<GenerationStats> history, Individual best) {
    	
        // print every 50th generation plus the final one for better reading
        for (GenerationStats s : history) {
            if (s.generation % 50 == 0) {
                System.out.println(s);
            }
        }
        
        GenerationStats last = history.get(history.size() - 1);
        System.out.println("Final: " + last);
        System.out.println("Feasible (0 hard violations)? " + best.getScore().isFeasible());
    }
}
