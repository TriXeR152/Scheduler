package ga;


public interface Repairer {
    void repair(Timetable timetable, ProblemInstance problem);

    Repairer NONE = (timetable, problem) -> { /* baseline GA: no repair */ };
}
