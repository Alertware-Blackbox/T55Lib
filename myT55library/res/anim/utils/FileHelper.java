package com.blackbox.roadways.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileHelper {

    public static JSONObject getAssetFileJson(Context context, String fileName) {
        if (TextUtils.isEmpty(fileName)) return null;
        try {
            JSONObject assetJson = parseInputStreamToJson(new InputStreamReader(context.getAssets().open(fileName)));
            return assetJson;

        } catch (IOException e) {
            Log.d(FileHelper.class.getSimpleName(), fileName, e);
        }
        return null;
    }

    static JSONObject parseInputStreamToJson(InputStreamReader isr) {
        JSONObject data = null;
        if (isr == null) return data;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            try {
                data = new JSONObject(builder.toString());
            } catch (JSONException e) {
                Log.e(FileHelper.class.getSimpleName(), e.getMessage());
            }

            return data;

        } catch (IOException e) {
            return data;

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(FileHelper.class.getSimpleName(), e.getMessage());
                }
            }
        }
    }
}