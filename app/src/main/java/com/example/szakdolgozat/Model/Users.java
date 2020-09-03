package com.example.szakdolgozat.Model;

public class Users {
    private String email, name;

    public Users ()
    {

    }

    public Users(String email, String name)
    {
        this.email=email;
        this.name =name;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }
    }
