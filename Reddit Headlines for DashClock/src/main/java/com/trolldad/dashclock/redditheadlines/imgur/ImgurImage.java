package com.trolldad.dashclock.redditheadlines.imgur;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by jacob-tabak on 1/18/14.
 */
public class ImgurImage implements Serializable {
    public String id;
    public String title;
    public String description;
    public DateTime datetime;
    public String type;
    public boolean animated;
    public int width;
    public int height;
    public long size;
    public long views;
    public long bandwidth;
    public String deletehash;
    public String section;
    public String link;

    /**
     * Gets a thumb, more info here: http://api.imgur.com/models/image
     *
     * @param size
     * @return
     */
    public String getThumb(String size) {
        int dotPosition = link.lastIndexOf(".");
        StringBuilder thumbBuilder = new StringBuilder(link.substring(0, dotPosition));
        thumbBuilder.append(size);
        thumbBuilder.append(link.substring(dotPosition));
        return thumbBuilder.toString();
    }
}
