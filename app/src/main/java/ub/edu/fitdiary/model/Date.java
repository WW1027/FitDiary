package ub.edu.fitdiary.model;

public class Date {

    private String numDate; // Número date
    private String dayDate; // Dia de la semana
    private int numMonth; // Dia de la semana
    private int numYear; // Dia de la semana

    public int getNumMonth() {
        return numMonth;
    }

    public void setNumMonth(int numMonth) {
        this.numMonth = numMonth;
    }

    public int getNumYear() {
        return numYear;
    }

    public void setNumYear(int numYear) {
        this.numYear = numYear;
    }

    public Date(){

    }
    public Date(String numDate, String dayDate, int numMonth, int numYear) {
        this.numDate = numDate;
        this.dayDate = dayDate;
        this.numMonth = numMonth;
        this.numYear = numYear;
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
