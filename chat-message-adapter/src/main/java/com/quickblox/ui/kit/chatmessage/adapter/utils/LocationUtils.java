package com.quickblox.ui.kit.chatmessage.adapter.utils;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.quickblox.ui.kit.chatmessage.adapter.R;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Roman on 14.01.2017.
 */

public class LocationUtils {

    private static String generateURI(double latitude, double longitude, Context context) {
        String URI_SCHEME_MAP = context.getString(R.string.uri_scheme_map);
        String ZOOM = context.getString(R.string.map_zoom);
        String SIZE = context.getString(R.string.map_zize);
        String MAPTYPE = context.getString(R.string.map_type);
        String COLOR = context.getString(R.string.map_color);

        //api static map key should be generated in developers google console
        String KEY = Resources.getSystem().getString(R.string.google_maps_key);

        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter("zoom", ZOOM)
                .appendQueryParameter("size", SIZE)
                .appendQueryParameter("maptype", MAPTYPE)
                .appendQueryParameter("markers", "color:" + COLOR + "%7Clabel:S%7C" + latitude + "," + longitude)
                .appendQueryParameter("key", KEY);

        return URI_SCHEME_MAP + builder.build().getQuery();
    }

    public static String getRemoteUri(String location, Context context) {
        if (!isJSONValid(location)) {
            return "";
        }

        ArrayList<Double> locations = new ArrayList<>(2);

        JsonParser jsonParser = new JsonParser();
        JsonObject jo = (JsonObject) jsonParser.parse(location);

        Set<Map.Entry<String, JsonElement>> entries = jo.entrySet();

        for (Map.Entry<String, JsonElement> entry: entries) {
            JsonElement jE = jo.get(entry.getKey());
            locations.add((jE == null) ? 0 :jE.getAsDouble());
        }

        return generateURI(locations.get(0), locations.get(1), context).replaceAll("&amp;(?!&)", "&");
    }

    public static String getRemoteUri(String location, BuilderParams params) {
//       the JsonParser logic
        return "";
    }

    private static boolean isJSONValid(String jsonInString) {
        if (TextUtils.isEmpty(jsonInString)) {
            return false;
        }
        Gson gson = new Gson();
        try {
            gson.fromJson(jsonInString, JsonObject.class);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    public static final class BuilderParams {
        String uriSchemeMap;
        String zoom;
        String size;
        String mapType;
        String color;
        String key;


        public BuilderParams setUriSchemeMap(String uriSchemeMap) {
            this.uriSchemeMap = uriSchemeMap;
            return this;
        }

        public BuilderParams setZoom(String zoom) {
            this.zoom = zoom;
            return this;
        }

        public BuilderParams setSize(String size) {
            this.size = size;
            return this;
        }

        public BuilderParams setMapType(String mapType) {
            this.mapType = mapType;
            return this;
        }

        public BuilderParams setColor(String color) {
            this.color = color;
            return this;
        }

        public BuilderParams setKey(String key) {
            this.key = key;
            return this;
        }
    }
}