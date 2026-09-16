package output;

import ga.Timetable;
import model.*;

import java.util.function.Predicate;


public class TimetableGridFormatter {

    private static final String[] DAY_NAMES = {"Mon", "Tue", "Wed", "Thu", "Fri"};

    public static String forGroup(Timetable timetable, StudentGroup group) {
        return render("Weekly Timetable - " + group.getName(),
                buildGrid(timetable, a -> a.getSession().getCourse().getGroup().equals(group)));
    }

    public static String forTeacher(Timetable timetable, Teacher teacher) {
        return render("Weekly Timetable - " + teacher.getName(),
                buildGrid(timetable, a -> a.getSession().getCourse().getTeacher().equals(teacher)));
    }

    private static String[][] buildGrid(Timetable timetable, Predicate<Assignment> filter) {
    	
        String[][] grid = new String[TimeSlot.MAX_PERIODS_PER_DAY][TimeSlot.MAX_DAYS];
        
        for (Assignment a : timetable.getAssignments()) {
            if (!filter.test(a)) continue;
            Course course = a.getSession().getCourse();
            int day = a.getTimeSlot().getDay();
            int period = a.getTimeSlot().getPeriod();
            grid[period][day] = course.getSubjectName() + " (" + a.getRoom().getName() + ")";
        }
        return grid;
    }

    private static String render(String title, String[][] grid) {
    	
        int colWidth = 22;
        StringBuilder sb = new StringBuilder();
        sb.append(title).append(System.lineSeparator());
        sb.append(pad("Period", 8));
        for (String day : DAY_NAMES) sb.append(pad(day, colWidth));
        sb.append(System.lineSeparator());

        for (int period = 0; period < grid.length; period++) {
            sb.append(pad("P" + (period + 1), 8));
            for (int day = 0; day < DAY_NAMES.length; day++) {
                String cell = grid[period][day] == null ? "-" : grid[period][day];
                sb.append(pad(cell, colWidth));
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    private static String pad(String s, int width) {
        if (s.length() >= width) return s.substring(0, width - 1) + " ";
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }
}
