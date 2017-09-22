package com.quickblox.ui.kit.chatmessage.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.ui.kit.chatmessage.adapter.listeners.QBChatAttachClickListener;
import com.quickblox.ui.kit.chatmessage.adapter.listeners.QBChatMessageLinkClickListener;
import com.quickblox.ui.kit.chatmessage.adapter.listeners.QBLinkPreviewClickListener;
import com.quickblox.ui.kit.chatmessage.adapter.listeners.QBMediaPlayerListener;
import com.quickblox.ui.kit.chatmessage.adapter.media.AudioController;
import com.quickblox.ui.kit.chatmessage.adapter.media.MediaController;
import com.quickblox.ui.kit.chatmessage.adapter.media.SingleMediaManager;
import com.quickblox.ui.kit.chatmessage.adapter.media.utils.Utils;
import com.quickblox.ui.kit.chatmessage.adapter.media.video.thumbnails.VideoThumbnail;
import com.quickblox.ui.kit.chatmessage.adapter.media.view.QBPlaybackControlView;
import com.quickblox.ui.kit.chatmessage.adapter.models.QBLinkPreview;
import com.quickblox.ui.kit.chatmessage.adapter.utils.AnimationsUtils;
import com.quickblox.ui.kit.chatmessage.adapter.utils.LinkUtils;
import com.quickblox.ui.kit.chatmessage.adapter.utils.LoadImagesUtils;
import com.quickblox.ui.kit.chatmessage.adapter.utils.LocationUtils;
import com.quickblox.ui.kit.chatmessage.adapter.utils.QBLinkPreviewCashService;
import com.quickblox.ui.kit.chatmessage.adapter.utils.QBMessageTextClickMovement;
import com.quickblox.users.model.QBUser;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

public class QBMessagesAdapter<T extends QBChatMessage> extends RecyclerView.Adapter<QBMessagesAdapter.QBMessageViewHolder> implements QBBaseAdapter<T> {
    private static final String TAG = QBMessagesAdapter.class.getSimpleName();

    protected static final long SHOW_VIEW_ANIMATION_DURATION = 500;

    protected static final int TYPE_TEXT_RIGHT = 1;
    protected static final int TYPE_TEXT_LEFT = 2;
    protected static final int TYPE_ATTACH_RIGHT = 3;
    protected static final int TYPE_ATTACH_LEFT = 4;
    protected static final int TYPE_ATTACH_RIGHT_AUDIO = 5;
    protected static final int TYPE_ATTACH_LEFT_AUDIO = 6;
    protected static final int TYPE_ATTACH_RIGHT_VIDEO = 7;
    protected static final int TYPE_ATTACH_LEFT_VIDEO = 8;

    //Message TextView click listener
    //
    private QBChatMessageLinkClickListener messageTextViewLinkClickListener;
    private boolean overrideOnClick;

    private QBChatAttachClickListener attachImageClickListener;
    private QBChatAttachClickListener attachLocationClickListener;
    private QBChatAttachClickListener attachAudioClickListener;
    private QBChatAttachClickListener attachVideoClickListener;

    private QBLinkPreviewClickListener linkPreviewClickListener;
    private boolean overrideOnLinkPreviewClick;


    private SparseIntArray containerLayoutRes = new SparseIntArray() {
        {
            put(TYPE_TEXT_RIGHT, R.layout.list_item_text_right);
            put(TYPE_TEXT_LEFT, R.layout.list_item_text_left);
            put(TYPE_ATTACH_RIGHT, R.layout.list_item_attach_right);
            put(TYPE_ATTACH_LEFT, R.layout.list_item_attach_left);
            put(TYPE_ATTACH_RIGHT_AUDIO, R.layout.list_item_attach_right_audio);
            put(TYPE_ATTACH_LEFT_AUDIO, R.layout.list_item_attach_left_audio);
            put(TYPE_ATTACH_RIGHT_VIDEO, R.layout.list_item_attach_right_video);
            put(TYPE_ATTACH_LEFT_VIDEO, R.layout.list_item_attach_left_video);
        }
    };

    protected QBMessageViewHolder qbViewHolder;

    protected List<T> chatMessages;
    protected LayoutInflater inflater;
    protected Context context;

    private SingleMediaManager mediaManager;
    private AudioController audioController;
    private MediaControllerEventListener mediaControllerEventListener;
    private Map<QBPlaybackControlView, Integer> playerViewHashMap;
    private int activePlayerViewPosition = -1;


    public QBMessagesAdapter(Context context, List<T> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
        this.inflater = LayoutInflater.from(context);
    }

    public QBChatMessageLinkClickListener getMessageTextViewLinkClickListener() {
        return messageTextViewLinkClickListener;
    }

    /**
     * Sets listener for handling pressed links on message text.
     *
     * @param textViewLinkClickListener listener to set. Must to implement {@link QBChatMessageLinkClickListener}
     * @param overrideOnClick           set 'true' if have to himself manage onLinkClick event or set 'false' for delegate
     *                                  onLinkClick event to {@link android.text.util.Linkify}
     */
    public void setMessageTextViewLinkClickListener(QBChatMessageLinkClickListener textViewLinkClickListener, boolean overrideOnClick) {
        this.messageTextViewLinkClickListener = textViewLinkClickListener;
        this.overrideOnClick = overrideOnClick;
    }

