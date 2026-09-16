package model;

public class Room {
    private final String id;
    private final String name;
    private final Building building;
    private final int capacity;
    private final RoomType type;

    public Room(String id, String name, Building building, int capacity, RoomType type) {
        this.id = id;
        this.name = name;
        this.building = building;
        this.capacity = capacity;
        this.type = type;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Building getBuilding() { return building; }
    public int getCapacity() { return capacity; }
    public RoomType getType() { return type; }

    public boolean canHost(int groupSize, RoomType requiredType) {
        return capacity >= groupSize && type == requiredType;
    }

    @Override
    public String toString() { return name + " (" + building.getName() + ")"; }
}
