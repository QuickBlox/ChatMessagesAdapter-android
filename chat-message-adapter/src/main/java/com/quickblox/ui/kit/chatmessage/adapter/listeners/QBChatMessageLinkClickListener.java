package com.quickblox.ui.kit.chatmessage.adapter.listeners;

import android.widget.TextView;

import com.quickblox.ui.kit.chatmessage.adapter.utils.QBMessageTextClickMovement;

/**
 * Interface used to handle Long clicks on the {@link TextView} and taps
 * on the phone, web, mail links inside of {@link TextView}.
 */
public interface QBChatMessageLinkClickListener {

    /**
     * This method will be invoked when user press and hold
     * finger on the {@link TextView}
     *
     * @param linkText          Text which contains link on which user presses.
     * @param linkType          Type of the link can be one of {@link QBMessageTextClickMovement.QBLinkType} enumeration
     * @param positionInAdapter Index of item with this TextView in message adapter
     */
    void onLinkClicked(final String linkText, final QBMessageTextClickMovement.QBLinkType linkType, int positionInAdapter);

    /**
     * @param text              Whole text of {@link TextView}
     * @param positionInAdapter Index of item with this TextView in message adapter
     */
    void onLongClick(final String text, int positionInAdapter);
}