package com.quickblox.chatdevelopmentkit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.quickblox.chatdevelopmentkit.R;


public class MessageTextViewLeft extends MessageTextView {

    public MessageTextViewLeft(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void setLinearSide() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frameLinear.getLayoutParams();
        layoutParams.gravity = Gravity.LEFT;
        frameLinear.setLayoutParams(layoutParams);
    }

    @Override
    protected void setTextLayout() {
        viewTextStub.setLayoutResource(R.layout.widget_text_msg_left);
        layoutStub = (LinearLayout) viewTextStub.inflate();
    }
}