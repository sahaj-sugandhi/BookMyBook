package com.example.bookmybook.data;

import java.io.Serializable;

public class BookInfo implements Serializable {
    public String name;
    public String authorName;
    public String edition;
    public String price;
    public String photoUrl;
    public String uploadedBy;
    public BookInfo(){}
    public BookInfo(String name, String authorName, String edition, String price, String photoUrl,String uploadedBy){
        this.name=name;
        this.authorName=authorName;
        this.edition=edition;
        this.price=price;
        this.photoUrl=photoUrl;
        this.uploadedBy=uploadedBy;
    }
    public BookInfo(String name, String authorName, String edition, String price, String uploadedBy){
        this.name=name;
        this.authorName=authorName;
        this.edition=edition;
        this.price=price;
        this.uploadedBy=uploadedBy;
        this.photoUrl="";
    }
    public void addPhotoUrl(String photoUrl){
        this.photoUrl=photoUrl;
    }
}
