package com.example.shareyourdayapp.Model;

import com.example.shareyourdayapp.ExClass.PostID;

import java.sql.Timestamp;

public class PostModel extends com.example.shareyourdayapp.ExClass.PostID{

    private String desc;
    private String postImage;
    private String timestamp;
    private String userID;
    private String postID;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

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
