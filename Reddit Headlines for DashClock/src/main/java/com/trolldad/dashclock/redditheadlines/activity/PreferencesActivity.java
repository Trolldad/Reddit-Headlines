package com.trolldad.dashclock.redditheadlines.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.google.analytics.tracking.android.EasyTracker;
import com.trolldad.dashclock.redditheadlines.R;
import com.trolldad.dashclock.redditheadlines.fragment.LoginDialogFragment_;
import com.trolldad.dashclock.redditheadlines.preferences.MyPrefs;
import com.trolldad.dashclock.redditheadlines.preferences.MyPrefs_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;

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