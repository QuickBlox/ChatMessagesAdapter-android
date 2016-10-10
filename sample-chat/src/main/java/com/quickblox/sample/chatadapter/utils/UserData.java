package com.quickblox.sample.chatadapter.utils;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserData implements Serializable {

    @SerializedName("avatar_url")
    private String userAvatar;

    @SerializedName("status")
    private String status;

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public String getStatus() {
        return status;
    }
}
