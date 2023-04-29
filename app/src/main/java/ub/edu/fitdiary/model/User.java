package ub.edu.fitdiary.model;

/**
 * Classe contenidor de la informació de l'usuari.
 */
public class User {
    private String email; // Per exemple, el mail
    private String name;
    private String surname;
    private String birthday;
    private String sex;
    private String pictureURL; // Url d'Internet, no la foto en si

    // Constructor

    public User(){
    }
    public User(
            String email,
            String name,
            String surname,
            String birthday,
            String sex,
            String pictureURL
    ) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.birthday = birthday;
        this.sex = sex;
        this.pictureURL = pictureURL;
    }


    // Getters
    public String getEmail(){return this.email;}
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

    // Setters
    public void setEmail (String email) { this.email = email;}
    public void setName (String name) { this.name = name; }
    public void setSurname (String surnmae) {
        this.surname = surname;
    }
    public void setBirthday(String birthday){ this.birthday = birthday;}
    public void setSex(String sex) {
        this.sex = sex;
    }
    public void setUrl(String pictureUrl) { this.pictureURL = pictureUrl; }
}
