package net.trolldad.dashclock.redditheadlines.fragment;

import android.app.Fragment;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.trolldad.dashclock.redditheadlines.R;
import net.trolldad.dashclock.redditheadlines.RedditHeadlinesApplication;
import net.trolldad.dashclock.redditheadlines.imgur.ImgurClient;
import net.trolldad.dashclock.redditheadlines.imgur.ImgurImage;
import net.trolldad.dashclock.redditheadlines.preferences.MyPrefs_;
import net.trolldad.dashclock.redditheadlines.reddit.RedditLink;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.IntegerRes;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * Created by jacob-tabak on 1/19/14.
 */
@EFragment(R.layout.fragment_imgur_image)
public class ImgurImageFragment extends Fragment {
    @FragmentArg
    ImgurImage mImage;

    @FragmentArg
    RedditLink mLink;

    @Bean
    ImgurClient mImgurClient;

    @ViewById(R.id.imageview)
    ImageViewTouch mImageView;

    @ViewById(R.id.webview)
    WebView mWebView;

    @ViewById(R.id.hq_button)
    Button mHighQualityButton;

    @StringRes(R.string.thumb_suffix)
    String mThumbSize;

    @IntegerRes(R.integer.thumb_dimension)
    int mThumbDimension;

    @StringRes(R.string.bounding_html)
    String mBoundingHtml;

    @Pref
    MyPrefs_ mPrefs;

    @InstanceState
    boolean mHighQuality;

    @AfterViews
    void init() {
        mHighQuality = mPrefs.hqImages().get();
        if (mImage == null) {
            if (mLink.url.endsWith(".gif")) {
                RedditHeadlinesApplication.toast("Loading animation, hang tight!");
                String html = String.format(mBoundingHtml, mLink.url);
                mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
            }
            else {
                Picasso.with(mImageView.getContext())
                        .load(mLink.url)
                        .placeholder(R.drawable.ic_content_picture)
                        .noFade()
                        .into(mImageView);
            }
        }
        else {
            displayImgurImage();
        }
        mWebView.setBackgroundColor(0xFF000000);
        mWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (getView() != null) {
                    mImageView.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @UiThread
    void displayImgurImage() {
        if (mImageView != null && mWebView != null) {
            String resizeArg = mHighQuality ? "" : mThumbSize;
            Picasso.with(mImageView.getContext())
                    .load(mImage.getResizedImage(resizeArg))
                    .placeholder(R.drawable.ic_content_picture)
                    .noFade()
                    .into(mImageView, new ImageLoadedCallback());
            if (!mHighQuality && mImage.isHighQualityAvailable(mThumbDimension)) {
                mHighQualityButton.setVisibility(View.VISIBLE);
            }

            if (mImage.animated) {
                RedditHeadlinesApplication.toast("Loading animation, hang tight!");
                String html = String.format(mBoundingHtml, mImage.link);
                mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
            }
        }
    }

    class ImageLoadedCallback implements Callback {
        @Override
        public void onSuccess() {
            mImageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        }

        @Override
        public void onError() {
            RedditHeadlinesApplication.toast("Unable to load image");
        }
    }

    @Click(R.id.hq_button)
    void onHighQualityClicked() {
        Picasso.with(mImageView.getContext())
                .load(mImage.getResizedImage(ImgurImage.ORIGINAL_SIZE))
                .placeholder(mImageView.getDrawable())
                .into(mImageView, new HQCompleteCallback());
        mHighQualityButton.setText("Loading HQ...");
    }

    class HQCompleteCallback implements Callback {
        @Override
        public void onSuccess() {
            mHighQualityButton.setVisibility(View.GONE);
        }

        @Override
        public void onError() {
        }
    }
}
