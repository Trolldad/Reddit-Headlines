package net.trolldad.dashclock.redditheadlines.imgur;

/**
 * Created by jacob-tabak on 1/19/14.
 */
public class ImgurAlbumResponse {
    public boolean success;
    public int status;
    private ImgurAlbum data;

    public ImgurAlbum getAlbum() {
        return data;
    }
}
