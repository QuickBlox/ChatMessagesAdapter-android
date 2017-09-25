package com.quickblox.sample.chatadapter.ui.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.sample.chatadapter.R;
import com.quickblox.sample.chatadapter.utils.UserData;
import com.quickblox.ui.kit.chatmessage.adapter.QBMessagesAdapter;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;


public class CustomMessageAdapter extends QBMessagesAdapter<QBChatMessage> {
    private static final String TAG = CustomMessageAdapter.class.getSimpleName();
    protected static final int TYPE_OWN_VIDEO_ATTACH = 5;
    protected static final int TYPE_OPPONENT_VIDEO_ATTACH = 6;

    private QBUser currentUser;
    private QBUser opponentUser;
    private UserData currentUserData;
    private UserData opponentUserData;

    public CustomMessageAdapter(Activity context, List<QBChatMessage> chatMessages, ArrayList<QBUser> qbUsers) {
        super(context, chatMessages);
        setUsers(qbUsers);
    }

    private void setUsers(ArrayList<QBUser> qbUsers) {
        int currentUserID = QBChatService.getInstance().getUser().getId();

        for (QBUser user : qbUsers) {
            if (user.getId().equals(currentUserID)) {
                currentUser = user;
            } else {
                opponentUser = user;
            }
        }
        setUsersData();
    }

    private void setUsersData() {
        currentUserData = new Gson().fromJson(currentUser.getCustomData(), UserData.class);
        opponentUserData = new Gson().fromJson(opponentUser.getCustomData(), UserData.class);
    }

    @Override
    protected int customViewType(int position) {
        QBChatMessage chatMessage = getItem(position);

        if (hasAttachments(chatMessage)) {
            QBAttachment attachment = chatMessage.getAttachments().iterator().next();
            if (QBAttachment.VIDEO_TYPE.equals(attachment.getType())) {
                return isIncoming(chatMessage) ? TYPE_OPPONENT_VIDEO_ATTACH : TYPE_OWN_VIDEO_ATTACH;
            }
        }
        return -1;
    }

    @Override
    protected void onBindViewCustomHolder(QBMessageViewHolder holder, QBChatMessage chatMessage, int position) {
        displayAttachment(holder, position);
        holder.avatar.setVisibility(View.GONE);
    }

    @Override
    protected void onBindViewMsgRightHolder(TextMessageHolder holder, QBChatMessage chatMessage, int position) {
        TextView view = (TextView) holder.itemView.findViewById(R.id.custom_text_view);
        view.setText(currentUser.getFullName());
        super.onBindViewMsgRightHolder(holder, chatMessage, position);
    }

    @Override
    protected void onBindViewMsgLeftHolder(TextMessageHolder holder, QBChatMessage chatMessage, int position) {
        holder.timeTextMessageTextView.setVisibility(View.GONE);

        TextView textView = (TextView) holder.itemView.findViewById(R.id.opponent_name_text_view);
        textView.setText(opponentUser.getFullName());

        TextView customMessageTimeTextView = (TextView) holder.itemView.findViewById(R.id.custom_msg_text_time_message);
        customMessageTimeTextView.setText(getDate(chatMessage.getDateSent()));

        super.onBindViewMsgLeftHolder(holder, chatMessage, position);
    }

    @Override
    public String getImageUrl(int position) {
        QBAttachment attachment = getQBAttach(position);
        return attachment.getUrl();
    }

    @Override
    public String obtainAvatarUrl(int valueType, QBChatMessage chatMessage) {
        return currentUser.getId().equals(chatMessage.getSenderId()) ?
                currentUserData.getUserAvatar() : opponentUserData.getUserAvatar();
    }
}