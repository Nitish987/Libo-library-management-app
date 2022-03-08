package com.nk.libo.db.model;

public class Member {
    private String uid;

    public Member() {}

    public Member(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
