package com.quickblox.ui.kit.chatmessage.adapter.listeners;

import android.widget.TextView;

import com.quickblox.ui.kit.chatmessage.adapter.utils.QBChatMessageClickMovement;

/**
 * Interface used to handle Long clicks on the {@link TextView} and taps
 * on the phone, web, mail links inside of {@link TextView}.
 */
public interface QBChatMessageLinkClickListener {

    /**
     * This method will be invoked when user press and hold
     * finger on the {@link TextView}
     *
     * @param linkText Text which contains link on which user presses.
     * @param linkType Type of the link can be one of {@link QBChatMessageClickMovement.QBLinkType} enumeration
     * @param positionInAdapter Index
     */
    void onLinkClicked(final String linkText, final QBChatMessageClickMovement.QBLinkType linkType, int positionInAdapter);

    /**
     * @param text Whole text of {@link TextView}
     */
    void onLongClick(final String text);
}