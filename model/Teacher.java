package model;

import java.util.HashSet;
import java.util.Set;

public class Teacher {
    private final String id;
    private final String name;
    // slots this teacher is NOT available (hard constraint input, for example part-time days)
    private final Set<TimeSlot> unavailable = new HashSet<>();
    // slots this teacher prefers (soft constraint input)
    private final Set<TimeSlot> preferred = new HashSet<>();
    private final int maxSessionsPerDay;

    public Teacher(String id, String name, int maxSessionsPerDay) {
        this.id = id;
        this.name = name;
        this.maxSessionsPerDay = maxSessionsPerDay;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getMaxSessionsPerDay() { return maxSessionsPerDay; }

    public void markUnavailable(TimeSlot slot) { unavailable.add(slot); }
    public void markPreferred(TimeSlot slot) { preferred.add(slot); }

    public boolean isAvailable(TimeSlot slot) { return !unavailable.contains(slot); }
    public boolean isPreferred(TimeSlot slot) { return preferred.contains(slot); }

    @Override
    public String toString() { return name; }
}
