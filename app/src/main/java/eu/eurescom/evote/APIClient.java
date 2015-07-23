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

    @GET("/v1/poll/get")
    void getPollForCode(@Query("token") String token, Callback<JsonObject> cb);

    @POST("/v1/vote/submit")
    void submitVoteForCode(@Query("token") String token, @Body JsonObject json, Callback<JsonObject> cb);
}
