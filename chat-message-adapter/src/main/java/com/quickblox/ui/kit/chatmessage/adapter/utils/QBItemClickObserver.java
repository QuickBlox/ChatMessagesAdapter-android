package com.quickblox.ui.kit.chatmessage.adapter.utils;

import android.view.View;

import com.quickblox.chat.model.QBAttachment;
import com.quickblox.ui.kit.chatmessage.adapter.listeners.QBChatAttachClickListener;

/**
 * Created by roman on 2/1/17.
 */

public class QBItemClickObserver implements View.OnClickListener {
    private int position;
    private QBAttachment attachment;
    private QBChatAttachClickListener chatAttachClickListener;


    public QBItemClickObserver(QBChatAttachClickListener qbChatAttachClickListener, QBAttachment attachment, int position) {
        this.position = position;
        this.attachment = attachment;
        this.chatAttachClickListener = qbChatAttachClickListener;
    }

    @Override
    public void onClick(View view) {
        chatAttachClickListener.onLinkClicked(attachment, position);
    }
}
