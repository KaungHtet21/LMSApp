package com.khk.lmsapp.modules;

import java.util.HashMap;
import java.util.Objects;

public class Message {
    private String message, msgId, type, from, to, date, time, name;

    public Message(){

    }

    public Message(String message, String msgId, String type, String from, String to, String date, String time, String name) {
        this.message = message;
        this.msgId = msgId;
        this.type = type;
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Message)
            return msgId.equals(((Message) object).msgId);
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, msgId, type, to, from, time, date, name);
    }
}
