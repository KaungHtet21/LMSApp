package com.khk.lmsapp.modules;

public class Course {
    String name, lecturer, credit;

    public Course(String name, String lecturer, String credit) {
        this.name = name;
        this.lecturer = lecturer;
        this.credit = credit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }
}
