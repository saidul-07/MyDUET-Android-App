package com.example.myduet.utils;

import android.content.Context;
import com.example.myduet.models.AdmissionResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AdmissionResultJsonReader {

    public static List<AdmissionResult> readResultsFromAssets(Context context, String fileName) throws IOException {
        InputStream is = context.getAssets().open(fileName);
        InputStreamReader reader = new InputStreamReader(is);
        Type listType = new TypeToken<List<AdmissionResult>>() {}.getType();
        List<AdmissionResult> results = new Gson().fromJson(reader, listType);
        reader.close();
        is.close();
        return results != null ? results : new ArrayList<>();
    }
}