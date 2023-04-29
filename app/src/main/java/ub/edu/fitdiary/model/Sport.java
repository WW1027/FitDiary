package ub.edu.fitdiary.model;

public class Sport {
    private String name;
    private float caloriesPerHour;

    //Constructor
    public Sport(String name) {
        this.name = name;
        this.caloriesPerHour = caloriesPerHour;
    }

    //Getters and setters
    public String getName() { return name; }
    public float getCaloriesPerHour() { return caloriesPerHour; }

    public void setName(String name) { this.name = name; }
    public void setCaloriesPerHour(float caloriesPerHour) { this.caloriesPerHour = caloriesPerHour; }
}
