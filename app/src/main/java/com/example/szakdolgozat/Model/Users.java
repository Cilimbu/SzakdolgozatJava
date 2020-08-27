package com.example.szakdolgozat.Model;

public class Users {
    private String name, email, pass, userID;

    public Users ()
    {

    }

    public Users(String name, String email, String pass)
    {
        this.name=name;
        this.email=email;
        this.pass=pass;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

}
