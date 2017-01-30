package com.quickblox.ui.kit.chatmessage.adapter.utils;

import android.net.Uri;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Iterator;
import java.util.Map;

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

    public static String generateURI(BuilderParams params) {
        if (TextUtils.isEmpty(params.key)) {
            Log.e("LocationUtils", "You should set google_static_maps_key in string resource!");
        }

        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter("zoom", params.zoom)
                .appendQueryParameter("size", params.size)
                .appendQueryParameter("maptype", params.mapType)
                .appendQueryParameter("markers", "color:" + params.color + "%7Clabel:S%7C" + params.latitude + "," + params.longitude)
                .appendQueryParameter("key", params.key);

        return (params.uriSchemeMap + builder.build().getQuery()).replaceAll("&amp;(?!&)", "&");
    }

    public static String getRemoteUri(String location, BuilderParams params) {
        Pair<Double, Double> latLng = getLatLngFromJson(location);
        if (params.latitude == null) {
            params.setLatitude(latLng.first);
        }
        if (params.longitude == null) {
            params.setLongitude(latLng.second);
        }

        return generateURI(params);
    }

    public static Pair<Double, Double> getLatLngFromJson(String location) {

        JsonParser jsonParser = new JsonParser();
        JsonObject jo;

        try {
            jo = (JsonObject) jsonParser.parse(location);
        } catch (Exception ex) {
            Log.e("LocationUtils", "Can't parse JsonObject: " + ex.getMessage());
            return new Pair<>(0.0, 0.0);
        }

        Iterator<Map.Entry<String, JsonElement>> iterator = jo.entrySet().iterator();

        JsonElement jELat = jo.get(iterator.next().getKey());
        JsonElement jELng = jo.get(iterator.next().getKey());

        double lat = (jELat == null) ? 0.0 : jELat.getAsDouble();
        double lng = (jELng == null) ? 0.0 : jELng.getAsDouble();

        return new Pair<>(lat, lng);
    }


    public static final class BuilderParams {
        String uriSchemeMap;
        String zoom;
        String size;
        String mapType;
        String color;
        //api static map key should be generated in developers google console
        String key;
        Double latitude;
        Double longitude;


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

        public BuilderParams setLatitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public BuilderParams setLongitude(double latitude) {
            this.latitude = latitude;
            return this;
        }
    }
}