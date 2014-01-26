package com.trolldad.dashclock.redditheadlines.reddit;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by jacob-tabak on 1/19/14.
 */
public interface RedditService {
    @GET("/r/{subreddit}/{sortOrder}.json")
    RedditListingsResponse subreddit(@Path("subreddit") String subreddit, @Path("sortOrder") String sortOrder, @Query("limit") Integer limit);

    @GET("/{sortOrder}.json")
    RedditListingsResponse frontpage(@Path("sortOrder") String sortOrder, @Query("limit") Integer limit);

    @FormUrlEncoded
    @POST("/api/login")
    RedditLoginResponse login(
            @Field("user") String user,
            @Field("passwd") String password,
            @Field("rem") boolean rem,
            @Field("api_type") String api_type
    );

    @GET("/api/me.json")
    RedditMeResponse me();

    @GET("/by_id/{name}.json")
    RedditListingsResponse byName(@Path("name") String name);

    @FormUrlEncoded
    @POST("/api/vote")
    Void vote(
            @Field("id") String name,
            @Field("dir") Integer direction
    );

    @FormUrlEncoded
    @POST("/api/save")
    Void save(@Field("id") String name);

    @FormUrlEncoded
    @POST("/api/unsave")
    Void unsave(@Field("id") String name);

    @FormUrlEncoded
    @POST("/api/hide")
    Void hide(@Field("id") String name);

    @FormUrlEncoded
    @POST("/api/unhide")
    Void unhide(@Field("id") String name);
}