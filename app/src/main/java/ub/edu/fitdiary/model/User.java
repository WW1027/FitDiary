package ub.edu.fitdiary.model;

/**
 * Classe contenidor de la informació de l'usuari.
 */
public class User {
    private String email; // Per exemple, el mail
    private String username;
    private String name;
    private String surname;
    private String birthday;
    private String sex;
    private String pictureURL; // Url d'Internet, no la foto en si
    private int goal;

    // Constructor

    public User(){
    }
    public User(
            String email,
            String username,
            String name,
            String surname,
            String birthday,
            String sex,
            String pictureURL
    ) {
        this.email = email;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.birthday = birthday;
        this.sex = sex;
        this.pictureURL = pictureURL;
    }


    // Getters
    public String getEmail(){return this.email;}
    public String getUsername(){return this.username;}
    public String getName () {
        return this.name;
    }
    public String getSurname () {
        return this.surname;
    }
    public String getBirthday(){ return this.birthday;}
    public String getSex() {
        return this.sex;
    }
    public String getURL() { return this.pictureURL; }
    public int getGoal() { return goal;}

    // Setters
    public void setEmail (String email) { this.email = email;}
    public void setUsername(String username){ this.username = username;}
    public void setName (String name) { this.name = name; }
    public void setSurname (String surname) {
        this.surname = surname;
    }
    public void setBirthday(String birthday){ this.birthday = birthday;}
    public void setSex(String sex) {
        this.sex = sex;
    }
    public void setUrl(String pictureUrl) { this.pictureURL = pictureUrl; }
    public void setGoal(int goal) { this.goal = goal; }
}