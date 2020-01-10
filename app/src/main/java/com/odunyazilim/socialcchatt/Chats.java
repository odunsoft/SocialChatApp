package com.odunyazilim.socialcchatt;

public class Chats {


    String message, receiver, sender,date,time,type,messageID;

    public Chats() {

    }

    public Chats(String message, String receiver, String sender, String date, String time, String type, String messageID) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.date = date;
        this.time = time;
        this.type = type;
        this.messageID = messageID;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }
}
