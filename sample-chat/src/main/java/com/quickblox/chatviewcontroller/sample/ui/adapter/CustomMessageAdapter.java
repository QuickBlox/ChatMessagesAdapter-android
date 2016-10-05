package com.quickblox.chatviewcontroller.sample.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chatdevelopmentkit.adapter.QBMessagesAdapter;
import com.quickblox.chatviewcontroller.sample.utils.Consts;

import java.util.Collection;
import java.util.List;


public class CustomMessageAdapter extends QBMessagesAdapter {
    private static final String TAG = CustomMessageAdapter.class.getSimpleName();
    private RequestListener glideRequestListener;
    private final String userOneAvatarUrl = "https://qbprod.s3.amazonaws.com/c91b4cdf084b4ecdb0989ce8b0e8c57900";
    private final String userTwoAvatarUrl = "https://qbprod.s3.amazonaws.com/0263a8c8840b440584311da82980351500";

    public CustomMessageAdapter(Context context, List<QBChatMessage> chatMessages) {
        super(context, chatMessages);
    }

    protected QBMessageViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateCustomViewHolder viewType= " + viewType);
        return new ImageAttachHolder(inflater.inflate(com.quickblox.chatdevelopmentkit.R.layout.list_item_attach_left, parent, false),
                com.quickblox.chatdevelopmentkit.R.id.msg_image_attach, com.quickblox.chatdevelopmentkit.R.id.msg_progressbar_attach);
    }

    @Override
    public void displayAttachment(QBMessageViewHolder holder, int position) {
        int preferredImageSizePreview = (int) (80 * Resources.getSystem().getDisplayMetrics().density);
        int valueType = getItemViewType(position);
        Log.d(TAG, "displayAttachment valueType= " + valueType);
        initGlideRequestListener((ImageAttachHolder) holder);

        QBChatMessage chatMessage = getItem(position);

        Collection<QBAttachment> attachments = chatMessage.getAttachments();
        QBAttachment attachment = attachments.iterator().next();
        Glide.with(context)
                .load(attachment.getUrl())
                .listener(glideRequestListener)
                .override(preferredImageSizePreview, preferredImageSizePreview)
                .dontTransform()
                .error(com.quickblox.chatdevelopmentkit.R.drawable.ic_error)
                .into(((ImageAttachHolder) holder).attachImageView);
    }

    private void initGlideRequestListener(final ImageAttachHolder holder) {
        glideRequestListener = new RequestListener() {

            @Override
            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                e.printStackTrace();
                holder.attachImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                holder.attachmentProgressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                holder.attachImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.attachmentProgressBar.setVisibility(View.GONE);
                return false;
            }
        };
    }


    @Override
    public void displayAvatarImage(String url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    @Override
    public String obtainAvatarUrl(int valueType, QBChatMessage chatMessage) {
        return chatMessage.getSenderId() == Consts.userOneID ? userOneAvatarUrl : userTwoAvatarUrl;
    }
}
