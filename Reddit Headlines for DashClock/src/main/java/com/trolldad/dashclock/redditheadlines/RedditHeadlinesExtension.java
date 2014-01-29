package com.trolldad.dashclock.redditheadlines;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.squareup.otto.Subscribe;
import com.trolldad.dashclock.redditheadlines.activity.PreviewActivity_;
import com.trolldad.dashclock.redditheadlines.analytics.EventAction;
import com.trolldad.dashclock.redditheadlines.analytics.EventCategory;
import com.trolldad.dashclock.redditheadlines.imgur.ImgurClient;
import com.trolldad.dashclock.redditheadlines.otto.MyBus;
import com.trolldad.dashclock.redditheadlines.otto.UpdateService;
import com.trolldad.dashclock.redditheadlines.preferences.MyPrefs_;
import com.trolldad.dashclock.redditheadlines.reddit.RedditClient;
import com.trolldad.dashclock.redditheadlines.reddit.RedditLink;
import com.trolldad.dashclock.redditheadlines.reddit.RedditListingsResponse;
import com.trolldad.dashclock.redditheadlines.reddit.RedditService;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by jacob-tabak on 1/2/14.
 */
@EService
public class RedditHeadlinesExtension extends DashClockExtension {
    @StringRes(R.string.open_link)
    String openLink;

    @StringRes(R.string.open_comments)
    String openComments;

    @StringRes(R.string.uparrow)
    String upArrow;

    @StringRes(R.string.downarrow)
    String downArrow;

    @StringRes(R.string.provider_authority)
    String mProviderAuthority;

    @StringRes(R.string.provider_path)
    String mProviderPath;

    @Pref
    MyPrefs_ mPrefs;

    @Bean
    ImgurClient mImgurClient;

    @Bean
    RedditClient mRedditClient;

    @Bean
    UpdateService mUpdateService;

    @Bean
    MyBus mBus;

    @Override
    public void onCreate() {
        super.onCreate();
        mBus.register(this);
    }

    @Override
    public void onDestroy() {
        mBus.unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        Log.d(RedditHeadlinesApplication.TAG, "onInitialize");
/*        removeAllWatchContentUris();
        addWatchContentUris(new String[] { "content://" + mProviderAuthority + mProviderPath});*/
        setUpdateWhenScreenOn(true);
    }

    /**
     * Triggered by Otto
     * @param e
     */
    @Subscribe
    public void onManualUpdateRequested(UpdateService.UpdateDashClockEvent e) {
        onUpdateData(6);
    }

    @Override
    @Background
    protected void onUpdateData(int reason) {
        Log.d(RedditHeadlinesApplication.TAG, "onUpdateData");
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
                Log.e(RedditHeadlinesApplication.TAG, Log.getStackTraceString(e));
                return;
            }
        }

        if (response.getListing() != null) {
            RedditLink link = (RedditLink) response.getListing();

            publishUpdate(new ExtensionData()
                    .visible(true)
                    .icon(R.drawable.ic_reddit_white)
                    .status(mPrefs.sortOrder().get())
                    .expandedTitle(link.title.replace("&amp;", "&"))
                    .expandedBody(getExpandedBody(link))
                    .contentDescription("reddit")
                    .clickIntent(getClickIntent(link)));
            Log.i(RedditHeadlinesApplication.TAG, "Publishing update: " + link.title + " - " + getExpandedBody(link));

            EasyTracker.getInstance(this).send(
                    MapBuilder.createEvent(
                            EventCategory.SERVICE,
                            EventAction.PUBLISH_UPDATE,
                            link.permalink,
                            null
                    ).build());
        }
        else {
            Log.e(RedditHeadlinesApplication.TAG, "Failed to parse listings.");
        }
    }

    private Intent getClickIntent(RedditLink link) {
        Intent intent = null;
        if (mPrefs.usePreview().get() && (link.domain.contains("imgur.com") || isImage(link))) {
            intent = PreviewActivity_
                    .intent(this)
                    .mLinkName(link.name)
                    .mLinkUrl(link.url)
                    .mCommentsUrl(link.permalink)
                    .flags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .get();
        }
        else {
            Log.d(RedditHeadlinesApplication.TAG, "Not using preview or image is null");
            if (mPrefs.actionOnClick().get().equals(openLink)) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link.url));
            }
            else if (mPrefs.actionOnClick().get().equals(openComments)) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://reddit.com" + link.permalink));
            }
        }
        Log.d(RedditHeadlinesApplication.TAG, "Exported intent: " + intent.toUri(Intent.URI_INTENT_SCHEME));
        return intent;
    }

    private boolean isImage(RedditLink link) {
        boolean isImage = false;
        isImage |= link.url.endsWith(".jpg");
        isImage |= link.url.endsWith(".jpeg");
        isImage |= link.url.endsWith(".png");
        isImage |= link.url.endsWith(".gif");
        isImage |= link.url.endsWith(".webp");
        return isImage;
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
