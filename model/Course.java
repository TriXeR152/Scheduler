package model;

public class Course {
    private final String id;
    private final String subjectName;
    private final Teacher teacher;
    private final StudentGroup group;
    private final RoomType requiredRoomType;
    private final int sessionsPerWeek;

    public Course(String id, String subjectName, Teacher teacher, StudentGroup group,
                  RoomType requiredRoomType, int sessionsPerWeek) {
        this.id = id;
        this.subjectName = subjectName;
        this.teacher = teacher;
        this.group = group;
        this.requiredRoomType = requiredRoomType;
        this.sessionsPerWeek = sessionsPerWeek;
    }

    public String getId() { return id; }
    public String getSubjectName() { return subjectName; }
    public Teacher getTeacher() { return teacher; }
    public StudentGroup getGroup() { return group; }
    public RoomType getRequiredRoomType() { return requiredRoomType; }
    public int getSessionsPerWeek() { return sessionsPerWeek; }

    @Override
    public String toString() { return subjectName + " [" + group.getName() + "/" + teacher.getName() + "]"; }
}
