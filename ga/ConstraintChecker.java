package ga;

import model.*;

import java.util.*;


public class ConstraintChecker {

    public ScoreReport evaluate(Timetable timetable) {
        ScoreReport report = new ScoreReport();

        // hard constraints
        checkRoomDoubleBooking(timetable, report);
        checkTeacherDoubleBooking(timetable, report);
        checkGroupDoubleBooking(timetable, report);
        checkRoomSuitability(timetable, report);
        checkTeacherAvailability(timetable, report);

        // soft constraints
        scoreTeacherPreferences(timetable, report);
        scoreTeacherDailyLoad(timetable, report);
        scoreGroupScheduleGaps(timetable, report);

        return report;
    }

    
    private void checkRoomDoubleBooking(Timetable tt, ScoreReport report) {
    	
        Map<String, Integer> occupancy = new HashMap<>();
        
        for (Assignment a : tt.getAssignments()) {
        	
            String key = a.getRoom().getId() + "@" + a.getTimeSlot().toIndex();
            occupancy.merge(key, 1, Integer::sum);
        }
        for (int count : occupancy.values()) {
            if (count > 1) report.hardViolations += (count - 1);
        }
    }

    
    private void checkTeacherDoubleBooking(Timetable tt, ScoreReport report) {
    	
        Map<String, Integer> occupancy = new HashMap<>();
        
        for (Assignment a : tt.getAssignments()) {
        	
            Teacher t = a.getSession().getCourse().getTeacher();
            String key = t.getId() + "@" + a.getTimeSlot().toIndex();
            occupancy.merge(key, 1, Integer::sum);
        }
        for (int count : occupancy.values()) {
            if (count > 1) report.hardViolations += (count - 1);
        }
    }

    
    private void checkGroupDoubleBooking(Timetable tt, ScoreReport report) {
    	
        Map<String, Integer> occupancy = new HashMap<>();
        
        for (Assignment a : tt.getAssignments()) {
        	
            StudentGroup g = a.getSession().getCourse().getGroup();
            String key = g.getId() + "@" + a.getTimeSlot().toIndex();
            occupancy.merge(key, 1, Integer::sum);
        }
        for (int count : occupancy.values()) {
            if (count > 1) report.hardViolations += (count - 1);
        }
    }

   
    private void checkRoomSuitability(Timetable tt, ScoreReport report) {
    	
        for (Assignment a : tt.getAssignments()) {
        	
            Course course = a.getSession().getCourse();
            
            if (!a.getRoom().canHost(course.getGroup().getSize(), course.getRequiredRoomType())) {
                report.hardViolations += 1;
            }
        }
    }

   
    private void checkTeacherAvailability(Timetable tt, ScoreReport report) {
    	
        for (Assignment a : tt.getAssignments()) {
        	
            Teacher t = a.getSession().getCourse().getTeacher();
            
            if (!t.isAvailable(a.getTimeSlot())) {
                report.hardViolations += 1;
            }
        }
    }

    
    private void scoreTeacherPreferences(Timetable tt, ScoreReport report) {
    	
        final double PREFERENCE_BONUS = 2.0;
        
        for (Assignment a : tt.getAssignments()) {
        	
            Teacher t = a.getSession().getCourse().getTeacher();
            
            if (!t.isPreferred(a.getTimeSlot())) {
                // only penalize, matching preferred slots costs nothing extra
                report.softPenalty += PREFERENCE_BONUS * 0.1;
            }
        }
    }

    // penalize a teacher being scheduled beyond their daily session cap.
    private void scoreTeacherDailyLoad(Timetable tt, ScoreReport report) {
    	
        final double OVERLOAD_PENALTY = 5.0;
        Map<String, int[]> perDayCount = new HashMap<>(); 
        Map<String, Teacher> teacherLookup = new HashMap<>();

        for (Assignment a : tt.getAssignments()) {
        	
            Teacher t = a.getSession().getCourse().getTeacher();
            teacherLookup.put(t.getId(), t);
            
            int[] counts = perDayCount.computeIfAbsent(t.getId(), k -> new int[TimeSlot.MAX_DAYS]);
            counts[a.getTimeSlot().getDay()]++;
            
        }
        for (Map.Entry<String, int[]> entry : perDayCount.entrySet()) {
        	
            Teacher t = teacherLookup.get(entry.getKey());
            
            for (int dailyCount : entry.getValue()) {
                if (dailyCount > t.getMaxSessionsPerDay()) {
                    report.softPenalty += OVERLOAD_PENALTY * (dailyCount - t.getMaxSessionsPerDay());
                }
            }
        }
    }

    
    private void scoreGroupScheduleGaps(Timetable tt, ScoreReport report) {
    	
        final double GAP_PENALTY = 1.0;
        
        
        Map<String, Map<Integer, List<Integer>>> schedule = new HashMap<>();

        for (Assignment a : tt.getAssignments()) {
        	
            StudentGroup g = a.getSession().getCourse().getGroup();
            
            schedule.computeIfAbsent(g.getId(), k -> new HashMap<>())
                    .computeIfAbsent(a.getTimeSlot().getDay(), k -> new ArrayList<>())
                    .add(a.getTimeSlot().getPeriod());
        }

        for (Map<Integer, List<Integer>> dayMap : schedule.values()) {
        	
            for (List<Integer> periods : dayMap.values()) {
                if (periods.size() < 2) continue;
                Collections.sort(periods);
                int span = periods.get(periods.size() - 1) - periods.get(0) + 1;
                int gaps = Math.max(0, span - periods.size());
                report.softPenalty += GAP_PENALTY * gaps;
            }
        }
    }
}
