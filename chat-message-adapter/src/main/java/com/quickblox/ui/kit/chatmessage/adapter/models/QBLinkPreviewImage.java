package com.quickblox.ui.kit.chatmessage.adapter.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QBLinkPreviewImage implements Serializable{

    @SerializedName("url")
    private String imageUrl;

    @SerializedName("width")
    private long width;

    @SerializedName("height")
    private long height;

    private String type;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(QBLinkPreviewImage.class.getSimpleName());
        stringBuilder.append("{").append("imageUrl").append("=").append(getImageUrl()).
                append(", width").append("=").append(getWidth()).
                append(", height").append("=").append(getHeight()).
                append(", type").append("=").append(getType()).
                append("}");
        return stringBuilder.toString();
    }
}
