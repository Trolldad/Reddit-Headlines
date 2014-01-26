package com.trolldad.dashclock.redditheadlines.imgur;

import org.joda.time.DateTime;

/**
 * Created by jacob-tabak on 1/18/14.
 */
public class ImgurImageResponse {
    public boolean success;
    public int status;
    private ImgurImage data;

    public ImgurImage getImage() {
        return data;
    }
}
