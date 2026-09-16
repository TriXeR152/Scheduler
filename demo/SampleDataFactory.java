package demo;

import ga.ProblemInstance;
import model.*;

import java.util.ArrayList;
import java.util.List;


public class SampleDataFactory {

    public static ProblemInstance build() {
    	
        Building mainBuilding = new Building("B1", "Main Building");
        Building annex = new Building("B2", "Annex");

        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room("R1", "101", mainBuilding, 40, RoomType.LECTURE_HALL));
        rooms.add(new Room("R2", "Lab A", mainBuilding, 20, RoomType.LAB));
        rooms.add(new Room("R3", "201", annex, 40, RoomType.SEMINAR_ROOM));

        Teacher t1 = new Teacher("T1", "Dr. Ivanov", 3);
        Teacher t2 = new Teacher("T2", "Dr. Petrova", 3);
        Teacher t3 = new Teacher("T3", "Dr. Georgiev", 2);
        Teacher t4 = new Teacher("T4", "Dr. Dimitrova", 3);
        List<Teacher> teachers = List.of(t1, t2, t3, t4);

        
        t1.markUnavailable(new TimeSlot(4, 6)); 
        t2.markPreferred(new TimeSlot(0, 1));   

        StudentGroup g1 = new StudentGroup("G1", "CS-2A", 35);
        StudentGroup g2 = new StudentGroup("G2", "CS-2B", 18);
        List<StudentGroup> groups = List.of(g1, g2);

        List<Course> courses = new ArrayList<>();
        courses.add(new Course("C1", "Algorithms", t1, g1, RoomType.LECTURE_HALL, 2));
        courses.add(new Course("C2", "Databases Lab", t2, g2, RoomType.LAB, 2));
        courses.add(new Course("C3", "Software Eng.", t3, g1, RoomType.SEMINAR_ROOM, 1));
        courses.add(new Course("C4", "Networks", t4, g2, RoomType.LECTURE_HALL, 2));
        courses.add(new Course("C5", "Operating Systems", t1, g2, RoomType.LECTURE_HALL, 1));
        courses.add(new Course("C6", "AI Fundamentals", t2, g1, RoomType.SEMINAR_ROOM, 2));

        
        List<Session> sessions = new ArrayList<>();
        for (Course course : courses) {
            for (int i = 0; i < course.getSessionsPerWeek(); i++) {
                sessions.add(new Session(course.getId() + "-S" + i, course, i));
            }
        }

        return new ProblemInstance(rooms, teachers, groups, sessions);
    }
}
