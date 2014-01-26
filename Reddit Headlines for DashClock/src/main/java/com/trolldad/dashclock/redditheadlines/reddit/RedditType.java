package com.trolldad.dashclock.redditheadlines.reddit;

import java.lang.reflect.Type;

/**
 * Created by jacob-tabak on 1/19/14.
 */
public class RedditType {
    public static final String COMMENT = "t1";
    public static final String ACCOUNT = "t2";
    public static final String LINK = "t3";
    public static final String MESSAGE = "t4";
    public static final String SUBREDDIT = "t5";
    public static final String AWARD = "t6";
    public static final String PROMOCAMPAIGN = "t7";

    RedditType(String type) {
        mType = type;
    }

    private final String mType;

    public static Type getType(String type) {
        if (type.equals(LINK)) {
            return RedditLink.class;
        }
        return null;
    }

}
