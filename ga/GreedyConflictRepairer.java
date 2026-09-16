package ga;

import model.*;

import java.util.*;


public class GreedyConflictRepairer implements Repairer {

    private final Random rng;

    public GreedyConflictRepairer(Random rng) {
        this.rng = rng;
    }

    @Override
    public void repair(Timetable timetable, ProblemInstance problem) {
    	
        Set<String> roomSlotTaken = new HashSet<>();
        Set<String> teacherSlotTaken = new HashSet<>();
        Set<String> groupSlotTaken = new HashSet<>();

        List<Room> shuffledRooms = new ArrayList<>(problem.getRooms());
        int totalSlots = TimeSlot.totalSlots();

        for (Assignment gene : timetable.getAssignments()) {
        	
            Course course = gene.getSession().getCourse();
            Teacher teacher = course.getTeacher();
            StudentGroup group = course.getGroup();

            boolean valid = isValidPlacement(gene.getRoom(), gene.getTimeSlot(), course, teacher,
                    roomSlotTaken, teacherSlotTaken, groupSlotTaken);

            if (!valid) {
                Placement fix = findFreePlacement(problem, shuffledRooms, totalSlots, course, teacher,
                        roomSlotTaken, teacherSlotTaken, groupSlotTaken);
                if (fix != null) {
                    gene.setRoom(fix.room);
                    gene.setTimeSlot(fix.slot);
                }
                // if no fix found leave the gene, ConstraintChecker will
                // still count it as a violation and selection pressure handles
                // it over subsequent generations
            }

            // confirm this gene's (possibly updated) placement as occupied
            markOccupied(gene, teacher, group, roomSlotTaken, teacherSlotTaken, groupSlotTaken);
        }
    }

    private boolean isValidPlacement(Room room, TimeSlot slot, Course course, Teacher teacher,
                                      Set<String> roomSlotTaken, Set<String> teacherSlotTaken,
                                      Set<String> groupSlotTaken) {
    	
        if (!room.canHost(course.getGroup().getSize(), course.getRequiredRoomType())) return false;
        if (!teacher.isAvailable(slot)) return false;
        if (roomSlotTaken.contains(room.getId() + "@" + slot.toIndex())) return false;
        if (teacherSlotTaken.contains(teacher.getId() + "@" + slot.toIndex())) return false;
        if (groupSlotTaken.contains(course.getGroup().getId() + "@" + slot.toIndex())) return false;
        return true;
    }

    private Placement findFreePlacement(ProblemInstance problem, List<Room> rooms, int totalSlots,
                                         Course course, Teacher teacher,
                                         Set<String> roomSlotTaken, Set<String> teacherSlotTaken,
                                         Set<String> groupSlotTaken) {
    	
        Collections.shuffle(rooms, rng);
        List<Integer> slotOrder = new ArrayList<>(totalSlots);
        for (int i = 0; i < totalSlots; i++) slotOrder.add(i);
        Collections.shuffle(slotOrder, rng);

        for (Room room : rooms) {
            if (!room.canHost(course.getGroup().getSize(), course.getRequiredRoomType())) continue;

            for (int slotIndex : slotOrder) {
                TimeSlot slot = TimeSlot.fromIndex(slotIndex);
                if (isValidPlacement(room, slot, course, teacher, roomSlotTaken, teacherSlotTaken, groupSlotTaken)) {
                    return new Placement(room, slot);
                }
            }
        }
        return null; // no free valid slot found schedule is over-constrained at this point
    }

    private void markOccupied(Assignment gene, Teacher teacher, StudentGroup group,
                               Set<String> roomSlotTaken, Set<String> teacherSlotTaken,
                               Set<String> groupSlotTaken) {
        int slotIndex = gene.getTimeSlot().toIndex();
        roomSlotTaken.add(gene.getRoom().getId() + "@" + slotIndex);
        teacherSlotTaken.add(teacher.getId() + "@" + slotIndex);
        groupSlotTaken.add(group.getId() + "@" + slotIndex);
    }

    private static class Placement {
        final Room room;
        final TimeSlot slot;
        Placement(Room room, TimeSlot slot) { this.room = room; this.slot = slot; }
    }
}
