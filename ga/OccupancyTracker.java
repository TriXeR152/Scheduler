package ga;

import model.*;

import java.util.HashMap;
import java.util.Map;


public class OccupancyTracker {
    private final Map<String, Integer> roomSlot = new HashMap<>();
    private final Map<String, Integer> teacherSlot = new HashMap<>();
    private final Map<String, Integer> groupSlot = new HashMap<>();

    public static OccupancyTracker buildFrom(Timetable timetable) {
        OccupancyTracker tracker = new OccupancyTracker();
        for (Assignment a : timetable.getAssignments()) {
            tracker.add(a);
        }
        return tracker;
    }

    public void add(Assignment a) {
        Course course = a.getSession().getCourse();
        bump(roomSlot, roomKey(a.getRoom(), a.getTimeSlot()), 1);
        bump(teacherSlot, teacherKey(course.getTeacher(), a.getTimeSlot()), 1);
        bump(groupSlot, groupKey(course.getGroup(), a.getTimeSlot()), 1);
    }

    public void remove(Assignment a) {
        Course course = a.getSession().getCourse();
        bump(roomSlot, roomKey(a.getRoom(), a.getTimeSlot()), -1);
        bump(teacherSlot, teacherKey(course.getTeacher(), a.getTimeSlot()), -1);
        bump(groupSlot, groupKey(course.getGroup(), a.getTimeSlot()), -1);
    }

    
    public int conflictsIfPlaced(Room room, TimeSlot slot, Course course, Teacher teacher) {
        int conflicts = 0;
        if (!room.canHost(course.getGroup().getSize(), course.getRequiredRoomType())) conflicts++;
        if (!teacher.isAvailable(slot)) conflicts++;
        conflicts += roomSlot.getOrDefault(roomKey(room, slot), 0);
        conflicts += teacherSlot.getOrDefault(teacherKey(teacher, slot), 0);
        conflicts += groupSlot.getOrDefault(groupKey(course.getGroup(), slot), 0);
        return conflicts;
    }

    private void bump(Map<String, Integer> map, String key, int delta) {
        int updated = map.getOrDefault(key, 0) + delta;
        if (updated <= 0) map.remove(key); else map.put(key, updated);
    }

    private String roomKey(Room room, TimeSlot slot) { return room.getId() + "@" + slot.toIndex(); }
    private String teacherKey(Teacher t, TimeSlot slot) { return t.getId() + "@" + slot.toIndex(); }
    private String groupKey(StudentGroup g, TimeSlot slot) { return g.getId() + "@" + slot.toIndex(); }
}
