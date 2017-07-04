package com.quickblox.ui.kit.chatmessage.adapter.models;

import com.google.gson.annotations.SerializedName;
import com.quickblox.chat.model.QBChatDialog;

import java.io.Serializable;

public class QBLinkPreview implements Serializable{

    @SerializedName("ogTitle")
    private String title;

    @SerializedName("ogDescription")
    private String description;

    @SerializedName("ogImage")
    private QBLinkPreviewImage image;

    @SerializedName("ogLocale")
    private String locale;

    @SerializedName("ogSiteName")
    private String siteName;

    @SerializedName("ogUrl")
    private String url;

    @SerializedName("ogType")
    private String type;     //possible "site", "website"

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

    public QBLinkPreviewImage getImage() {
        return image;
    }

    public void setImage(QBLinkPreviewImage image) {
        this.image = image;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(QBLinkPreview.class.getSimpleName());
        stringBuilder.append("{").append("title").append("=").append(getTitle()).
                append(", description").append("=").append(getDescription()).
                append(", image").append("=").append(getImage()).
                append(", locale").append("=").append(getLocale()).
                append(", siteName").append("=").append(getSiteName()).
                append(", url").append("=").append(getUrl()).
                append(", type").append("=").append(getType()).
                append("}");
        return stringBuilder.toString();
    }
}
