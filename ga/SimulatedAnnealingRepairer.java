package ga;

import model.*;

import java.util.List;
import java.util.Random;


public class SimulatedAnnealingRepairer implements Repairer {

    private final int maxIterations;
    private final double initialTemperature;
    private final double coolingRate; // multiplicative decay applied each iteration, e.g. 0.995
    private final Random rng;

    public SimulatedAnnealingRepairer(int maxIterations, double initialTemperature,
                                       double coolingRate, Random rng) {
        this.maxIterations = maxIterations;
        this.initialTemperature = initialTemperature;
        this.coolingRate = coolingRate;
        this.rng = rng;
    }

    @Override
    public void repair(Timetable timetable, ProblemInstance problem) {
    	
        OccupancyTracker tracker = OccupancyTracker.buildFrom(timetable);
        List<Assignment> genes = timetable.getAssignments();
        List<Room> rooms = problem.getRooms();
        int totalSlots = TimeSlot.totalSlots();

        double temperature = initialTemperature;

        for (int iter = 0; iter < maxIterations; iter++) {
            Assignment gene = genes.get(rng.nextInt(genes.size()));
            Course course = gene.getSession().getCourse();
            Teacher teacher = course.getTeacher();

            tracker.remove(gene);
            int currentConflicts = tracker.conflictsIfPlaced(gene.getRoom(), gene.getTimeSlot(), course, teacher);

            Room candidateRoom = rooms.get(rng.nextInt(rooms.size()));
            TimeSlot candidateSlot = TimeSlot.fromIndex(rng.nextInt(totalSlots));
            int candidateConflicts = tracker.conflictsIfPlaced(candidateRoom, candidateSlot, course, teacher);

            int delta = candidateConflicts - currentConflicts;
            boolean accept = delta <= 0 || rng.nextDouble() < Math.exp(-delta / temperature);

            if (accept) {
                gene.setRoom(candidateRoom);
                gene.setTimeSlot(candidateSlot);
            }
            tracker.add(gene); // re-confirm at whichever placement we ended up with

            temperature = Math.max(temperature * coolingRate, 1e-6); // floor avoids div-by-zero
        }
    }
}
