package com.example.alpha.Model;

public class User {

    private String FullName;
    private String Email;
    private String Image;

    public User() {

    }

    public User(String fullName, String email) {
        FullName = fullName;
        Email = email;

    }

    public User(String fullName, String email, String img) {
        FullName = fullName;
        Email = email;
        Image = img;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getImage() {
        return Image;
    }


}
