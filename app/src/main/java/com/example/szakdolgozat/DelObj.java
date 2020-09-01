package com.example.szakdolgozat;
import java.io.Serializable;

public class DelObj implements Serializable{
    String ID;


    public DelObj(String ID) {
        this.ID = ID;
    }

    public DelObj() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
