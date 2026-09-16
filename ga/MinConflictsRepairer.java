package ga;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MinConflictsRepairer implements Repairer {

    private final int maxIterations;
    private final Random rng;

    public MinConflictsRepairer(int maxIterations, Random rng) {
        this.maxIterations = maxIterations;
        this.rng = rng;
    }

    @Override
    public void repair(Timetable timetable, ProblemInstance problem) {
    	
        OccupancyTracker tracker = OccupancyTracker.buildFrom(timetable);
        List<Assignment> genes = timetable.getAssignments();

        for (int iter = 0; iter < maxIterations; iter++) {
            Assignment gene = pickConflictingGene(genes, tracker);
            if (gene == null) break; 

            Course course = gene.getSession().getCourse();
            Teacher teacher = course.getTeacher();

            tracker.remove(gene); // exclude its own current placement before searching

            List<Placement> bestTies = new ArrayList<>();
            int bestConflicts = Integer.MAX_VALUE;

            for (Room room : problem.getRooms()) {
            	
                for (int slotIndex = 0; slotIndex < TimeSlot.totalSlots(); slotIndex++) {
                	
                    TimeSlot slot = TimeSlot.fromIndex(slotIndex);
                    int conflicts = tracker.conflictsIfPlaced(room, slot, course, teacher);
                    
                    if (conflicts < bestConflicts) {
                        bestConflicts = conflicts;
                        bestTies.clear();
                        bestTies.add(new Placement(room, slot));
                    } else if (conflicts == bestConflicts) {
                        bestTies.add(new Placement(room, slot));
                    }
                }
            }

            Placement chosen = bestTies.get(rng.nextInt(bestTies.size()));
            gene.setRoom(chosen.room);
            gene.setTimeSlot(chosen.slot);
            tracker.add(gene);
        }
    }

    private Assignment pickConflictingGene(List<Assignment> genes, OccupancyTracker tracker) {
    	
        List<Assignment> conflicting = new ArrayList<>();
        
        for (Assignment a : genes) {
            Course course = a.getSession().getCourse();
            tracker.remove(a);
            int conflicts = tracker.conflictsIfPlaced(a.getRoom(), a.getTimeSlot(), course, course.getTeacher());
            tracker.add(a);
            if (conflicts > 0) conflicting.add(a);
        }
        if (conflicting.isEmpty()) return null;
        return conflicting.get(rng.nextInt(conflicting.size()));
    }

    private static class Placement {
        final Room room;
        final TimeSlot slot;
        Placement(Room room, TimeSlot slot) { this.room = room; this.slot = slot; }
    }
}
