package com.quickblox.ui.kit.chatmessage.adapter.listeners;

import com.quickblox.chat.model.QBAttachment;

/**
 * Created by roman on 8/7/17.
 */

public interface QBChatAttachAudioClickListener extends QBChatAttachClickListener {

    void onLinkClicked(QBAttachment audioAttach, int positionInAdapter);
}