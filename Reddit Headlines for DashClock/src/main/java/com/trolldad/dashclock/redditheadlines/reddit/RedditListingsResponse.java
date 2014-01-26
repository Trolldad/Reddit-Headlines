package com.trolldad.dashclock.redditheadlines.reddit;

/**
 * Created by jacob-tabak on 1/19/14.
 */
public class RedditListingsResponse {
    private String kind;
    private RedditListings data;

    private static class RedditListings {
        String modhash;
        RedditListing[] children;
    }

    public String getModhash() {
        if (data != null) {
            return data.modhash;
        }
        return null;
    }

    public RedditListing[] getListings() {
        if (data != null) {
            return data.children;
        }
        return null;
    }

    public RedditListing getListing() {
        if (data != null && data.children.length > 0) {
            return data.children[0];
        }
        return null;
    }
}
