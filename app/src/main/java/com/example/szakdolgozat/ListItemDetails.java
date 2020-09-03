package com.example.szakdolgozat;

import java.io.Serializable;

public class ListItemDetails implements Serializable{
    String ID, checked, name;

    public ListItemDetails(String ID, String checked, String name) {
        this.ID = ID;
        this.checked = checked;
        this.name = name;
    }

    @Override
    public String toString() {
        if(name!=null)
        {
            return String.format(name);
        }
        else
        {
            return "";
        }

    }

    public ListItemDetails() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
