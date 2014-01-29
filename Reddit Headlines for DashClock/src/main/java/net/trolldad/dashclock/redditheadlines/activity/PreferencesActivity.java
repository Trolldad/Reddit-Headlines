package net.trolldad.dashclock.redditheadlines.activity;

import android.app.Activity;

import com.google.analytics.tracking.android.EasyTracker;
import net.trolldad.dashclock.redditheadlines.R;

import org.androidannotations.annotations.EActivity;

/**
 * Created by jacob-tabak on 1/2/14.
 */
@EActivity(R.layout.activity_preferences)
public class PreferencesActivity extends Activity {
    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }
}