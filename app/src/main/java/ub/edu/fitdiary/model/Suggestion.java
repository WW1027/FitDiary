package ub.edu.fitdiary.model;

public class Suggestion {
    private final static String TAG = "Suggestion";
    private Date date;
    private String suggestion;

    public Suggestion(){

    }
    public Suggestion(Date date, String suggestion) {
        this.date = date;
        this.suggestion = suggestion;
    }

    public Date getDate() {return date;}
    public String getSuggestion() {return suggestion;}

    public void setDate(Date date) {this.date = date;}
    public void setSuggestion(String suggestion) {this.suggestion = suggestion;}
}
