package com.sswebtricks.sanjeev.autocompletelocation;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by sanjeev on 4/2/18.
 */

public class LocationModel {

    private static final String TAG = LocationModel.class.getSimpleName();

    private static final String URL = "https://maps.googleapis.com/maps/api/place";
    private static final String API_TYPE = "/autocomplete";
    private static final String OUTPUT = "/json";

    private static final String KEY = "YOUR API KEY";

    public ArrayList<String> autocomplete (String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(URL + API_TYPE + OUTPUT);
            sb.append("?key=" + KEY);
            sb.append("&types=(cities)");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Api Error", e);
            return resultList;
        } catch (IOException e) {
            Log.e(TAG, "Connection Error", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            JSONObject Object = new JSONObject(jsonResults.toString());
            JSONArray PredictArray = Object.getJSONArray("predictions");

            resultList = new ArrayList<String>(PredictArray.length());
            for (int i = 0; i < PredictArray.length(); i++) {
                resultList.add(PredictArray.getJSONObject(i).getString("description"));

                //Adding all search result in resultlist
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot Parse Json", e);
        }

        return resultList;
    }
}