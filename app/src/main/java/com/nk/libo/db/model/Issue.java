package com.nk.libo.db.model;

import java.io.Serializable;

public class Issue implements Serializable {
    private String bookId, by, id, issuePeriod;
    private long issueTime;
    private String lib;
    private long submitTime;

    public Issue() {}

    public Issue(String bookId, String by, String id, String issuePeriod, long issueTime, String lib, long submitTime) {
        this.bookId = bookId;
        this.by = by;
        this.id = id;
        this.issuePeriod = issuePeriod;
        this.issueTime = issueTime;
        this.lib = lib;
        this.submitTime = submitTime;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getIssuePeriod() {
        return issuePeriod;
    }

    public void setIssuePeriod(String issuePeriod) {
        this.issuePeriod = issuePeriod;
    }

    public long getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(long issueTime) {
        this.issueTime = issueTime;
    }

    public String getLib() {
        return lib;
    }

    public void setLib(String lib) {
        this.lib = lib;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(long submitTime) {
        this.submitTime = submitTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
