package com.trolldad.dashclock.redditheadlines.imgur;

import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by jacob-tabak on 1/19/14.
 */
public interface ImgurService {
    @GET("/3/image/{id}")
    ImgurImageResponse imageInfo(@Path("id") String imageId);

    @GET("/3/album/{id}")
    ImgurAlbumResponse albumInfo(@Path("id") String albumId);
}
