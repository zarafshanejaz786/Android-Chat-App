package com.example.chatapp;

public class Upload {
    private String mId;
    private String mImageUrl;

    public Upload() {
        // Required empty constructor for Firebase
    }

    public Upload(String id, String imageUrl) {
        if (imageUrl.trim().equals("")) {
            imageUrl = null;
        }

        mId = id;
        mImageUrl = imageUrl;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}

