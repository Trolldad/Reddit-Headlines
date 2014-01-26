package com.trolldad.dashclock.redditheadlines.reddit;

import org.joda.time.DateTime;

/**
 * Created by jacob-tabak on 1/19/14.
 */
public class RedditLink extends RedditListing {
    public String id;
    public String name;
    public String domain;
    public String subreddit;
    public String selftext;
    public String selftext_html;
    public String author;
    public String score;
    public String ups;
    public String downs;
    public Boolean saved;
    public Boolean is_self;
    public Boolean likes;
    public String permalink;
    public DateTime created_utc;
    public String url;
    public String title;
    public Boolean hidden;
}
