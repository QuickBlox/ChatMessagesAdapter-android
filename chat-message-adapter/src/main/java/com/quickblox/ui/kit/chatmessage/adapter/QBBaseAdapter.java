package com.quickblox.ui.kit.chatmessage.adapter;

import android.widget.ImageView;
import java.util.List;

public interface QBBaseAdapter<T> {

    T getItem(int position);

    int getItemViewType(int position);

    void add(T item);

    List<T> getList();

    void addList(List<T> items);

    void displayAvatarImage (String uri, ImageView imageView);

    String obtainAvatarUrl(int type, T chatMessage);

}