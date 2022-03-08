package com.nk.libo.db.model;

import java.io.Serializable;
import java.util.List;

public class Book implements Serializable {
    private String author, bClass, category;
    private long date;
    private String id, isbn;
    private int issueCount;
    private String lib, name, number, pdfUrl, photoUrl, pub;
    private int quantity;
    private List<String> search;

    public Book() {}

    public Book(String author, String bClass, String category, long date, String id, String isbn, int issueCount, String lib, String name, String number, String pdfUrl, String photoUrl, String pub, int quantity, List<String> search) {
        this.author = author;
        this.bClass = bClass;
        this.category = category;
        this.date = date;
        this.id = id;
        this.isbn = isbn;
        this.issueCount = issueCount;
        this.lib = lib;
        this.name = name;
        this.number = number;
        this.pdfUrl = pdfUrl;
        this.photoUrl = photoUrl;
        this.pub = pub;
        this.quantity = quantity;
        this.search = search;
    }

    public String getbClass() {
        return bClass;
    }

    public void setbClass(String bClass) {
        this.bClass = bClass;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(int issueCount) {
        this.issueCount = issueCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPub() {
        return pub;
    }

    public void setPub(String pub) {
        this.pub = pub;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<String> getSearch() {
        return search;
    }

    public void setSearch(List<String> search) {
        this.search = search;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getLib() {
        return lib;
    }

    public void setLib(String lib) {
        this.lib = lib;
    }
}
