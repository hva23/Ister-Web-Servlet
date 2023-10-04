package com.ister.isterservlet.web;

import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BaseWebJson {

    /**
     * Reads request body only if it's JSON
     *
     * @return JSON string as a HashMap object
     */
    public Map<String, String> readRequestBody(HttpServletRequest request) {
        try {
            StringBuilder jsonData = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            Iterator<String> keys;
            JSONObject json;
            String line;
            Map<String, String> map = new HashMap<>();

            // Get request body lines
            while ((line = reader.readLine()) != null) jsonData.append(line);

            // Now jsonData has all body data
            // Make a new JSONObject to parse jsonData
            json = new JSONObject(jsonData.toString());

            // Get all keys
            keys = json.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                map.put(key, json.getString(key));
            }

            return map;

        } catch (IOException e) {
            return null;
        } catch (JSONException e) {
            return null;
        }
    }

    public Map<String, String> readRequestBody(String body) {
        try {
            Iterator<String> keys;
            JSONObject json;
            Map<String, String> map = new HashMap<>();

            // Make a new JSONObject to parse body
            json = new JSONObject(body);

            // Get all keys
            keys = json.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                map.put(key, json.getString(key));
            }

            return map;
        } catch (JSONException e) {
            return null;
        }
    }
}
