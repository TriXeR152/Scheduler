package model;

import java.util.Objects;


public class Assignment {
    private final Session session;
    private Room room;
    private TimeSlot timeSlot;

    public Assignment(Session session, Room room, TimeSlot timeSlot) {
        this.session = session;
        this.room = room;
        this.timeSlot = timeSlot;
    }

    public Session getSession() { return session; }
    public Room getRoom() { return room; }
    public TimeSlot getTimeSlot() { return timeSlot; }

    public void setRoom(Room room) { this.room = room; }
    public void setTimeSlot(TimeSlot timeSlot) { this.timeSlot = timeSlot; }

    public Assignment copy() {
        return new Assignment(session, room, timeSlot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assignment)) return false;
        Assignment that = (Assignment) o;
        return Objects.equals(session, that.session);
    }

    @Override
    public int hashCode() { return Objects.hash(session); }

    @Override
    public String toString() {
        return session + " -> " + room.getName() + " @ " + timeSlot;
    }
}
