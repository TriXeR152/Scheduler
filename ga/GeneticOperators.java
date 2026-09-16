package ga;

import model.Assignment;
import model.Room;
import model.Session;
import model.TimeSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GeneticOperators {

    private final ProblemInstance problem;
    private final Random rng;

    public GeneticOperators(ProblemInstance problem, Random rng) {
        this.problem = problem;
        this.rng = rng;
    }

    
    public Individual tournamentSelect(List<Individual> population, int tournamentSize) {
    	
        Individual best = null;
        
        for (int i = 0; i < tournamentSize; i++) {
            Individual candidate = population.get(rng.nextInt(population.size()));
            if (best == null || candidate.getScore().fitness() > best.getScore().fitness()) {
                best = candidate;
            }
        }
        return best;
    }

    
    public Timetable crossover(Timetable parentA, Timetable parentB) {
    	
        List<Assignment> genesA = parentA.getAssignments();
        List<Assignment> genesB = parentB.getAssignments();
        int size = genesA.size();
        int cutPoint = rng.nextInt(size);

        List<Assignment> childGenes = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Assignment source = (i < cutPoint) ? genesA.get(i) : genesB.get(i);
            childGenes.add(source.copy());
        }
        return new Timetable(childGenes);
    }

    
    public void mutate(Timetable individual, double mutationRate) {
    	
        List<Room> rooms = problem.getRooms();
        int totalSlots = TimeSlot.totalSlots();

        for (Assignment gene : individual.getAssignments()) {
        	
            if (rng.nextDouble() < mutationRate) {
            	
                boolean mutateRoom = rng.nextBoolean();
                
                if (mutateRoom) {
                    gene.setRoom(rooms.get(rng.nextInt(rooms.size())));
                } else {
                    gene.setTimeSlot(TimeSlot.fromIndex(rng.nextInt(totalSlots)));
                }
            }
        }
    }
}
