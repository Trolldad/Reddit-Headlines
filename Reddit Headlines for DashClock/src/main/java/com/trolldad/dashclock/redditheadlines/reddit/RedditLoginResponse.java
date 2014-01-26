package com.trolldad.dashclock.redditheadlines.reddit;

/**
 * Created by jacob-tabak on 1/19/14.
 */
public class RedditLoginResponse {
    public static class RedditLoginResponseJson {
        public String[][] errors;
        public RedditLogin data;
    }

    public RedditLoginResponseJson json;
}
