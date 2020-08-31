package com.example.szakdolgozat;

import com.example.szakdolgozat.CurrentOnline.CurrentUsers;

import java.io.Serializable;

public class PrivateChatDetails implements Serializable {

    public String creator, partner, message, messageID, roomID;

    public PrivateChatDetails(String creator, String partner, String message, String messageID, String roomID, String roomname) {
        this.creator = creator;
        this.partner = partner;
        this.message = message;
        this.messageID = messageID;
        this.roomID = roomID;
    }

    public PrivateChatDetails() {

    }

    @Override
    public String toString() {
        if(CurrentUsers.currentOnlineUser.getEmail().equals(creator))
        {
            return String.format(partner);
        }
        else{
            return String.format(creator);
        }
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getcreator() {
        return creator;
    }

    public void setcreator(String creator) {
        this.creator = creator;
    }

    public String getpartner() {
        return partner;
    }

    public void setpartner(String partner) {
        this.partner = partner;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }
}
