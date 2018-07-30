package com.example.shareyourdayapp.ExClass;

import android.support.annotation.NonNull;
import com.google.firebase.firestore.Exclude;


public class PostID {
    @Exclude
    public String PostID;

    public <T extends PostID> T withId(@NonNull final String id) {
        this.PostID = id;
        return (T) this;
    }


}
