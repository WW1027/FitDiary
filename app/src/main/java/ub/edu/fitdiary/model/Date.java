package ub.edu.fitdiary.model;

public class Date {

    private String numDate; // Número date
    private String dayDate; // Dia de la semana

    public Date(String numDate, String dayDate) {
        this.numDate = numDate;
        this.dayDate = dayDate;
    }

    public String getNumDate() {
        return numDate;
    }

    public void setNumDate(String numDate) {
        this.numDate = numDate;
    }

    public String getDayDate() {
        return dayDate;
    }

    public void setDayDate(String dayDate) {
        this.dayDate = dayDate;
    }
}
