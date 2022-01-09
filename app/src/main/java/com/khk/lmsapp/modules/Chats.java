package com.khk.lmsapp.modules;

public class Chats {
    private String id, message, name, state, type;

    public Chats() {
    }

    public Chats(String id, String message, String name, String state, String type) {
        this.id = id;
        this.message = message;
        this.name = name;
        this.state = state;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
