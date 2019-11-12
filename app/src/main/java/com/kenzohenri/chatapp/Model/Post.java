package com.kenzohenri.chatapp.Model;

import com.google.firebase.database.ServerValue;

public class Post {
    private String title;
    private String description;
    private String userphoto;
    private String userId;
    private String username;
    private Object timeStamp;
    private String postKey;
    private String search;

    public Post(String title, String description, String userphoto, String userId, String username, String search) {
        this.title = title;
        this.description = description;
        this.userphoto = userphoto;
        this.userId = userId;
        this.username = username;
        this.timeStamp = ServerValue.TIMESTAMP;
        this.search = search;
    }

    public Post() {
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserphoto() {
        return userphoto;
    }

    public void setUserphoto(String userphoto) {
        this.userphoto = userphoto;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
