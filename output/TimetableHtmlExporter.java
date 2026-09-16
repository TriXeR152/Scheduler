package output;

import ga.ProblemInstance;
import ga.Timetable;
import model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;


public class TimetableHtmlExporter {

    private static final String[] DAY_NAMES = {"Mon", "Tue", "Wed", "Thu", "Fri"};

    public static void export(Path outputPath, Timetable timetable, ProblemInstance problem) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\">");
        html.append("<title>Intelligent Scheduler - Weekly Timetables</title>");
        html.append(STYLE);
        html.append("</head><body>");
        html.append("<h1>Intelligent Scheduler &mdash; Generated Timetables</h1>");

        html.append("<h2>By Student Group</h2>");
        for (StudentGroup group : problem.getGroups()) {
            html.append(renderGridHtml(group.getName(), timetable,
                    a -> a.getSession().getCourse().getGroup().equals(group)));
        }

        html.append("<h2>By Teacher</h2>");
        for (Teacher teacher : problem.getTeachers()) {
            html.append(renderGridHtml(teacher.getName(), timetable,
                    a -> a.getSession().getCourse().getTeacher().equals(teacher)));
        }

        html.append("</body></html>");
        Files.writeString(outputPath, html.toString());
    }

    private static String renderGridHtml(String label, Timetable timetable, Predicate<Assignment> filter) {
        String[][] grid = new String[TimeSlot.MAX_PERIODS_PER_DAY][TimeSlot.MAX_DAYS];
        for (Assignment a : timetable.getAssignments()) {
            if (!filter.test(a)) continue;
            Course course = a.getSession().getCourse();
            int day = a.getTimeSlot().getDay();
            int period = a.getTimeSlot().getPeriod();
            grid[period][day] = escape(course.getSubjectName())
                    + "<br><span class=\"room\">" + escape(a.getRoom().getName()) + "</span>";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<h3>").append(escape(label)).append("</h3>");
        sb.append("<table><tr><th>Period</th>");
        for (String day : DAY_NAMES) sb.append("<th>").append(day).append("</th>");
        sb.append("</tr>");

        for (int period = 0; period < grid.length; period++) {
            sb.append("<tr><td class=\"period\">P").append(period + 1).append("</td>");
            for (int day = 0; day < DAY_NAMES.length; day++) {
                String cell = grid[period][day];
                sb.append(cell == null ? "<td class=\"empty\"></td>" : "<td class=\"filled\">" + cell + "</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static final String STYLE = "<style>"
            + "body{font-family:Arial,sans-serif;margin:24px;color:#222;}"
            + "h1{font-size:22px;} h2{margin-top:32px;font-size:18px;border-bottom:2px solid #ddd;padding-bottom:4px;}"
            + "h3{margin-top:20px;font-size:15px;}"
            + "table{border-collapse:collapse;margin-bottom:8px;width:100%;max-width:900px;}"
            + "th,td{border:1px solid #ccc;padding:6px 8px;text-align:center;font-size:13px;}"
            + "th{background:#f2f2f2;}"
            + "td.period{background:#fafafa;font-weight:bold;width:60px;}"
            + "td.filled{background:#e8f0fe;}"
            + "td.empty{background:#fff;}"
            + ".room{color:#666;font-size:11px;}"
            + "</style>";
}
