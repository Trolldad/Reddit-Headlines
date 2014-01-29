package net.trolldad.dashclock.redditheadlines.analytics;

import android.util.Log;

import com.google.analytics.tracking.android.ExceptionParser;

/**
 * Created by jacob-tabak on 1/26/14.
 */
public class AnalyticsExceptionParser implements ExceptionParser {
    public String getDescription(String thread, Throwable throwable) {
        return "Thread: " + thread + ", Exception: " + Log.getStackTraceString(throwable);
    }
}