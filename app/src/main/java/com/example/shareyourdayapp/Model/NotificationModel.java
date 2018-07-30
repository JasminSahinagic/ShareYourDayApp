package com.example.shareyourdayapp.Model;

import java.io.Serializable;

public class NotificationModel implements Serializable {

    private String userID;
    private String postID;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }
}
