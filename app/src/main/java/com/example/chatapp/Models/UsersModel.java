package com.example.chatapp.Models;

public class UsersModel {
    String mName,mEmail ,id;

    public String getmName() {
        return mName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public String getId() {
        return id;
    }

    public UsersModel(String mName, String mEmail, String id) {
        this.mName = mName;
        this.mEmail = mEmail;
        this.id = id;
    }

    public UsersModel() {

    }




}
