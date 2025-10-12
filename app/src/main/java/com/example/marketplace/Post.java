package com.example.marketplace;

import java.io.Serializable;

public class Post implements Serializable {
    private String title;
    private String price;

    private String uid;

    public Post() {} // Required for Firestore

    public Post(String title, String price, String uid) {
        this.title = title;
        this.price = price;
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String id) {
        this.uid = id;
    }
}
