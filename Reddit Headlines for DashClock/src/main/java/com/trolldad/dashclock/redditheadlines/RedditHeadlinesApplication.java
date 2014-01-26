package com.trolldad.dashclock.redditheadlines;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.UiThread;

/**
 * Created by jacob-tabak on 1/3/14.
 */
@EApplication
public class RedditHeadlinesApplication extends Application {
    public static final String TAG = "RedditHeadlinesApplication";
    private static RedditHeadlinesApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static Context getContext() {
        return mInstance;
    }

    public static void toast(String message) {
        mInstance.makeToast(message);
    }

    @UiThread
    void makeToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
