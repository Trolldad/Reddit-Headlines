package com.trolldad.dashclock.redditheadlines;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.trolldad.dashclock.redditheadlines.analytics.AnalyticsExceptionParser;

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
        EasyTracker.getInstance(this);
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (uncaughtExceptionHandler instanceof ExceptionReporter) {
            ExceptionReporter exceptionReporter = (ExceptionReporter) uncaughtExceptionHandler;
            exceptionReporter.setExceptionParser(new AnalyticsExceptionParser());
        }
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
