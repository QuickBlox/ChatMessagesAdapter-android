package com.quickblox.ui.kit.chatmessage.adapter.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.quickblox.ui.kit.chatmessage.adapter.R;


public abstract class MessageTextView extends FrameLayout {
    private static String TAG = MessageTextView.class.getSimpleName();
    protected LinearLayout frameLinear;
    protected ViewStub viewTextStub;

    LinearLayout layoutStub;
    LayoutInflater inflater;

    public MessageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(R.layout.widget_text_msg_frame);
        applyAttributes(attrs);
    }

    protected void init(@LayoutRes int layoutId) {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutId, this, true);
        frameLinear = (LinearLayout) getRootView().findViewById(R.id.msg_linear_frame);
        viewTextStub = (ViewStub) findViewById(R.id.msg_stub_message);
    }

    protected void applyAttributes(AttributeSet attrs) {
        TypedArray array = null;
        int widgetId;

        try {
            array = getContext().obtainStyledAttributes(attrs, R.styleable.MessageTextView);
            widgetId = array.getResourceId(R.styleable.MessageTextView_widget_id, 0);
        } finally {
            if (array != null) {
                array.recycle();
            }
        }

        setLinearSide();
        setTextLayout();
        setCustomWidget(widgetId);
    }

    protected void setCustomWidget(@LayoutRes int widgetId) {
        if (widgetId != 0) {
            final ViewGroup widgetFrameBottom = (ViewGroup) findViewById(R.id.msg_custom_widget_frame_bottom);
            final ViewGroup widgetFrameTop = (ViewGroup) findViewById(R.id.msg_custom_widget_frame_top);
            View customViewBottom = inflater.inflate(widgetId, widgetFrameBottom);
            View customViewTop = inflater.inflate(widgetId, widgetFrameTop);
            Log.d(TAG, "customViewBottom = null ? " + (customViewBottom == null));
            Log.d(TAG, "customViewBottom = null ? " + (customViewTop == null));
        }
    }

    abstract protected void setLinearSide();

    abstract protected void setTextLayout();
}
