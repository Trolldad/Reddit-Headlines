package com.trolldad.dashclock.redditheadlines.imgur;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by jacob-tabak on 1/18/14.
 */
public class ImgurImage implements Serializable {
    public static final String THUMB_SIZE_HUGE = "h";
    public static final String ORIGINAL_SIZE = "";
    public static final int MAX_WIDTH_OR_HEIGHT = 2048;
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
     * @param suffix
     * @return
     */
    public String getResizedImage(String suffix) {
        // if they don't want a thumb, make sure the image is small enough to display
        if (suffix.length() == 0 && (width > MAX_WIDTH_OR_HEIGHT || height > MAX_WIDTH_OR_HEIGHT)) {
            suffix = THUMB_SIZE_HUGE;
        }
        int dotPosition = link.lastIndexOf(".");
        StringBuilder thumbBuilder = new StringBuilder(link.substring(0, dotPosition));
        thumbBuilder.append(suffix);
        thumbBuilder.append(link.substring(dotPosition));
        return thumbBuilder.toString();
    }

    public boolean isHighQualityAvailable(int thumbDimension) {
        return (width > thumbDimension || height > thumbDimension);
    }
}
