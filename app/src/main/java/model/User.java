package model;

import java.io.Serializable;

/**
 * Created by anotauntanto on 21/3/16.
 */
public class User implements Serializable {

    String username;
    String photo;
    String email;

    public User(String email, String photo, String username) {
        this.email = email;
        this.photo = photo;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", photo='" + photo + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
