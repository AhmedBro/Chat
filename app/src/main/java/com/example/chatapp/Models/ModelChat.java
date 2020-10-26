package com.example.chatapp.Models;

public class ModelChat {
    String mMessage , mSender, mReceiver,mTime , mLastMessage;
    Boolean mIsSeen;

    public ModelChat(String mMessage, String mSender, String mReceiver, String mTime, Boolean mIsSeen) {
        this.mMessage = mMessage;
        this.mSender = mSender;
        this.mReceiver = mReceiver;
        this.mTime = mTime;
        this.mIsSeen = mIsSeen;
    }

    public ModelChat() {
    }

    public String getmLastMessage() {
        return mLastMessage;
    }

    public String getmMessage() {
        return mMessage;
    }

    public String getmSender() {
        return mSender;
    }

    public String getmReceiver() {
        return mReceiver;
    }

    public String getmTime() {
        return mTime;
    }

    public Boolean getmIsSeen() {
        return mIsSeen;
    }
}
