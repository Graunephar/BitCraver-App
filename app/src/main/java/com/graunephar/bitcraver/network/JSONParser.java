package com.graunephar.bitcraver.network;

import android.text.Spanned;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {

/**
    public static Spanned JSONtoHTML(String data) {

        return new Sp
    }
**/

    public static String getContentOfFirstPost(String data) {

        String result = null;

        try {
            JSONArray array = new JSONArray(data);
            JSONObject post = array.getJSONObject(0); // get the first post
            JSONObject content = post.getJSONObject("content");
            result = content.getString("rendered");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;

    }


}
