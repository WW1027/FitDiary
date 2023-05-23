package ub.edu.fitdiary.model;

public class Reminder {
    private String date;
    private String sport;
    private String duration;
    private String alarm;

    //Constructor

    public Reminder(){

    }
    public Reminder(String date, String sport, String duration) {
        this.date = date;
        this.sport = sport;
        this.duration = duration;
    }

    public Reminder(String date, String sport, String duration, String alarm) {
        this.date = date;
        this.sport = sport;
        this.duration = duration;
        this.alarm = alarm;
    }

    //Getters and setters
    public String getDate() { return date;}
    public String getSport() { return sport; }
    public String getDuration() { return duration; }
    public String getAlarm() { return alarm; }

    public void setDate(String date) { this.date = date; }
    public void setSport(String sport) { this.sport = sport; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setAlarm(String alarm) { this.alarm = alarm; }


}
