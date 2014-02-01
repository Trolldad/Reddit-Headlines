package net.trolldad.dashclock.redditheadlines.cache;

import net.trolldad.dashclock.redditheadlines.imgur.ImgurAlbum;
import net.trolldad.dashclock.redditheadlines.imgur.ImgurImage;
import net.trolldad.dashclock.redditheadlines.reddit.RedditLink;

import org.androidannotations.annotations.EBean;

/**
 * Created by jacob-tabak on 2/1/14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class ContentCache {

    public ImgurImage image;
    public ImgurAlbum album;
    public RedditLink link;

    public void cacheLink(RedditLink link) {
        this.link = link;
    }

    public void cacheImageMetadata(ImgurImage image) {
        this.image = image;
    }

    public void cacheAlbumMetadata(ImgurAlbum album) {
        this.album = album;
    }

    public void clearCache() {
        link = null;
        image = null;
        album = null;
    }
}
