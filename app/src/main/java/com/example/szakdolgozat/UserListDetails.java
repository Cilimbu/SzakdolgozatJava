package com.example.szakdolgozat;


import java.io.Serializable;

public class UserListDetails implements Serializable {

    private String ID, email, name, shared, date;

    public UserListDetails(String ID, String email, String name, String shared, String date) {
        this.ID = ID;
        this.email = email;
        this.name = name;
        this.shared = shared;
        this.date = date;
    }

    public UserListDetails() {

    }

    @Override
    public String toString() {
        return String.format(name);
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShared() {
        return shared;
    }

    public void setShared(String shared) {
        this.shared = shared;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
