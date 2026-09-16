package ga;

public class Individual {
    private final Timetable timetable;
    private final ScoreReport score;

    public Individual(Timetable timetable, ScoreReport score) {
        this.timetable = timetable;
        this.score = score;
    }

    public Timetable getTimetable() { return timetable; }
    public ScoreReport getScore() { return score; }
}
