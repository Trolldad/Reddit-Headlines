package com.trolldad.dashclock.redditheadlines.preferences;

import com.trolldad.dashclock.redditheadlines.R;

import org.androidannotations.annotations.sharedpreferences.DefaultRes;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by jacob-tabak on 1/3/14.
 */
@SharedPref(value=SharedPref.Scope.APPLICATION_DEFAULT)
public interface MyPrefs {
    String subreddit();

    @DefaultString("hot")
    String sortOrder();

    String redditUser();

    String redditPassword();

    String modHash();

    String cookie();

    @DefaultRes(R.string.open_link)
    String actionOnClick();

    boolean usePreview();

    boolean hqImages();

    boolean backToDaydream();
}
