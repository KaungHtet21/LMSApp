package com.khk.lmsapp.modules;

import java.util.Objects;

public class Students {
    private String address, batch, bd, email, gender, id, image, name, password, ph, role, roll;

    public Students(){

    }

    public Students(String address, String batch, String bd, String email, String gender, String id, String image, String name, String password, String ph, String role, String roll) {
        this.address = address;
        this.batch= batch;
        this.bd = bd;
        this.email = email;
        this.gender = gender;
        this.id = id;
        this.image = image;
        this.name = name;
        this.password = password;
        this.ph = ph;
        this.role = role;
        this.roll = roll;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBd() {
        return bd;
    }

    public void setBd(String bd) {
        this.bd = bd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Students){
            return id.equals(((Students) object).id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, bd, email, gender, id, name, password, ph, role);
    }

    @Override
    public String toString() {
        return "Students{" +
                "address='" + address + '\'' +
                ", bd='" + bd + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", id='" + id + '\'' +
                ", image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", ph='" + ph + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
