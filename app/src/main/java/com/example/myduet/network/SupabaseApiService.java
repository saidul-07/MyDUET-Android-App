package com.example.myduet.network;

import com.example.myduet.models.AuthRequest;
import com.example.myduet.models.AuthResponse;
import com.example.myduet.models.Event;
import com.example.myduet.models.Profile;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;
import java.util.Map;

public interface SupabaseApiService {

    // --- Authentication ---
    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> login(@Body AuthRequest request);

    // --- Profiles ---
    @GET("rest/v1/profiles?select=*")
    Call<List<Profile>> getProfileByUserId(@Query("user_id") String userIdFilter);

    @GET("rest/v1/profiles?select=*")
    Call<List<Profile>> getProfileById(@Query("id") String idFilter);

    // --- Events CRUD ---
    @GET("rest/v1/events?select=*&order=created_at.desc")
    Call<List<Event>> getAllEvents();

    @GET("rest/v1/events?select=*&order=created_at.desc")
    Call<List<Event>> getEventsByCreator(@Query("created_by") String createdByFilter);

    @POST("rest/v1/events")
    Call<List<Event>> createEvent(
            @Header("Prefer") String prefer, // "return=representation"
            @Body Event event
    );

    @PATCH("rest/v1/events")
    Call<List<Event>> updateEvent(
            @Query("id") String idFilter, // "eq.123"
            @Header("Prefer") String prefer, // "return=representation"
            @Body Event event
    );

    @PATCH("rest/v1/events")
    Call<List<Event>> updateEventPartial(
            @Query("id") String idFilter,
            @Header("Prefer") String prefer,
            @Body Map<String, Object> fields
    );

    @DELETE("rest/v1/events")
    Call<Void> deleteEvent(@Query("id") String idFilter); // "eq.123"
}

