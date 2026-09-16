package model;


public class Session {
    private final String id;
    private final Course course;
    private final int occurrenceIndex; // 0-based, which meeting of the week this is

    public Session(String id, Course course, int occurrenceIndex) {
        this.id = id;
        this.course = course;
        this.occurrenceIndex = occurrenceIndex;
    }

    public String getId() { return id; }
    public Course getCourse() { return course; }
    public int getOccurrenceIndex() { return occurrenceIndex; }

    @Override
    public String toString() {
        return course.getSubjectName() + " #" + (occurrenceIndex + 1);
    }
}
