package model;

public class StudentGroup {
    private final String id;
    private final String name;
    private final int size;

    public StudentGroup(String id, String name, int size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getSize() { return size; }

    @Override
    public String toString() { return name; }
}
