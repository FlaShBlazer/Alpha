package com.example.alpha;

public class Requests {


    private String Email;
    private String FullName;
    private String Image;

    public Requests() {

    }

    public Requests(String email, String fullName, String image) {
        Email = email;
        FullName = fullName;
        Image = image;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}

