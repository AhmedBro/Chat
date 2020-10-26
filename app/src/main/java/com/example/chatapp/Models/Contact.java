package com.example.chatapp.Models;

public class Contact {
    String mPhone, mName, mImage, mId , mLastMessage , mTime;

    public Contact(String mPhone, String id, String mName, String mImage) {
        this.mPhone = mPhone;
        this.mName = mName;
        this.mImage = mImage;
        mId = id;
    }

    public Contact() {
    }

    public String getmId() {
        return mId;
    }

    public String getmLastMessage() {
        return mLastMessage;
    }

    public String getmTime() {
        return mTime;
    }

    public String getmPhone() {
        return mPhone;
    }

    public String getmName() {
        return mName;
    }

    public String getmImage() {
        return mImage;
    }
}
