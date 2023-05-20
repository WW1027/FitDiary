package ub.edu.fitdiary.model;

public class Event {
    private String date;
    private String sport;
    private String duration;
    private String pulse;
    private String imageURL;
    private String comment;

    //Constructor

    public Event(){

    }
    public Event(String date, String sport, String duration, String pulse, String imageURL, String comment) {
        this.date = date;
        this.sport = sport;
        this.duration = duration;
        this.pulse = pulse;
        this.imageURL = imageURL;
        this.comment = comment;
    }

    //Getters and setters
    public String getDate() { return date;}
    public String getSport() { return sport; }
    public String getDuration() { return duration; }
    public String getPulse() { return pulse; }
    public String getImageURL() { return imageURL; }
    public String getComment() { return comment; }

    public void setDate(String date) { this.date = date; }
    public void setSport(String sport) { this.sport = sport; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setPulse(String pulse) { this.pulse = pulse; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }
    public void setComment(String comment) { this.comment = comment;}

}
