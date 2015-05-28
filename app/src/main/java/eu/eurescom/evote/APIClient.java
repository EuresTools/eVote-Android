package eu.eurescom.evote;


import com.google.gson.JsonObject;


import java.util.ArrayList;
import java.util.HashSet;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
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

    @FormUrlEncoded
    @POST("/vote")
    void submitVoteForCode(@Field("code") String code, @Field("votes") HashSet<Integer> votes, Callback<JsonObject> cb);
}
