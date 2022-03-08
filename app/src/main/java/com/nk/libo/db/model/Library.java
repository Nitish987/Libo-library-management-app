package com.nk.libo.db.model;

import java.io.Serializable;
import java.util.List;

public class Library implements Serializable {
    private List<String> admin;
    private int bookCount;
    private String college, id;
    private int lateFine;
    private String name, phone, photoUrl;
    private List<String> search;

    public Library() {}

    public Library(List<String> admin, int bookCount, String college, String id, int lateFine, String name, String phone, String photoUrl, List<String> search) {
        this.admin = admin;
        this.bookCount = bookCount;
        this.college = college;
        this.id = id;
        this.lateFine = lateFine;
        this.name = name;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.search = search;
    }

    public List<String> getAdmin() {
        return admin;
    }

    public void setAdmin(List<String> admin) {
        this.admin = admin;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLateFine() {
        return lateFine;
    }

    public void setLateFine(int lateFine) {
        this.lateFine = lateFine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getSearch() {
        return search;
    }

    public void setSearch(List<String> search) {
        this.search = search;
    }
}
