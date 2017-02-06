package com.quickblox.ui.kit.chatmessage.adapter.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by roman on 1/31/17.
 */

public class JsonParserBase {


    public static String serialize(final Map<String, String> map) {

        Gson gson = new GsonBuilder().create();
        return gson.toJson(map);
    }

    public static Map<String, Object> deserialize(String jsonString) {

        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, Object> jsonMap = new Gson().fromJson(jsonString, type);

        return jsonMap;
    }

}
