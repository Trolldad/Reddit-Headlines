package com.trolldad.dashclock.redditheadlines.fragment;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.squareup.otto.Subscribe;
import com.trolldad.dashclock.redditheadlines.R;
import com.trolldad.dashclock.redditheadlines.RedditHeadlinesApplication;
import com.trolldad.dashclock.redditheadlines.RedditHeadlinesExtension;
import com.trolldad.dashclock.redditheadlines.activity.AboutActivity_;
import com.trolldad.dashclock.redditheadlines.otto.LoginService;
import com.trolldad.dashclock.redditheadlines.otto.MyBus;
import com.trolldad.dashclock.redditheadlines.otto.UpdateService;
import com.trolldad.dashclock.redditheadlines.preferences.MyPrefs_;
import com.trolldad.dashclock.redditheadlines.reddit.RedditClient;
import com.trolldad.dashclock.redditheadlines.reddit.RedditMe;
import com.trolldad.dashclock.redditheadlines.reddit.RedditMeResponse;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.api.sharedpreferences.AbstractPrefField;

/**
 * Created by jacob-tabak on 1/25/14.
 */
@EFragment
public class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Pref
    MyPrefs_ mPrefs;

    @Bean
    RedditClient mRedditClient;

    @Bean
    LoginService mLoginService;

    @Bean
    MyBus mBus;

    @Bean
    UpdateService mUpdateService;

    @StringRes(R.string.provider_authority)
    String mProviderAuthority;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        findPreference(getString(R.string.pref_account)).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        LoginDialogFragment_.builder().build().show(getFragmentManager(), "login");
                        return true;
                    }
                }
        );
        findPreference("about").setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        startActivity(AboutActivity_.intent(PrefsFragment.this).get());
                        return true;
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        mBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(null, null);
        if (mPrefs.cookie().get().length() == 0) {
            setSummary(mPrefs.redditUser(), "You are not logged in.  Tap here to log in.");
        }
        else {
            setSummary(mPrefs.redditUser(), "Confirming session, please wait...");
            verifyLogin();
        }
    }

    @Background
    void verifyLogin() {
        String name = null;
        try {
            RedditMeResponse meResponse = mRedditClient.getService().me();
            RedditMe me = meResponse.data;
            name = me.name;
        }
        catch (Exception e) {
            Log.e(RedditHeadlinesApplication.TAG, Log.getStackTraceString(e));
        }
        mLoginService.onLoginResult(name != null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (mPrefs.subreddit().get().length() > 0) {
            setSummary(mPrefs.subreddit(), "Posts from /r/" + mPrefs.subreddit().get() + " will be displayed");
        }
        else {
            setSummary(mPrefs.subreddit(), "Posts from the front page will be displayed");
        }

        setSummary(mPrefs.sortOrder(), "The \"" + mPrefs.sortOrder().get() + "\" sort order will be used");

        setSummary(mPrefs.usePreview(), mPrefs.usePreview().get() ?
                "Images from imgur will be displayed in-app" :
                "Images from imgur will open up normally");

        setSummary(mPrefs.actionOnClick(),
                mPrefs.actionOnClick().get().equals(getString(R.string.open_link)) ?
                        "Clicking a link will open the link directly" :
                        "Clicking a link will open up the comments page (or your other Reddit app)"
        );
        setSummary(mPrefs.hqImages(),
                mPrefs.hqImages().get() ?
                        "Full size images will always be loaded first" :
                        "Large thumbnails will be displayed first"
        );
        mUpdateService.onUpdateDashClock();
    }

    @Subscribe
    public void onLoginResult(LoginService.LoginResultEvent e) {
        if (e.result && findPreference(mPrefs.redditUser().key()) != null) {
            setSummary(mPrefs.redditUser(), "Logged in as " + mPrefs.redditUser().get());
        }
        else {
            setSummary(mPrefs.redditUser(), "You are not logged in.  Tap here to log in.");
        }
    }

    void setSummary(AbstractPrefField pref, String value) {
        findPreference(pref.key()).setSummary(value);
    }
}
