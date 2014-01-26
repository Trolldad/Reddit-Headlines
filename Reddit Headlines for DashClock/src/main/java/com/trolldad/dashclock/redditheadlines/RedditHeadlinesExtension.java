package com.trolldad.dashclock.redditheadlines;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.trolldad.dashclock.redditheadlines.activity.ImgurPreviewActivity_;
import com.trolldad.dashclock.redditheadlines.imgur.ImgurClient;
import com.trolldad.dashclock.redditheadlines.preferences.MyPrefs_;
import com.trolldad.dashclock.redditheadlines.reddit.RedditClient;
import com.trolldad.dashclock.redditheadlines.reddit.RedditLink;
import com.trolldad.dashclock.redditheadlines.reddit.RedditListingsResponse;
import com.trolldad.dashclock.redditheadlines.reddit.RedditService;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by jacob-tabak on 1/2/14.
 */
@EService
public class RedditHeadlinesExtension extends DashClockExtension {
    public static final String TAG = "RedditHeadlinesExtension";

    @StringRes(R.string.open_link)
    String openLink;

    @StringRes(R.string.open_comments)
    String openComments;

    @StringRes(R.string.uparrow)
    String upArrow;

    @StringRes(R.string.downarrow)
    String downArrow;

    @Pref
    MyPrefs_ mPrefs;

    @Bean
    ImgurClient mImgurClient;

    @Bean
    RedditClient mRedditClient;

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        //android.os.Debug.waitForDebugger();
        Log.d(TAG, "onInitialize");
        setUpdateWhenScreenOn(true);
    }

    @Override
    protected void onUpdateData(int reason) {
        Log.d(TAG, "onUpdateData");
        RedditService redditService = mRedditClient.getService();
        RedditListingsResponse response;
        String subreddit = mPrefs.subreddit().get();
        if (subreddit.length() == 0) {
            response = redditService.frontpage(mPrefs.sortOrder().get(), 1);
        }
        else {
            // replace /r/ with empty string if user entered it accidentally
            if (subreddit.startsWith("/r/")) {
                subreddit = subreddit.replace("/r/", "");
            }
            try {
                response = redditService.subreddit(subreddit, mPrefs.sortOrder().get(), 1);
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
                return;
            }
        }

        if (response.getListing() != null) {
            RedditLink link = (RedditLink) response.getListing();
            publishUpdate(new ExtensionData()
                    .visible(true)
                    .icon(R.drawable.ic_reddit_white)
                    .status(mPrefs.sortOrder().get())
                    .expandedTitle(link.title)
                    .expandedBody(getExpandedBody(link))
                    .contentDescription("reddit")
                    .clickIntent(getClickIntent(link)));
            Log.d(TAG, "Publishing update: " + link.title + " - " + getExpandedBody(link));
        }
        else {
            Log.e(TAG, "Failed to parse listings.");
        }
    }

    private Intent getClickIntent(RedditLink link) {
        Intent intent = null;
        if (mPrefs.usePreview().get() && link.domain.contains("imgur.com")) {
            intent = ImgurPreviewActivity_
                    .intent(this)
                    .mLinkName(link.name)
                    .mLinkUrl(link.url)
                    .mCommentsUrl(link.permalink)
                    .get();
        }
        else {
            Log.d(TAG, "Not using preview or image is null");
            if (mPrefs.actionOnClick().get().equals(openLink)) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link.url));
            }
            else if (mPrefs.actionOnClick().get().equals(openComments)) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://reddit.com" + link.permalink));
            }
        }
        Log.d(TAG, "Exported intent: " + intent.toUri(Intent.URI_INTENT_SCHEME));
        return intent;
    }

    public String getExpandedBody(RedditLink link) {
        StringBuilder expandedBodyBuilder = new StringBuilder();
        expandedBodyBuilder
                .append(upArrow)
                .append(link.ups)
                .append(" ")
                .append(downArrow)
                .append(link.downs)
                .append(" /r/")
                .append(link.subreddit)
                .append(" ")
                .append(link.domain);
        return expandedBodyBuilder.toString();
    }
}
