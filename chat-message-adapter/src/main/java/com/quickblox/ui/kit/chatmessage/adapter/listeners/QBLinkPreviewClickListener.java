package com.quickblox.ui.kit.chatmessage.adapter.listeners;

import com.quickblox.ui.kit.chatmessage.adapter.models.QBLinkPreview;

public interface QBLinkPreviewClickListener {

    void onLinkPreviewClicked(String link, QBLinkPreview linkPreview, int position);

    void onLinkPreviewLongClicked(String link, QBLinkPreview linkPreview, int position);
}
