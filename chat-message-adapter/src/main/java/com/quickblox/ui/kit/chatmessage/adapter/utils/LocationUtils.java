package com.quickblox.ui.kit.chatmessage.adapter.utils;

import android.content.Context;
import android.net.Uri;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.quickblox.ui.kit.chatmessage.adapter.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Roman on 14.01.2017.
 */

public class LocationUtils {

    public static String generateLocationJson(Pair<String, Double> latitude, Pair<String, Double> longitude) {
        Map<String, String> latLng = new LinkedHashMap<>();
        latLng.put(latitude.first, String.valueOf(latitude.second));
        latLng.put(longitude.first, String.valueOf(longitude.second));
        return JsonParserBase.serialize(latLng);
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

    public static Pair<Double, Double> getLatLngFromJson(String jsonLocation) {
        double lat = 0.0;
        double lng = 0.0;
        try {
            Map<String, Object> latLng = JsonParserBase.deserialize(jsonLocation);
            Iterator<Map.Entry<String, Object>> it = latLng.entrySet().iterator();
            lat = Double.parseDouble(String.valueOf(it.next().getValue()));
            lng = Double.parseDouble(String.valueOf(it.next().getValue()));
        } catch (Exception ex) {
            Log.e("LocationUtils", "Can't parse JsonObject: " + ex.getMessage());
        }
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

        public BuilderParams setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public BuilderParams setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }
    }

    public static LocationUtils.BuilderParams defaultUrlLocationParams(Context context) {
        return new LocationUtils.BuilderParams()
                .setUriSchemeMap(context.getString(R.string.uri_scheme_map))
                .setZoom(context.getString(R.string.map_zoom))
                .setSize(context.getString(R.string.map_size))
                .setMapType(context.getString(R.string.map_type))
                .setColor(context.getString(R.string.map_color))
                .setKey(context.getString(R.string.google_static_maps_key));
    }
}