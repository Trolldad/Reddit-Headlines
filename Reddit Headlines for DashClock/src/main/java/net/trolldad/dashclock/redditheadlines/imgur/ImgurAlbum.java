package net.trolldad.dashclock.redditheadlines.imgur;

import org.joda.time.DateTime;

/**
 * Created by jacob-tabak on 1/19/14.
 */
public class ImgurAlbum {
    public String id;
    public String title;
    public String description;
    public DateTime datetime;
    public String cover;
    public String account_url;
    public String privacy;
    public String layout;
    public long views;
    public String link;
    public String deletehash;
    public int images_count;
    public ImgurImage[] images;
}
