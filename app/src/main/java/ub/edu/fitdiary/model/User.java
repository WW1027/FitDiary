package ub.edu.fitdiary.model;

/**
 * Classe contenidor de la informació de l'usuari.
 */
public class User {
    private String mId; // Per exemple, el mail
    private String mFirstName;
    private String mLastName;
    private String mBirthday;
    private String mSex;
    private String mPictureURL; // Url d'Internet, no la foto en si

    // Constructor
    public User(
            String id,
            String firstName,
            String lastName,
            String birthday,
            String sex,
            String pictureURL
    ) {
        this.mId = id;
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mBirthday = birthday;
        this.mSex = sex;
        this.mPictureURL = pictureURL;
    }

    // Getters
    public String getFirstName () {
        return this.mFirstName;
    }
    public String getLastName () {
        return this.mLastName;
    }
    public String getBirthday(){ return this.mBirthday;}
    public String getSex() {
        return this.mSex;
    }
    public String getURL() { return this.mPictureURL; }

    // Setters
    public void setFirstName (String firstName) { this.mFirstName = firstName; }
    public void setLastName (String lastName) {
        this.mLastName = lastName;
    }
    public void setBirthday(String birthday){ this.mBirthday = birthday;}
    public void setSex(String sex) {
        this.mSex = sex;
    }
    public void setUrl(String pictureUrl) { this.mPictureURL = pictureUrl; }
}
