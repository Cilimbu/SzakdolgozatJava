package com.example.szakdolgozat;
import java.io.Serializable;

public class ChatRoomDetails implements Serializable {

    private String creator, roomname, ID, roomPass;

    public ChatRoomDetails(String creator, String roomname, String ID, String roomPass) {
        this.creator = creator;
        this.roomname = roomname;
        this.ID = ID;
        this.roomPass = roomPass;
    }

    public ChatRoomDetails() {

    }

    public String getRoomPass() {
        return roomPass;
    }

    public void setRoomPass(String roomPass) {
        this.roomPass = roomPass;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
