package com.example.myduet.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SupabaseRealtimeClient {

    private static final String TAG = "SupabaseRealtime";
    private static final long HEARTBEAT_INTERVAL_MS = 25000; // 25 seconds
    private static final long RECONNECT_DELAY_MS = 5000;

    public interface OnRealtimeEventListener {
        void onEventChange(String eventType, String rawPayload);
        void onConnectionStateChange(boolean isConnected);
    }

    private WebSocket webSocket;
    private final OkHttpClient client;
    private OnRealtimeEventListener listener;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final AtomicInteger messageRef = new AtomicInteger(1);
    private boolean isStarted = false;
    private boolean isConnecting = false;

    private final Runnable heartbeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (webSocket != null) {
                int ref = messageRef.incrementAndGet();
                String heartbeat = "{\"topic\":\"phoenix\",\"event\":\"heartbeat\",\"payload\":{},\"ref\":\"" + ref + "\"}";
                webSocket.send(heartbeat);
                mainHandler.postDelayed(this, HEARTBEAT_INTERVAL_MS);
            }
        }
    };

    public SupabaseRealtimeClient() {
        this.client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS) // Keep alive
                .build();
    }

    public void setListener(OnRealtimeEventListener listener) {
        this.listener = listener;
    }

    public synchronized void start() {
        if (!SupabaseConfig.isConfigured()) {
            Log.d(TAG, "Supabase is not configured yet with valid credentials. Skipping Realtime.");
            return;
        }
        if (isStarted || isConnecting) return;

        isStarted = true;
        connect();
    }

    private synchronized void connect() {
        if (webSocket != null) {
            webSocket.close(1000, "Reconnecting");
            webSocket = null;
        }

        isConnecting = true;
        String wsUrl = SupabaseConfig.getRealtimeWebSocketUrl();
        Request request = new Request.Builder().url(wsUrl).build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket ws, Response response) {
                Log.d(TAG, "WebSocket Connected successfully");
                isConnecting = false;
                
                // Join the public:events channel with postgres_changes listener
                int ref = messageRef.incrementAndGet();
                String joinPayload = "{"
                        + "\"topic\":\"realtime:public:events\","
                        + "\"event\":\"phx_join\","
                        + "\"payload\":{\"config\":{\"postgres_changes\":[{\"event\":\"*\",\"schema\":\"public\",\"table\":\"events\"}]}},"
                        + "\"ref\":\"" + ref + "\""
                        + "}";
                ws.send(joinPayload);

                // Start heartbeat
                mainHandler.post(heartbeatRunnable);

                if (listener != null) {
                    mainHandler.post(() -> listener.onConnectionStateChange(true));
                }
            }

            @Override
            public void onMessage(WebSocket ws, String text) {
                Log.d(TAG, "Realtime Message: " + text);

                if (text.contains("phx_reply") || text.contains("phx_close")) {
                    return;
                }

                // Check if this is an event notification
                if (listener != null) {
                    mainHandler.post(() -> listener.onEventChange("CHANGE", text));
                }
            }

            @Override
            public void onClosing(WebSocket ws, int code, String reason) {
                Log.d(TAG, "WebSocket Closing: " + reason);
                ws.close(1000, null);
            }

            @Override
            public void onClosed(WebSocket ws, int code, String reason) {
                Log.d(TAG, "WebSocket Closed: " + reason);
                cleanup();
                if (listener != null) {
                    mainHandler.post(() -> listener.onConnectionStateChange(false));
                }
                scheduleReconnect();
            }

            @Override
            public void onFailure(WebSocket ws, Throwable t, Response response) {
                Log.w(TAG, "WebSocket Failure: " + t.getMessage());
                cleanup();
                if (listener != null) {
                    mainHandler.post(() -> listener.onConnectionStateChange(false));
                }
                scheduleReconnect();
            }
        });
    }

    private void cleanup() {
        mainHandler.removeCallbacks(heartbeatRunnable);
        isConnecting = false;
        webSocket = null;
    }

    private void scheduleReconnect() {
        if (!isStarted) return;
        mainHandler.postDelayed(() -> {
            if (isStarted && webSocket == null && !isConnecting) {
                Log.d(TAG, "Attempting Realtime reconnection...");
                connect();
            }
        }, RECONNECT_DELAY_MS);
    }

    public synchronized void stop() {
        isStarted = false;
        cleanup();
        if (webSocket != null) {
            webSocket.close(1000, "Client stopped");
            webSocket = null;
        }
    }
}

