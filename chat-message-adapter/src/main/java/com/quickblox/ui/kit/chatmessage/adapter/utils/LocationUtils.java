package com.quickblox.ui.kit.chatmessage.adapter.utils;

import android.content.Context;
import android.net.Uri;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.quickblox.ui.kit.chatmessage.adapter.R;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Roman on 14.01.2017.
 */

public class LocationUtils {

    public static String generateLocationJson(Pair<String, Double> latitude, Pair<String, Double> longitude) {
        JsonObject innerObject = new JsonObject();
        innerObject.addProperty(latitude.first, String.valueOf(latitude.second));
        innerObject.addProperty(longitude.first, String.valueOf(longitude.second));

        return innerObject.toString();
    }

    public static String generateURI(double latitude, double longitude, Context context) {
        String uriSchemeMap = context.getString(R.string.uri_scheme_map);
        String zoom = context.getString(R.string.map_zoom);
        String size = context.getString(R.string.map_zize);
        String mapType = context.getString(R.string.map_type);
        String color = context.getString(R.string.map_color);

        //api static map key should be generated in developers google console
        String key = context.getString(R.string.google_static_maps_key);
        if (TextUtils.isEmpty(key)) {
            Log.e("LocationUtils", "You should set google_static_maps_key in resource!");
        }

        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter("zoom", zoom)
                .appendQueryParameter("size", size)
                .appendQueryParameter("maptype", mapType)
                .appendQueryParameter("markers", "color:" + color + "%7Clabel:S%7C" + latitude + "," + longitude)
                .appendQueryParameter("key", key);

        return (uriSchemeMap + builder.build().getQuery()).replaceAll("&amp;(?!&)", "&");
    }

    public static String getRemoteUri(String location, Context context) {
        Pair<Double, Double> latLng = getLatLngFromJson(location);

        return generateURI(latLng.first, latLng.second, context);
    }

    public static Pair<Double, Double> getLatLngFromJson(String location) {
        if (!isJSONValid(location)) {
            return new Pair<>(0.0, 0.0);
        }

        JsonParser jsonParser = new JsonParser();
        JsonObject jo = (JsonObject) jsonParser.parse(location);

        Iterator<Map.Entry<String, JsonElement>> iterator = jo.entrySet().iterator();

        JsonElement jELat = jo.get(iterator.next().getKey());
        JsonElement jELng = jo.get(iterator.next().getKey());

        double lat = (jELat == null) ? 0.0 : jELat.getAsDouble();
        double lng = (jELng == null) ? 0.0 : jELng.getAsDouble();

        return new Pair<>(lat, lng);
    }

    public static String getRemoteUri(String location, BuilderParams params) {
//       another way to get url
//       the JsonParser logic...
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