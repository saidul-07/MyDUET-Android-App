package com.example.myduet.repositories;

import android.content.Context;
import com.example.myduet.models.Teacher;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherRepository {
    private final Context context;
    private final Map<String, List<Teacher>> cache = new HashMap<>();

    public TeacherRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public List<Teacher> loadTeachers(String deptKey) {
        if (cache.containsKey(deptKey)) {
            return cache.get(deptKey);
        }

        List<Teacher> list = new ArrayList<>();
        String fileName = "teachers/" + deptKey + "_teachers.json";
        Gson gson = new Gson();
        InputStream is = null;
        try {
            is = context.getAssets().open(fileName);
            Type listType = new TypeToken<List<Teacher>>() {}.getType();
            list = gson.fromJson(new InputStreamReader(is), listType);
            if (list == null) {
                list = new ArrayList<>();
            }
            cache.put(deptKey, list);
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
        return list;
    }
}
