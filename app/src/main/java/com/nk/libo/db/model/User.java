package com.nk.libo.db.model;

import java.io.Serializable;

public class User implements Serializable {
    private String address, department, email, libraryId, name, phone, photoUrl, uid, username;

    public User() {}

    public User(String address, String department, String email, String libraryId, String name, String phone, String photoUrl, String uid, String username) {
        this.address = address;
        this.department = department;
        this.email = email;
        this.libraryId = libraryId;
        this.name = name;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.uid = uid;
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(String libraryId) {
        this.libraryId = libraryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
