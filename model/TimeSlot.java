package model;

import java.util.Objects;

/**
 * represents one discrete period in the weekly timetable grid
 * day: 0 = monday ... 4 = friday (adjust MAX_DAYS if you include Saturday)
 * period: 0-based index into the day's periods (for example 0 = 08:00-09:00)
 */
public class TimeSlot {
    public static final int MAX_DAYS = 5;
    public static final int MAX_PERIODS_PER_DAY = 8;

    private final int day;
    private final int period;

    public TimeSlot(int day, int period) {
        if (day < 0 || day >= MAX_DAYS) {
            throw new IllegalArgumentException("day out of range: " + day);
        }
        if (period < 0 || period >= MAX_PERIODS_PER_DAY) {
            throw new IllegalArgumentException("period out of range: " + period);
        }
        this.day = day;
        this.period = period;
    }

    public int getDay() { return day; }
    public int getPeriod() { return period; }

    // flattened index, useful for array-backed lookups / random selection
    public int toIndex() {
        return day * MAX_PERIODS_PER_DAY + period;
    }

    public static TimeSlot fromIndex(int index) {
        return new TimeSlot(index / MAX_PERIODS_PER_DAY, index % MAX_PERIODS_PER_DAY);
    }

    public static int totalSlots() {
        return MAX_DAYS * MAX_PERIODS_PER_DAY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot)) return false;
        TimeSlot that = (TimeSlot) o;
        return day == that.day && period == that.period;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, period);
    }

    @Override
    public String toString() {
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri"};
        return dayNames[day] + " P" + (period + 1);
    }
}
