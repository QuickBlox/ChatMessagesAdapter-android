package com.quickblox.ui.kit.chatmessage.adapter.utils.network;

import com.quickblox.core.server.Performer;
import com.quickblox.ui.kit.chatmessage.adapter.models.QBLinkPreview;

public class QBRestRequestExecutor {

    public static Performer<QBLinkPreview> getLinkPreview(String url, String token) {
        return new QueryGetLinkPreview(url, token);
    }
}
