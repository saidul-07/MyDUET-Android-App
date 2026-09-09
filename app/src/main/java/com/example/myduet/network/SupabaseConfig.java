package com.example.myduet.network;

public class SupabaseConfig {

    public static final String SUPABASE_URL = "https://vkijvmpcuhuuddlocumd.supabase.co";
    public static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZraWp2bXBjdWh1dWRkbG9jdW1kIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODgyMzUzNzksImV4cCI6MjEwMzgxMTM3OX0.L_Oxaxo_92AasgrtAQTNRQJzQrTa1zaVinZQj3uKKbc";

    public static String getRestBaseUrl() {
        String url = SUPABASE_URL.trim();
        if (!url.endsWith("/")) {
            url += "/";
        }
        return url;
    }

    public static String getRealtimeWebSocketUrl() {
        String base = SUPABASE_URL.trim()
                .replace("https://", "wss://")
                .replace("http://", "ws://");
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/realtime/v1/websocket?apikey=" + SUPABASE_ANON_KEY + "&vsn=1.0.0";
    }

    public static boolean isConfigured() {
        return SUPABASE_URL != null && !SUPABASE_URL.contains("your-project-id")
                && SUPABASE_ANON_KEY != null && !SUPABASE_ANON_KEY.contains("your-anon-key");
    }
}
