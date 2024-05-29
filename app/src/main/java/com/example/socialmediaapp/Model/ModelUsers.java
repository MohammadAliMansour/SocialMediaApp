package com.example.socialmediaapp.model;

import android.graphics.Bitmap;

public class ModelUsers {
    String name;
    String email;
    String image;
    String uid;
    String fcmToken;
    private Bitmap wallpaper;


    public ModelUsers() {
    }

    public ModelUsers(String email) {
        this.email = email;
    }

    public ModelUsers(String name, String email, String uid) {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Bitmap getWallpaper() {
        return wallpaper;
    }

    public void setWallpaper(Bitmap wallpaper) {
        this.wallpaper = wallpaper;
    }
}