    public void setAttachImageClickListener(QBChatAttachClickListener clickListener) {
        attachImageClickListener = clickListener;
    }

    public void setAttachLocationClickListener(QBChatAttachClickListener clickListener) {
        attachLocationClickListener = clickListener;
    }

    public void setLinkPreviewClickListener(QBLinkPreviewClickListener linkPreviewClickListener, boolean overrideOnLinkpreviewClick) {
        this.linkPreviewClickListener = linkPreviewClickListener;
        this.overrideOnLinkPreviewClick = overrideOnLinkpreviewClick;
    }

    public void setAttachAudioClickListener(QBChatAttachClickListener clickListener) {
        this.attachAudioClickListener = clickListener;
    }

    public void setAttachVideoClickListener(QBChatAttachClickListener clickListener) {
        this.attachVideoClickListener = clickListener;
    }

    public void removeAttachImageClickListener(QBChatAttachClickListener clickListener) {
        attachImageClickListener = null;
    }

    public void removeLocationImageClickListener(QBChatAttachClickListener clickListener) {
        attachLocationClickListener = null;
    }

    public void removeAttachAudioClickListener(QBChatAttachClickListener clickListener) {
        attachAudioClickListener = null;
    }

    public void removeAttachVideoClickListener(QBChatAttachClickListener clickListener) {
        attachVideoClickListener = null;
    }

    /**
     * Removes listener for handling onLinkClick event on message text.
     */
    public void removeMessageTextViewLinkClickListener() {
        this.messageTextViewLinkClickListener = null;
        this.overrideOnClick = false;
    }

    public void setMediaPlayerListener(QBMediaPlayerListener mediaPlayerListener) {
        getMediaManagerInstance().addListener(mediaPlayerListener);
    }

    public void removeMediaPlayerListener(QBMediaPlayerListener listener) {
        getMediaManagerInstance().removeListener(listener);
    }

    public void removeLinkPreviewClickListener() {
        this.linkPreviewClickListener = null;
        this.overrideOnLinkPreviewClick = false;
    }

    @Override
    public QBMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_TEXT_RIGHT:
                qbViewHolder = new TextMessageHolder(inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_text_message,
                        R.id.msg_text_time_message, R.id.msg_link_preview);
                return qbViewHolder;
            case TYPE_TEXT_LEFT:
                qbViewHolder = new TextMessageHolder(inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_text_message,
                        R.id.msg_text_time_message, R.id.msg_link_preview);
                return qbViewHolder;
            case TYPE_ATTACH_RIGHT:
                qbViewHolder = new ImageAttachHolder(inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_image_attach, R.id.msg_progressbar_attach,
                        R.id.msg_text_time_attach, R.id.msg_signs_attach);
                return qbViewHolder;
            case TYPE_ATTACH_LEFT:
                qbViewHolder = new ImageAttachHolder(inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_image_attach, R.id.msg_progressbar_attach,
                        R.id.msg_text_time_attach, R.id.msg_signs_attach);
                return qbViewHolder;
            case TYPE_ATTACH_RIGHT_AUDIO:
                qbViewHolder = new AudioAttachHolder(inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_audio_attach, R.id.msg_attach_duration,
                        R.id.msg_text_time_attach, R.id.msg_signs_attach);
                return qbViewHolder;
            case TYPE_ATTACH_LEFT_AUDIO:
                qbViewHolder = new AudioAttachHolder(inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_audio_attach, R.id.msg_attach_duration,
                        R.id.msg_text_time_attach, R.id.msg_signs_attach);
                return qbViewHolder;
            case TYPE_ATTACH_RIGHT_VIDEO:
                qbViewHolder = new VideoAttachHolder(inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_video_attach, R.id.msg_progressbar_attach,
                        R.id.msg_attach_duration, R.id.msg_text_time_attach, R.id.msg_signs_attach, R.id.msg_video_play_icon);
                return qbViewHolder;
            case TYPE_ATTACH_LEFT_VIDEO:
                qbViewHolder = new VideoAttachHolder(inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_video_attach, R.id.msg_progressbar_attach,
                        R.id.msg_attach_duration, R.id.msg_text_time_attach, R.id.msg_signs_attach, R.id.msg_video_play_icon);
                return qbViewHolder;

