package com.example.alpha.Model;

public class User {

    private String FullName;
    private String Email;


    public User() {

    }

    public User(String fullName, String email) {
        FullName = fullName;
        Email = email;

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


}
