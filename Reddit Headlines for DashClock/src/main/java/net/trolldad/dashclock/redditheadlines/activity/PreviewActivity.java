package net.trolldad.dashclock.redditheadlines.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.squareup.otto.Subscribe;

import net.trolldad.dashclock.redditheadlines.R;
import net.trolldad.dashclock.redditheadlines.RedditHeadlinesApplication;
import net.trolldad.dashclock.redditheadlines.analytics.EventAction;
import net.trolldad.dashclock.redditheadlines.analytics.EventCategory;
import net.trolldad.dashclock.redditheadlines.cache.ContentCache;
import net.trolldad.dashclock.redditheadlines.fragment.ImgurAlbumFragment_;
import net.trolldad.dashclock.redditheadlines.fragment.ImgurImageFragment_;
import net.trolldad.dashclock.redditheadlines.fragment.LoginDialogFragment_;
import net.trolldad.dashclock.redditheadlines.fragment.ShareDialogFragment_;
import net.trolldad.dashclock.redditheadlines.otto.LoginService;
import net.trolldad.dashclock.redditheadlines.otto.MyBus;
import net.trolldad.dashclock.redditheadlines.otto.UpdateService;
import net.trolldad.dashclock.redditheadlines.preferences.MyPrefs_;
import net.trolldad.dashclock.redditheadlines.reddit.RedditClient;
import net.trolldad.dashclock.redditheadlines.reddit.RedditLink;
import net.trolldad.dashclock.redditheadlines.reddit.RedditListingsResponse;
import net.trolldad.dashclock.redditheadlines.view.FontHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created by jacob-tabak on 1/4/14.
 */
@EActivity(R.layout.activity_imgur_preview)
@OptionsMenu(R.menu.menu_preview)
public class PreviewActivity extends Activity {
    public static final int UPVOTE = 1;
    public static final int UNVOTE = 0;
    public static final int DOWNVOTE = -1;

    @Extra
    String mLinkName;

    @Extra
    String mLinkUrl;

    @Extra
    String mCommentsUrl;

    @StringRes(R.string.reddit_base_url)
    String mRedditUrl;

    @StringRes(R.string.play_store_url)
    String mPlayStoreUrl;

    @StringRes(R.string.provider_authority)
    String mProviderAuthority;

    @StringRes(R.string.provider_path)
    String mProviderPath;

    @Bean
    RedditClient mRedditClient;

    @Bean
    MyBus mBus;

    @Bean
    UpdateService mUpdateService;

    @InstanceState
    boolean mFullscreen;

    @ViewById(R.id.preview_detail_container)
    ViewGroup mDetailContainer;

    @ViewById(R.id.preview_caption)
    TextView mCaptionTextView;

    @ViewById(R.id.preview_score)
    TextView mScoreTextView;

    @ViewById(R.id.preview_upvote_score)
    TextView mUpvoteTextView;

    @ViewById(R.id.preview_downvote_score)
    TextView mDownvoteTextView;

    @ViewById(R.id.preview_subtitle)
    TextView mSubtitleTextView;

    @ViewById(R.id.menu_save)
    ImageView mSaveButton;

    @ViewById(R.id.menu_upvote)
    ImageView mUpvoteButton;

    @ViewById(R.id.menu_comments)
    ImageView mCommentsButton;

    @ViewById(R.id.menu_downvote)
    ImageView mDownvoteButton;

    @ViewById(R.id.menu_hide)
    ImageView mHideButton;

    @ViewById(R.id.menu_link)
    ImageView mLinkButton;

    @ViewById(R.id.preview_fragment_container)
    FrameLayout mFragmentContainer;

    @InstanceState
    boolean mFragmentAdded;

    @InstanceState
    RedditLink mLink;

    @InstanceState
    boolean mLinkLoadedFromCache;

    @Bean
    ContentCache mContentCache;