            default:
                Log.d(TAG, "onCreateViewHolder case default");
                // resource must be set manually by creating custom adapter
                return onCreateCustomViewHolder(parent, viewType);
        }
    }

    @Override
    public void onViewRecycled(QBMessageViewHolder holder) {
        if (holder.getItemViewType() == TYPE_TEXT_LEFT || holder.getItemViewType() == TYPE_TEXT_RIGHT) {
            TextMessageHolder textMessageHolder = (TextMessageHolder) holder;

            if (textMessageHolder.linkPreviewLayout.getTag() != null) {
                textMessageHolder.linkPreviewLayout.setTag(null);
            }
        }

        //abort loading avatar before setting new avatar to view
        if (containerLayoutRes.get(holder.getItemViewType()) != 0) {
            Glide.clear(holder.avatar);
        }

        super.onViewRecycled(holder);
    }

    protected QBMessageViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        Log.e(TAG, "You must create ViewHolder by your own");
        return null;
    }

    protected void setMsgLayoutResourceByType(int typeLayout, @LayoutRes int messageLayoutResource) {
        containerLayoutRes.put(typeLayout, messageLayoutResource);
    }

    @Override
    public void onBindViewHolder(QBMessageViewHolder holder, int position) {
        T chatMessage = getItem(position);
        int valueType = getItemViewType(position);
        switch (valueType) {
            case TYPE_TEXT_RIGHT:
                onBindViewMsgRightHolder((TextMessageHolder) holder, chatMessage, position);
                break;
            case TYPE_TEXT_LEFT:
                onBindViewMsgLeftHolder((TextMessageHolder) holder, chatMessage, position);
                break;
            case TYPE_ATTACH_RIGHT:
                Log.i(TAG, "onBindViewHolder TYPE_ATTACH_RIGHT");
                onBindViewAttachRightHolder((ImageAttachHolder) holder, chatMessage, position);
                break;
            case TYPE_ATTACH_LEFT:
                Log.i(TAG, "onBindViewHolder TYPE_ATTACH_LEFT");
                onBindViewAttachLeftHolder((ImageAttachHolder) holder, chatMessage, position);
                break;
            case TYPE_ATTACH_RIGHT_AUDIO:
                Log.i(TAG, "onBindViewHolder TYPE_ATTACH_RIGHT_AUDIO");
                onBindViewAttachRightAudioHolder((AudioAttachHolder) holder, chatMessage, position);
                break;
            case TYPE_ATTACH_LEFT_AUDIO:
                Log.i(TAG, "onBindViewHolder TYPE_ATTACH_LEFT_AUDIO");
                onBindViewAttachLeftAudioHolder((AudioAttachHolder) holder, chatMessage, position);
                break;
            case TYPE_ATTACH_RIGHT_VIDEO:
                Log.i(TAG, "onBindViewHolder TYPE_ATTACH_RIGHT_VIDEO");
                onBindViewAttachRightVideoHolder((VideoAttachHolder) holder, chatMessage, position);
                break;
            case TYPE_ATTACH_LEFT_VIDEO:
                Log.i(TAG, "onBindViewHolder TYPE_ATTACH_LEFT_VIDEO");
                onBindViewAttachLeftVideoHolder((VideoAttachHolder) holder, chatMessage, position);
                break;
            default:
                onBindViewCustomHolder(holder, chatMessage, position);
                Log.i(TAG, "onBindViewHolder TYPE_ATTACHMENT_CUSTOM");
                break;
        }
    }

    protected void onBindViewCustomHolder(QBMessageViewHolder holder, T chatMessage, int position) {
    }

    protected void onBindViewAttachRightHolder(ImageAttachHolder holder, T chatMessage, int position) {
        setDateSentAttach(holder, chatMessage);
        displayAttachment(holder, position);

        int valueType = getItemViewType(position);
        String avatarUrl = obtainAvatarUrl(valueType, chatMessage);
        if (avatarUrl != null) {
            displayAvatarImage(avatarUrl, holder.avatar);
        }

        setItemAttachClickListener(getAttachListenerByType(position), holder, getQBAttach(position), position);
    }

    protected void onBindViewAttachLeftHolder(ImageAttachHolder holder, T chatMessage, int position) {
        setDateSentAttach(holder, chatMessage);
        displayAttachment(holder, position);

        int valueType = getItemViewType(position);
        String avatarUrl = obtainAvatarUrl(valueType, chatMessage);
        if (avatarUrl != null) {
            displayAvatarImage(avatarUrl, holder.avatar);
        }

        setItemAttachClickListener(getAttachListenerByType(position), holder, getQBAttach(position), position);
    }

    protected void onBindViewAttachRightAudioHolder(AudioAttachHolder holder, T chatMessage, int position) {
        setDateSentAttach(holder, chatMessage);
        displayAttachmentAudio(holder, position);

        int valueType = getItemViewType(position);
        String avatarUrl = obtainAvatarUrl(valueType, chatMessage);
        if (avatarUrl != null) {
            displayAvatarImage(avatarUrl, holder.avatar);
        }
    }


    protected void onBindViewAttachLeftAudioHolder(AudioAttachHolder holder, T chatMessage, int position) {
        setDateSentAttach(holder, chatMessage);
        displayAttachmentAudio(holder, position);

        int valueType = getItemViewType(position);
        String avatarUrl = obtainAvatarUrl(valueType, chatMessage);
        if (avatarUrl != null) {
            displayAvatarImage(avatarUrl, holder.avatar);
        }
    }

    protected void onBindViewAttachRightVideoHolder(VideoAttachHolder holder, T chatMessage, int position) {
        setDateSentAttach(holder, chatMessage);
        displayAttachmentVideo(holder, position);

        int valueType = getItemViewType(position);
        String avatarUrl = obtainAvatarUrl(valueType, chatMessage);
        if (avatarUrl != null) {
            displayAvatarImage(avatarUrl, holder.avatar);
        }

        setItemAttachClickListener(getAttachListenerByType(position), holder, getQBAttach(position), position);
    }

    protected void onBindViewAttachLeftVideoHolder(VideoAttachHolder holder, T chatMessage, int position) {
        setDateSentAttach(holder, chatMessage);
        displayAttachmentVideo(holder, position);

        int valueType = getItemViewType(position);
        String avatarUrl = obtainAvatarUrl(valueType, chatMessage);
        if (avatarUrl != null) {
            displayAvatarImage(avatarUrl, holder.avatar);
        }

        setItemAttachClickListener(getAttachListenerByType(position), holder, getQBAttach(position), position);
    }

    protected void onBindViewMsgLeftHolder(TextMessageHolder holder, T chatMessage, int position) {
        fillTextMessageHolder(holder, chatMessage, position, true);
    }

    protected void onBindViewMsgRightHolder(TextMessageHolder holder, T chatMessage, int position) {
        fillTextMessageHolder(holder, chatMessage, position, false);
    }

    protected void fillTextMessageHolder(TextMessageHolder holder, T chatMessage, int position, boolean isLeftMessage) {
        holder.linkPreviewLayout.setVisibility(View.GONE);
        holder.messageTextView.setText(chatMessage.getBody());
        holder.timeTextMessageTextView.setText(getDate(chatMessage.getDateSent()));

        setMessageTextViewLinkClickListener(holder, position);

        int valueType = getItemViewType(position);
        String avatarUrl = obtainAvatarUrl(valueType, chatMessage);
        if (avatarUrl != null) {
            displayAvatarImage(avatarUrl, holder.avatar);
        }

        final List<String> urlsList = LinkUtils.extractUrls(chatMessage.getBody());
        if (!urlsList.isEmpty()) {
            holder.messageTextView.setMaxWidth((int) context.getResources().getDimension(R.dimen.link_preview_width));
            holder.linkPreviewLayout.setTag(chatMessage.getId());

            if (isLeftMessage) {
                processLinksFromLeftMessage(holder, urlsList, position);
            } else {
                processLinksFromRightMessage(holder, urlsList, position);
            }
        } else {
            holder.messageTextView.setMaxWidth(context.getResources().getDisplayMetrics().widthPixels);
        }
    }

    protected void processLinksFromLeftMessage(TextMessageHolder holder, List<String> urlsList, int position) {
        processLinksFromMessage(holder, urlsList, position);
    }

    protected void processLinksFromRightMessage(TextMessageHolder holder, List<String> urlsList, int position) {
        processLinksFromMessage(holder, urlsList, position);
    }

    protected void processLinksFromMessage(TextMessageHolder holder, final List<String> urlsList, final int position) {
        final String firstLink = LinkUtils.getLinkWithProtocol(urlsList.get(0));
        String linkPreviewViewIdentifier = (String) holder.linkPreviewLayout.getTag();

        QBLinkPreviewCashService.getInstance().getLinkPreview(firstLink,
                null,
                false,
                new LoadLinkPreviewHandler(holder, urlsList, position, linkPreviewViewIdentifier));
    }

    protected void fillLinkPreviewLayout(final View linkPreviewLayout, final QBLinkPreview linkPreview, String link) {
        TextView linkTitle = (TextView) linkPreviewLayout.findViewById(R.id.link_preview_title);
        TextView linkDescription = (TextView) linkPreviewLayout.findViewById(R.id.link_preview_description);
        ImageView linkImage = (ImageView) linkPreviewLayout.findViewById(R.id.link_preview_image);
        ImageView linkHostIcon = (ImageView) linkPreviewLayout.findViewById(R.id.link_host_icon);
        TextView linkHost = (TextView) linkPreviewLayout.findViewById(R.id.link_host_url);

        linkTitle.setText(linkPreview.getTitle());

        if (!TextUtils.isEmpty(linkPreview.getDescription())) {
            linkDescription.setText(linkPreview.getDescription());
            linkDescription.setVisibility(View.VISIBLE);
        } else {
            linkDescription.setVisibility(View.GONE);
        }

        linkHost.setText(LinkUtils.getHostFromLink(link));

        linkImage.setVisibility(View.GONE);
        if (linkPreview.getImage() != null && linkPreview.getImage().getImageUrl() != null && LoadImagesUtils.isPossibleToDisplayImage(context)) {
            loadImageOrHideView(LinkUtils.prepareCorrectLink(linkPreview.getImage().getImageUrl()), linkImage);
        }

        linkHostIcon.setVisibility(View.GONE);
        if (LinkUtils.getLinkForHostIcon(link) != null && LoadImagesUtils.isPossibleToDisplayImage(context)) {
            loadImageOrHideView(LinkUtils.getLinkForHostIcon(link), linkHostIcon);
        }
    }

    protected void loadImageOrHideView(final String imageUrl, final ImageView imageView) {
        int preferredImageWidth = (int) context.getResources().getDimension(R.dimen.attach_image_width_preview);
        int preferredImageHeight = (int) context.getResources().getDimension(R.dimen.attach_image_height_preview);

        Glide.with(context)
                .load(imageUrl)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        AnimationsUtils.showView(imageView, SHOW_VIEW_ANIMATION_DURATION);
                        return false;
                    }
                })
                .override(preferredImageWidth, preferredImageHeight)
                .dontTransform()
                .into(imageView);
    }

    private void setMessageTextViewLinkClickListener(TextMessageHolder holder, int position) {
        if (messageTextViewLinkClickListener != null) {
            QBMessageTextClickMovement customClickMovement =
                    new QBMessageTextClickMovement(messageTextViewLinkClickListener, overrideOnClick, context);
            customClickMovement.setPositionInAdapter(position);

            holder.messageTextView.setMovementMethod(customClickMovement);
        }
    }

    private QBChatAttachClickListener getAttachListenerByType(int position) {
        QBAttachment attachment = getQBAttach(position);

        if (QBAttachment.PHOTO_TYPE.equalsIgnoreCase(attachment.getType()) ||
                QBAttachment.IMAGE_TYPE.equalsIgnoreCase(attachment.getType())) {
            return attachImageClickListener;
        } else if (QBAttachment.LOCATION_TYPE.equalsIgnoreCase(attachment.getType())) {
            return attachLocationClickListener;
        } else if (QBAttachment.AUDIO_TYPE.equalsIgnoreCase(attachment.getType())) {
            return attachAudioClickListener;
        } else if (QBAttachment.VIDEO_TYPE.equalsIgnoreCase(attachment.getType())) {
            return attachVideoClickListener;
        }
        return null;
    }

    protected void setDateSentAttach(BaseAttachHolder holder, T chatMessage) {
        holder.attachTextTime.setText(getDate(chatMessage.getDateSent()));
    }

    /**
     * ObtainAvatarUrl must be implemented in derived class
     *
     * @return String avatar url
     */
    @Nullable
    public String obtainAvatarUrl(int valueType, T chatMessage) {
        return null;
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public T getItem(int position) {
        return chatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        T chatMessage = getItem(position);

        if (hasAttachments(chatMessage)) {
            QBAttachment attachment = getQBAttach(position);

            if (QBAttachment.PHOTO_TYPE.equalsIgnoreCase(attachment.getType()) ||
                    QBAttachment.IMAGE_TYPE.equalsIgnoreCase(attachment.getType())) {
                return isIncoming(chatMessage) ? TYPE_ATTACH_LEFT : TYPE_ATTACH_RIGHT;
            } else if (QBAttachment.LOCATION_TYPE.equalsIgnoreCase(attachment.getType())) {
                return getLocationView(chatMessage);
            } else if (QBAttachment.AUDIO_TYPE.equalsIgnoreCase(attachment.getType())) {
                return isIncoming(chatMessage) ? TYPE_ATTACH_LEFT_AUDIO : TYPE_ATTACH_RIGHT_AUDIO;
            } else if (QBAttachment.VIDEO_TYPE.equalsIgnoreCase(attachment.getType())) {
                return isIncoming(chatMessage) ? TYPE_ATTACH_LEFT_VIDEO : TYPE_ATTACH_RIGHT_VIDEO;
            }

        } else {
            return isIncoming(chatMessage) ? TYPE_TEXT_LEFT : TYPE_TEXT_RIGHT;
        }
        return customViewType(position);
    }

    protected int getLocationView(T chatMessage) {
        return isIncoming(chatMessage) ? TYPE_ATTACH_LEFT : TYPE_ATTACH_RIGHT;
    }

    protected int customViewType(int position) {
        return -1;
    }

    @Override
    public void add(T item) {
        chatMessages.add(item);
        notifyDataSetChanged();
    }

    @Override
    public List<T> getList() {
        return chatMessages;
    }

    @Override
    public void addList(List<T> items) {
        chatMessages.clear();
        chatMessages.addAll(items);
        notifyDataSetChanged();
    }

    protected boolean isIncoming(T chatMessage) {
        QBUser currentUser = QBChatService.getInstance().getUser();
        return chatMessage.getSenderId() != null && !chatMessage.getSenderId().equals(currentUser.getId());
    }

    protected boolean hasAttachments(T chatMessage) {
        Collection<QBAttachment> attachments = chatMessage.getAttachments();
        return attachments != null && !attachments.isEmpty();
    }

    /**
     * @return string in "Hours:Minutes" format, i.e. <b>10:15</b>
     */
    protected String getDate(long seconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(new Date(seconds * 1000));
    }

    /**
     * displayAttachment must be implemented in derived class
     */
    protected void displayAttachment(QBMessageViewHolder holder, int position) {
        QBAttachment attachment = getQBAttach(position);

        if (QBAttachment.PHOTO_TYPE.equalsIgnoreCase(attachment.getType()) ||
                QBAttachment.IMAGE_TYPE.equalsIgnoreCase(attachment.getType())) {
            showPhotoAttach(holder, position);
        } else if (QBAttachment.LOCATION_TYPE.equalsIgnoreCase(attachment.getType())) {
            showLocationAttach(holder, position);
        }
    }

    protected void displayAttachmentAudio(QBMessageViewHolder holder, int position) {
        QBAttachment attachment = getQBAttach(position);

        Uri uri = getUriFromAttach(attachment);
        int duration = getDurationFromAttach(attachment, position);
        setDurationAudio(duration, holder);
        QBPlaybackControlView playerView = ((AudioAttachHolder) holder).playerView;

        showAudioView(playerView, uri, position);
        setItemAttachAudioClickListener(getAttachListenerByType(position), holder, getQBAttach(position), position, playerView);
    }


    protected void displayAttachmentVideo(QBMessageViewHolder holder, int position) {
        QBAttachment attachment = getQBAttach(position);
        int duration = getDurationFromAttach(attachment, position);
        setDurationVideo(duration, holder);
        String url = getVideoUrl(position);
        showVideoThumbnail(holder, url, position);
    }

    protected void setDurationVideo(int duration, QBMessageViewHolder holder) {
        ((VideoAttachHolder) holder).durationView.setText(Utils.formatTimeSecondsToMinutes(duration));
    }


    protected Uri getUriFromAttach(QBAttachment attachment) {
        return Utils.getUriFromAttachPublicUrl(attachment);
    }

    protected int getDurationFromAttach(QBAttachment attachment, int position) {
        return attachment.getDuration();
    }

    protected void setDurationAudio(int duration, QBMessageViewHolder holder) {
        ((AudioAttachHolder) holder).durationView.setText(Utils.formatTimeSecondsToMinutes(duration));
    }

    private void showAudioView(QBPlaybackControlView playerView, Uri uri, int position) {
        initPlayerView(playerView, uri, position);
        if (isCurrentViewActive(position)) {
            Log.d(TAG, "showAudioView isCurrentViewActive");
            playerView.restoreState(getMediaManagerInstance().getExoPlayer());
        }
    }

    private void initPlayerView(QBPlaybackControlView playerView, Uri uri, int position) {
        playerView.releaseView();
        setViewPosition(playerView, position);
        playerView.initView(getAudioControllerInstance(), uri);
    }

    private boolean isCurrentViewActive(int position) {
        return activePlayerViewPosition == position;
    }

    private void setPlayerViewActivePosition(int activeViewPosition) {
        this.activePlayerViewPosition = activeViewPosition;
    }

    private void setViewPosition(QBPlaybackControlView view, int position) {
        if (playerViewHashMap == null) {
            playerViewHashMap = new WeakHashMap<>();
        }
        playerViewHashMap.put(view, position);
    }

    private int getPlayerViewPosition(QBPlaybackControlView view) {
        Integer position = playerViewHashMap.get(view);
        return position == null ? activePlayerViewPosition : position;
    }

    private void showVideoThumbnail(final QBMessageViewHolder holder, String url, int position) {

        int preferredImageWidth = (int) context.getResources().getDimension(R.dimen.attach_image_width_preview);
        int preferredImageHeight = (int) context.getResources().getDimension(R.dimen.attach_image_height_preview);

        VideoThumbnail model = new VideoThumbnail(url);
        Glide.with(context)
                .load(model)
                .listener(this.<VideoThumbnail, GlideDrawable>getVideoThumbnailRequestListener(holder, position))
                .override(preferredImageWidth, preferredImageHeight)
                .dontTransform()
                .error(R.drawable.ic_error)
                .into(((VideoAttachHolder) holder).attachImageView);
    }

    public SingleMediaManager getMediaManagerInstance() {
        return mediaManager = mediaManager == null ? new SingleMediaManager(context) : mediaManager;
    }

    private AudioController getAudioControllerInstance() {
        return audioController = audioController == null ? new AudioController(getMediaManagerInstance(), getMediaControllerEventListenerInstance())
                : audioController;
    }

    private MediaControllerEventListener getMediaControllerEventListenerInstance() {
        return mediaControllerEventListener = mediaControllerEventListener == null ? new MediaControllerEventListener() : mediaControllerEventListener;
    }


    protected void showPhotoAttach(QBMessageViewHolder holder, int position) {
        String imageUrl = getImageUrl(position);
        showImageByURL(holder, imageUrl, position);
    }

    protected void showLocationAttach(QBMessageViewHolder holder, int position) {
        String locationUrl = getLocationUrl(position);
        showImageByURL(holder, locationUrl, position);
    }

    public String getImageUrl(int position) {
        QBAttachment attachment = getQBAttach(position);
        return QBFile.getPrivateUrlForUID(attachment.getId());
    }

    public String getVideoUrl(int position) {
        QBAttachment attachment = getQBAttach(position);
        return QBFile.getPrivateUrlForUID(attachment.getId());
    }

    public String getLocationUrl(int position) {
        QBAttachment attachment = getQBAttach(position);

        LocationUtils.BuilderParams params = LocationUtils.defaultUrlLocationParams(context);

        return LocationUtils.getRemoteUri(attachment.getData(), params);
    }

    protected QBAttachment getQBAttach(int position) {
        T chatMessage = getItem(position);
        return chatMessage.getAttachments().iterator().next();
    }

    private void showImageByURL(QBMessageViewHolder holder, String url, int position) {
        int preferredImageWidth = (int) context.getResources().getDimension(R.dimen.attach_image_width_preview);
        int preferredImageHeight = (int) context.getResources().getDimension(R.dimen.attach_image_height_preview);

        Glide.with(context)
                .load(url)
                .listener(this.<String, GlideDrawable>getRequestListener(holder, position))
                .override(preferredImageWidth, preferredImageHeight)
                .dontTransform()
                .error(R.drawable.ic_error)
                .into(((BaseImageAttachHolder) holder).attachImageView);
    }


    protected RequestListener getRequestListener(QBMessageViewHolder holder, int position) {
        return new ImageLoadListener<>((BaseImageAttachHolder) holder);
    }

    protected RequestListener getVideoThumbnailRequestListener(QBMessageViewHolder holder, int position) {
        return new VideoThumbnailLoadListener((VideoAttachHolder) holder);
    }

    /**
     * displayAvatarImage must be implemented in derived class
     */
    @Override
    public void displayAvatarImage(String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.placeholder_user)
                .dontAnimate()
                .into(imageView);
    }

    protected void setItemAttachClickListener(QBChatAttachClickListener listener, QBMessageViewHolder holder, QBAttachment qbAttachment, int position) {
        if (listener != null) {
            holder.bubbleFrame.setOnClickListener(new QBItemClickListenerFilter(listener, qbAttachment, position));
        }
    }

    protected void setItemAttachAudioClickListener(QBChatAttachClickListener listener, QBMessageViewHolder holder, QBAttachment qbAttachment, int position,
                                                   QBPlaybackControlView controlView) {
        holder.bubbleFrame.setOnClickListener(new QBItemAudioClickListener(listener, qbAttachment, position, controlView));
    }


    protected static class TextMessageHolder extends QBMessageViewHolder {
        public View linkPreviewLayout;
        public TextView messageTextView;
        public TextView timeTextMessageTextView;

        public TextMessageHolder(View itemView, @IdRes int msgId, @IdRes int timeId, @IdRes int linkPreviewLayoutId) {
            super(itemView);
            messageTextView = (TextView) itemView.findViewById(msgId);
            timeTextMessageTextView = (TextView) itemView.findViewById(timeId);
            linkPreviewLayout = itemView.findViewById(linkPreviewLayoutId);
        }
    }

    protected static class BaseAttachHolder extends QBMessageViewHolder {
        public TextView attachTextTime;
        public ImageView signAttachView;

        public BaseAttachHolder(View itemView, @IdRes int timeId, @IdRes int signViewId) {
            super(itemView);
            attachTextTime = (TextView) itemView.findViewById(timeId);
            signAttachView = (ImageView) itemView.findViewById(signViewId);
        }
    }

    protected static class BaseImageAttachHolder extends BaseAttachHolder {
        public ImageView attachImageView;
        public ProgressBar attachmentProgressBar;

        public BaseImageAttachHolder(View itemView, @IdRes int attachId, @IdRes int progressBarId, @IdRes int timeId, @IdRes int signId) {
            super(itemView, timeId, signId);
            attachImageView = (ImageView) itemView.findViewById(attachId);
            attachmentProgressBar = (ProgressBar) itemView.findViewById(progressBarId);
        }
    }

    protected static class ImageAttachHolder extends BaseImageAttachHolder {

        public ImageAttachHolder(View itemView, @IdRes int attachId, @IdRes int progressBarId, @IdRes int timeId, @IdRes int signId) {
            super(itemView, attachId, progressBarId, timeId, signId);
        }
    }

    public static class AudioAttachHolder extends BaseAttachHolder {
        public QBPlaybackControlView playerView;
        public TextView durationView;

        public AudioAttachHolder(View itemView, @IdRes int attachId, @IdRes int durationId, @IdRes int timeId, @IdRes int signId) {
            super(itemView, timeId, signId);
            playerView = (QBPlaybackControlView) itemView.findViewById(attachId);
            durationView = (TextView) itemView.findViewById(durationId);
        }
    }

    protected static class VideoAttachHolder extends BaseImageAttachHolder {
        public ImageView playIcon;
        public TextView durationView;

        public VideoAttachHolder(View itemView, @IdRes int attachId, @IdRes int progressBarId, @IdRes int durationId, @IdRes int timeId, @IdRes int signId, @IdRes int playIconId) {
            super(itemView, attachId, progressBarId, timeId, signId);
            playIcon = (ImageView) itemView.findViewById(playIconId);
            durationView = (TextView) itemView.findViewById(durationId);
        }
    }

    protected abstract static class QBMessageViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public View bubbleFrame;

        public QBMessageViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.msg_image_avatar);
            bubbleFrame = itemView.findViewById(R.id.msg_bubble_background);
        }
    }

    protected static class ImageLoadListener<M, P> implements RequestListener<M, P> {
        private BaseImageAttachHolder holder;

        protected ImageLoadListener(BaseImageAttachHolder holder) {
            this.holder = holder;
            holder.attachmentProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean onException(Exception e, M model, Target<P> target, boolean isFirstResource) {
            Log.e(TAG, "ImageLoadListener Exception= " + e);
            holder.attachImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.attachmentProgressBar.setVisibility(View.GONE);
            return false;
        }

        @Override
        public boolean onResourceReady(P resource, M model, Target<P> target, boolean isFromMemoryCache, boolean isFirstResource) {
            holder.attachImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.attachmentProgressBar.setVisibility(View.GONE);
            return false;
        }
    }

    protected static class VideoThumbnailLoadListener extends ImageLoadListener<VideoThumbnail, GlideDrawable> {
        private VideoAttachHolder holder;

        protected VideoThumbnailLoadListener(VideoAttachHolder holder) {
            super(holder);
            this.holder = holder;
            holder.playIcon.setVisibility(View.INVISIBLE);
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, VideoThumbnail model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            super.onResourceReady(resource, model, target, isFromMemoryCache, isFirstResource);
            holder.playIcon.setVisibility(View.VISIBLE);
            return false;
        }
    }

    private class QBItemAudioClickListener extends QBItemClickListenerFilter {
        private QBPlaybackControlView controlView;

        QBItemAudioClickListener(QBChatAttachClickListener chatAttachClickListener, QBAttachment attachment, int position, QBPlaybackControlView controlView) {
            super(chatAttachClickListener, attachment, position);
            this.controlView = controlView;
        }

        @Override
        public void onClick(View view) {
            if (chatAttachClickListener != null) {
                super.onClick(view);
            }
            controlView.clickIconPlayPauseView();
        }
    }

    private class QBItemClickListenerFilter implements View.OnClickListener {
        protected int position;
        protected QBAttachment attachment;
        protected QBChatAttachClickListener chatAttachClickListener;

        QBItemClickListenerFilter(QBChatAttachClickListener qbChatAttachClickListener, QBAttachment attachment, int position) {
            this.position = position;
            this.attachment = attachment;
            this.chatAttachClickListener = qbChatAttachClickListener;
        }

        @Override
        public void onClick(View view) {
            chatAttachClickListener.onLinkClicked(attachment, position);
        }
    }

    private class MediaControllerEventListener implements MediaController.EventMediaController {

        @Override
        public void onPlayerInViewInit(QBPlaybackControlView view) {
            setPlayerViewActivePosition(getPlayerViewPosition(view));
        }
    }

    private class QBLinkPreviewClickListenerFilter implements View.OnClickListener, View.OnLongClickListener {
        private final QBLinkPreviewClickListener linkPreviewClickListener;
        private final QBLinkPreview linkPreview;
        private final int position;
        private final String link;

        public QBLinkPreviewClickListenerFilter(QBLinkPreviewClickListener linkPreviewClickListener, String link, QBLinkPreview linkPreview, int position) {
            this.linkPreviewClickListener = linkPreviewClickListener;
            this.link = link;
            this.linkPreview = linkPreview;
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (linkPreviewClickListener != null) {
                linkPreviewClickListener.onLinkPreviewClicked(link, linkPreview, position);
            }

            if (!overrideOnLinkPreviewClick) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (linkPreviewClickListener != null) {
                linkPreviewClickListener.onLinkPreviewLongClicked(link, linkPreview, position);
            }

            return true;
        }
    }

    private class LoadLinkPreviewHandler implements QBEntityCallback<QBLinkPreview> {
        private TextMessageHolder holder;
        private int position;
        private String firstLink;
        private List<String> urlsList;
        private final String viewIdentifier;

        public LoadLinkPreviewHandler(TextMessageHolder holder, List<String> urlsList, int position, String viewIdentifier) {
            this.holder = holder;
            this.urlsList = urlsList;
            this.position = position;
            this.firstLink = LinkUtils.getLinkWithProtocol(urlsList.get(0));
            this.viewIdentifier = viewIdentifier;
        }

        @Override
        public void onSuccess(QBLinkPreview linkPreview, Bundle bundle) {
            if (linkPreview != null
                    && holder.linkPreviewLayout.getTag() != null
                    && holder.linkPreviewLayout.getTag().equals(viewIdentifier)) {
                holder.linkPreviewLayout.setOnClickListener(new QBLinkPreviewClickListenerFilter(linkPreviewClickListener, firstLink, linkPreview, position));
                holder.linkPreviewLayout.setOnLongClickListener(new QBLinkPreviewClickListenerFilter(linkPreviewClickListener, firstLink, linkPreview, position));

                fillLinkPreviewLayout(holder.linkPreviewLayout, linkPreview, firstLink);

                AnimationsUtils.showView(holder.linkPreviewLayout, SHOW_VIEW_ANIMATION_DURATION);
            }
        }

        @Override
        public void onError(QBResponseException e) {
            //ignore error
        }
    }
}