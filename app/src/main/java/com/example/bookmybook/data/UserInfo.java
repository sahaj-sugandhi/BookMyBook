package com.example.bookmybook.data;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserInfo {
    public String name;
    public String email;
    public String college;
    public String branch;

    public UserInfo(){}

    public UserInfo(String name, String email, String college, String branch){
        this.name=name;
        this.email=email;
        this.college=college;
        this.branch=branch;
    }
}