package eu.eurescom.evote;


import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by trainee on 26.5.2015.
 */
public interface APIClient {

    // It would be better to create POJOs for the response and make the callbacks receive them.
    // Then retrofit would parse the JSON automatically.
    @GET("/vote")
    void getPollForCode(@Query("code") String code, Callback<JsonObject> cb);

    @POST("/vote")
    void submitVoteForCode(@Body JsonObject json, Callback<JsonObject> cb);
}