    @Pref
    MyPrefs_ mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Action bar overlay required in conjunction with a margin view to smooth the animation of maximizing
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        if (!mLinkLoadedFromCache) {
            mLink = mContentCache.link;
            onLinkReceived();
            mLinkLoadedFromCache = true;
        }
    }

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

    @Override
    protected void onResume() {
        super.onResume();
        mBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @AfterViews
    void afterInject() {
        onLinkReceived();
        if (!mFragmentAdded) {
            getFragmentManager().beginTransaction().add(R.id.preview_fragment_container, getFragment(), "main").commit();
            mFragmentAdded = true;
        }
    }

    @AfterViews
    void initViews() {
        mDetailContainer.setVisibility(View.GONE);
        if (mFullscreen) {
            onFullscreenClicked();
        }
        FontHelper.setCustomFont(findViewById(android.R.id.content), getAssets());
    }

    @Subscribe
    public void onLoginStatusUpdated(LoginService.LoginResultEvent e) {
        getLink();
    }

    @Background
    void getLink() {
        try {
            RedditListingsResponse response = mRedditClient.getService().byName(mLinkName);
            mLink = (RedditLink) response.getListing();
            onLinkReceived();
        }
        catch (Exception e) {
            Log.e(RedditHeadlinesApplication.TAG, Log.getStackTraceString(e));
            RedditHeadlinesApplication.toast("Unable to load link.  Please try again later.");
        }
        mUpdateService.onUpdateDashClock();
    }

    @UiThread
    void onLinkReceived() {
        PrettyTime prettyTime = new PrettyTime();
        String subtitle = String.format(
                getString(R.string.preview_subtitle_template),
                prettyTime.format(mLink.created_utc.toDate()),
                mLink.author
        );

        invalidateOptionsMenu();
        mCaptionTextView.setText(mLink.title.replace("&amp;", "&"));
        mScoreTextView.setText(mLink.score);
        mUpvoteTextView.setText(mLink.ups);
        mDownvoteTextView.setText(mLink.downs);
        mSubtitleTextView.setText(subtitle);
        mDetailContainer.setVisibility(View.VISIBLE);
    }

    @UiThread
    void invalidateFakeActionBar() {
        if (mLink != null) {
            if (mLink.likes != null) {
                mUpvoteButton.setImageResource(
                        mLink.likes ?
                                R.drawable.ic_action_upvote_checked :
                                R.drawable.ic_action_upvote_unchecked

                );
                mDownvoteButton.setImageResource(
                        mLink.likes ?
                                R.drawable.ic_action_downvote_unchecked:
                                R.drawable.ic_action_downvote_checked
                );
            }
            else {
                mUpvoteButton.setImageResource(R.drawable.ic_action_upvote_unchecked);
                mDownvoteButton.setImageResource(R.drawable.ic_action_downvote_unchecked);
            }

            mHideButton.setImageResource(
                    mLink.hidden ?
                            R.drawable.ic_action_hide_checked :
                            R.drawable.ic_action_hide_unchecked
            );
            mSaveButton.setImageResource(
                    mLink.saved ?
                            R.drawable.ic_action_save_checked :
                            R.drawable.ic_action_save_unchecked
            );
        }
    }

    private Fragment getFragment() {
        String action;
        Fragment fragment;
        if (mContentCache.album != null) {
            fragment = ImgurAlbumFragment_.builder().mAlbum(mContentCache.album).build();
            action = EventAction.VIEW_ALBUM;
        }
        else {
            fragment = ImgurImageFragment_.builder().mImage(mContentCache.image).mLink(mLink).build();
            action = EventAction.VIEW_IMAGE;

        }
        EasyTracker.getInstance(this).send(
                MapBuilder.createEvent(
                        EventCategory.CONTENT,
                        action,
                        mLinkUrl,
                        null
                ).build()
        );
        return fragment;
    }

    @Click(R.id.menu_link)
    public void onWebClicked() {
        RedditHeadlinesApplication.toast("Opening link...");
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mLinkUrl)));
    }

    @Click(R.id.menu_comments)
    public void onCommentsClicked() {
        RedditHeadlinesApplication.toast("Opening comments...");
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mRedditUrl + mCommentsUrl)));
    }

    @Click(R.id.menu_upvote)
    public void onUpvoteClicked() {
        if (!isUserLoggedIn()) {
            return;
        }
        if (mLink != null) {
            if (mLink.likes == null || mLink.likes == false) {
                mLink.likes = true;
                vote(UPVOTE);
            }
            else {
                mLink.likes = null;
                vote(UNVOTE);
            }
            invalidateFakeActionBar();
        }
        else {
            vote(UPVOTE);
        }
    }

    @Click(R.id.menu_downvote)
    public void onDownvoteClicked() {
        if (!isUserLoggedIn()) {
            return;
        }
        // This is a bit tricky because there are 3 states, up down and neutral
        if (mLink != null) {
            if (mLink.likes == null || mLink.likes == true) {
                mLink.likes = false;
                vote(DOWNVOTE);
            }
            else {
                mLink.likes = null;
                vote(UNVOTE);
            }
        }
        else {
            vote(DOWNVOTE);
        }
    }

    @Click(R.id.menu_save)
    public void onSaveClicked() {
        if (!isUserLoggedIn()) {
            return;
        }
        if (mLink != null) {
            save (!mLink.saved);
        }
        else {
            save(true);
        }
    }

    @Click(R.id.menu_hide)
    public void onHideClicked() {
        if (!isUserLoggedIn()) {
            return;
        }
        if (mLink != null) {
            hide(!mLink.hidden);
        }
        else {
            hide(true);
        }
    }

    @Background
    void vote(int direction) {
        invalidateFakeActionBar();
        mRedditClient.getService().vote(mLinkName, direction);
        getLink();
    }

    @Background
    void save(boolean save) {
        if (!isUserLoggedIn()) {
            return;
        }
        mLink.saved = save;
        invalidateFakeActionBar();
        try {
            if (save) {
                mRedditClient.getService().save(mLinkName);
            }
            else {
                mRedditClient.getService().unsave(mLinkName);
            }
        }
        catch (Exception e) {
            RedditHeadlinesApplication.toast("Unable to toggle save state");
            Log.e(RedditHeadlinesApplication.TAG, Log.getStackTraceString(e));
            mLink.saved = !save;
            invalidateOptionsMenu();
        }
        getLink();
    }

    @Background
    void hide(boolean hidden) {
        if (!isUserLoggedIn()) {
            return;
        }
        mLink.hidden = hidden;
        invalidateFakeActionBar();
        try {
            if (hidden) {
                mRedditClient.getService().hide(mLinkName);
            }
            else {
                mRedditClient.getService().unhide(mLinkName);
            }
        }
        catch (Exception e) {
            RedditHeadlinesApplication.toast("Unable to toggle hidden state");
            Log.e(RedditHeadlinesApplication.TAG, Log.getStackTraceString(e));
            mLink.hidden = !hidden;
            invalidateOptionsMenu();
        }
        getLink();
    }

    @OptionsItem(R.id.menu_preferences)
    void onPreferencesClicked() {
        startActivity(PreferencesActivity_.intent(this).get());
    }

    @OptionsItem(R.id.menu_fullscreen)
    void onFullscreenClicked() {
        mFullscreen = true;
        getActionBar().hide();
        findViewById(R.id.preview_ui_container).setVisibility(View.GONE);
        findViewById(R.id.preview_return_from_fullscreen).setVisibility(View.VISIBLE);
        findViewById(R.id.preview_fragment_container_margin).setVisibility(View.GONE);
    }

    @Click(R.id.preview_return_from_fullscreen)
    void onReturnFromFullscreenClicked() {
        mFullscreen = false;
        getActionBar().show();
        findViewById(R.id.preview_fragment_container_margin).setVisibility(View.VISIBLE);
        findViewById(R.id.preview_ui_container).setVisibility(View.VISIBLE);
        findViewById(R.id.preview_return_from_fullscreen).setVisibility(View.GONE);
    }

    @OptionsItem(R.id.menu_refresh)
    void onRefreshClicked() {
        getLink();
    }

    @OptionsItem(R.id.menu_feedback)
    void onFeedbackClicked() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","jacob@tabak.me", null));
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "RHDC Feedback (" + versionName + ")");
            startActivity(Intent.createChooser(emailIntent, "Send Feedback..."));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(RedditHeadlinesApplication.TAG, Log.getStackTraceString(e));
        }
    }

    @OptionsItem(R.id.menu_rate)
    void onRateClicked() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mPlayStoreUrl)));
    }

    @OptionsItem(R.id.menu_share)
    void onShareClicked() {
        ShareDialogFragment_.builder().mLink(mLink).build().show(getFragmentManager(), "share");
    }

    @OptionsItem(R.id.menu_daydream)
    void onDaydreamClicked() {
        try {
            // Major hack, who knows if it will continue to work?
            final Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setClassName("com.android.systemui", "com.android.systemui.Somnambulator");
            startActivity(intent);
        }
        catch (Exception e) {
            EasyTracker.getInstance(this).send(MapBuilder.createException(Log.getStackTraceString(e), false).build());
            Toast.makeText(this, "This feature is not supported on your device.", Toast.LENGTH_SHORT).show();
            Log.e(RedditHeadlinesApplication.TAG, Log.getStackTraceString(e));
        }

    }

    boolean isUserLoggedIn() {
        if (mPrefs.cookie().get().length() == 0) {
            LoginDialogFragment_.builder().build().show(getFragmentManager(), "login");
            return false;
        } else {
            return true;
        }
    }
}
