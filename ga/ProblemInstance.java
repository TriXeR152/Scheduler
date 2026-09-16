package ga;

import model.Room;
import model.Session;
import model.Teacher;
import model.StudentGroup;

import java.util.List;


public class ProblemInstance {
    private final List<Room> rooms;
    private final List<Teacher> teachers;
    private final List<StudentGroup> groups;
    private final List<Session> sessions;

    public ProblemInstance(List<Room> rooms, List<Teacher> teachers,
                            List<StudentGroup> groups, List<Session> sessions) {
        this.rooms = rooms;
        this.teachers = teachers;
        this.groups = groups;
        this.sessions = sessions;
    }

    public List<Room> getRooms() { return rooms; }
    public List<Teacher> getTeachers() { return teachers; }
    public List<StudentGroup> getGroups() { return groups; }
    public List<Session> getSessions() { return sessions; }
}
