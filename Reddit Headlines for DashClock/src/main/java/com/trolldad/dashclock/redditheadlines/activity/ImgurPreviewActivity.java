package com.trolldad.dashclock.redditheadlines.activity;

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

import com.trolldad.dashclock.redditheadlines.R;
import com.trolldad.dashclock.redditheadlines.RedditHeadlinesApplication;
import com.trolldad.dashclock.redditheadlines.fragment.ImgurAlbumFragment_;
import com.trolldad.dashclock.redditheadlines.fragment.ImgurImageFragment_;
import com.trolldad.dashclock.redditheadlines.imgur.ImgurClient;
import com.trolldad.dashclock.redditheadlines.reddit.RedditClient;
import com.trolldad.dashclock.redditheadlines.reddit.RedditLink;
import com.trolldad.dashclock.redditheadlines.reddit.RedditListingsResponse;
import com.trolldad.dashclock.redditheadlines.view.FontHelper;

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
import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;

/**
 * Created by jacob-tabak on 1/4/14.
 */
@EActivity(R.layout.activity_imgur_preview)
@OptionsMenu(R.menu.menu_preview)
public class ImgurPreviewActivity extends Activity {
    public static final int UPVOTE = 1;
    public static final int UNVOTE = 0;
    public static final int DOWNVOTE = -1;

    @Extra
    String mLinkName;

    @Extra
    String mLinkUrl;

    @Extra
    String mCommentsUrl;

    @StringRes(R.string.reddit_api_url)
    String mRedditUrl;

    @Bean
    RedditClient mRedditClient;

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

    private RedditLink mLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
    }

    @AfterViews
    void afterInject() {
        getLink();
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
        mCaptionTextView.setText(mLink.title);
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
        Uri uri = Uri.parse(mLinkUrl);
        List<String> pathSegments = uri.getPathSegments();
        String id = uri.getLastPathSegment();
        // clean up the id in case it has an extension or anchor
        if (id.contains(".")) {
            id = id.substring(0, id.indexOf("."));
        }
        if (id.contains("#")) {
            id = id.substring(0, id.indexOf("#"));
        }

        if (pathSegments.size() > 1 && pathSegments.get(0).equals(ImgurClient.ALBUM)) {
            return ImgurAlbumFragment_.builder().mAlbumId(id).build();
        }
        else {
            return ImgurImageFragment_.builder().mImageId(id).build();
        }
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
        if (mLink != null) {
            save (!mLink.saved);
        }
        else {
            save(true);
        }
    }

    @Click(R.id.menu_hide)
    public void onHideClicked() {
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
        return;
    }
}
