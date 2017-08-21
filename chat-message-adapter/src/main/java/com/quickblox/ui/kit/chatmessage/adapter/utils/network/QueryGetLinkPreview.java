package com.quickblox.ui.kit.chatmessage.adapter.utils.network;

import android.text.TextUtils;

import com.quickblox.core.RestMethod;
import com.quickblox.core.parser.QBJsonParser;
import com.quickblox.core.query.JsonQuery;
import com.quickblox.core.rest.RestRequest;
import com.quickblox.ui.kit.chatmessage.adapter.models.QBLinkPreview;

import java.util.Map;

public class QueryGetLinkPreview extends JsonQuery<QBLinkPreview> {
    private static final String OPEN_GRAPH_DOMAIN = "https://ogs.quickblox.com";
    private static final String URL = "url";
    private static final String TOKEN = "token";
    private final String link;
    private final String token;

    public QueryGetLinkPreview(String link, String token) {
        this.link = link;
        this.token = token;

        QBJsonParser<QBLinkPreview> parser = getParser();
        parser.setDeserializer(QBLinkPreview.class);
    }

    @Override
    public String getUrl() {
        return OPEN_GRAPH_DOMAIN;
    }

    @Override
    protected void setMethod(RestRequest request) {
        request.setMethod(RestMethod.GET);
    }

    @Override
    protected void setParams(RestRequest request) {
        super.setParams(request);
        Map<String, Object> parameters = request.getParameters();

        if (!TextUtils.isEmpty(link)) {
            parameters.put(URL, link);
        }

        if (!TextUtils.isEmpty(token)) {
            parameters.put(TOKEN, token);
        }
    }
}
