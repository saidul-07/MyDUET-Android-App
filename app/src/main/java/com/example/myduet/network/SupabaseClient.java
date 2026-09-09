package com.example.myduet.network;

import android.content.Context;
import com.example.myduet.storage.SessionManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class SupabaseClient {

    private static SupabaseApiService apiService;
    private static OkHttpClient okHttpClient;

    public static synchronized OkHttpClient getOkHttpClient(Context context) {
        if (okHttpClient == null) {
            final SessionManager sessionManager = new SessionManager(context);

            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        String token = sessionManager.getAccessToken();
                        String authHeader = (token != null && !token.isEmpty())
                                ? "Bearer " + token
                                : "Bearer " + SupabaseConfig.SUPABASE_ANON_KEY;

                        Request.Builder builder = original.newBuilder()
                                .header("apikey", SupabaseConfig.SUPABASE_ANON_KEY)
                                .header("Authorization", authHeader)
                                .header("Content-Type", "application/json");

                        return chain.proceed(builder.build());
                    })
                    .build();
        }
        return okHttpClient;
    }

    public static synchronized SupabaseApiService getApiService(Context context) {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SupabaseConfig.getRestBaseUrl())
                    .client(getOkHttpClient(context))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(SupabaseApiService.class);
        }
        return apiService;
    }

    /**
     * Resets client when login/logout occurs so headers update.
     */
    public static synchronized void resetClient() {
        apiService = null;
        okHttpClient = null;
    }
}

