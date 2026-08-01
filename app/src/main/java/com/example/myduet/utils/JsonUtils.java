package com.example.myduet.utils;

import android.content.Context;
import com.example.myduet.models.SeatPlan;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static List<SeatPlan> loadSeatPlanFromAssets(Context context, String fileName) {
        List<SeatPlan> seatPlanList = new ArrayList<>();
        Gson gson = new Gson();
        InputStream is = null;
        try {
            is = context.getAssets().open(fileName);
            Type listType = new com.google.gson.reflect.TypeToken<List<SeatPlan>>() {}.getType();
            seatPlanList = gson.fromJson(new InputStreamReader(is), listType);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return seatPlanList;
    }
}