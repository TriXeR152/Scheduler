package ga;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Timetable {
    private final List<Assignment> assignments;

    public Timetable(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public List<Assignment> getAssignments() { return assignments; }

    
    public static Timetable randomIndividual(ProblemInstance problem, Random rng) {
        List<Room> rooms = problem.getRooms();
        int totalSlots = TimeSlot.totalSlots();
        List<Assignment> genes = new ArrayList<>(problem.getSessions().size());

        for (Session session : problem.getSessions()) {
            Room room = rooms.get(rng.nextInt(rooms.size()));
            TimeSlot slot = TimeSlot.fromIndex(rng.nextInt(totalSlots));
            genes.add(new Assignment(session, room, slot));
        }
        return new Timetable(genes);
    }

    public Timetable copy() {
        List<Assignment> copied = new ArrayList<>(assignments.size());
        for (Assignment a : assignments) {
            copied.add(a.copy());
        }
        return new Timetable(copied);
    }

    public Assignment getAssignmentFor(Session session) {
        for (Assignment a : assignments) {
            if (a.getSession().equals(session)) return a;
        }
        throw new IllegalArgumentException("No assignment for session " + session);
    }
}
