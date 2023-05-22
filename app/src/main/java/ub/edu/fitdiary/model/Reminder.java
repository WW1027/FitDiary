package ub.edu.fitdiary.model;

public class Reminder {
    private String date;
    private String sport;

    private String duration;
    private String timeBefore;

    public Reminder() {
    }

    public Reminder(String date, String sport, String duration, String timeBefore) {
        this.date = date;
        this.sport = sport;
        this.duration = duration;
        this.timeBefore = timeBefore;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTimeBefore() {
        return timeBefore;
    }

    public void setTimeBefore(String timeBefore) {
        this.timeBefore = timeBefore;
    }
}
