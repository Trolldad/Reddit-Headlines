package com.trolldad.dashclock.redditheadlines.fragment;

import android.app.Fragment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.trolldad.dashclock.redditheadlines.R;
import com.trolldad.dashclock.redditheadlines.RedditHeadlinesApplication;
import com.trolldad.dashclock.redditheadlines.imgur.ImgurClient;
import com.trolldad.dashclock.redditheadlines.imgur.ImgurImage;
import com.trolldad.dashclock.redditheadlines.imgur.ImgurImageResponse;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * Created by jacob-tabak on 1/19/14.
 */
@EFragment(R.layout.fragment_imgur_image)
@OptionsMenu(R.menu.menu_image_fragment)
public class ImgurImageFragment extends Fragment {
    @FragmentArg
    String mImageId;

    @FragmentArg
    ImgurImage mImage;

    @Bean
    ImgurClient mImgurClient;

    @ViewById(R.id.imageview)
    ImageViewTouch mImageView;

    @ViewById(R.id.webview)
    WebView mWebView;

    @StringRes(R.string.thumb_size)
    String mThumbSize;

    @StringRes(R.string.bounding_html)
    String mBoundingHtml;

    @AfterViews
    void init() {
        if (mImage == null) {
            loadImageInfo();
        }
        else {
            mImageId = mImage.id;
            displayImage();
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

    @Background
    void loadImageInfo() {
        try {
            ImgurImageResponse response = mImgurClient.getService().imageInfo(mImageId);
            mImage = response.getImage();
            displayImage();
        }
        catch (Exception e) {
            Log.e(RedditHeadlinesApplication.TAG, Log.getStackTraceString(e));
            RedditHeadlinesApplication.toast("Unable to load image");
        }
    }

    @UiThread
    void displayImage() {
        if (mImage != null && mImageView != null && mWebView != null) {
            Picasso.with(mImageView.getContext())
                    .load(mImage.getThumb(mThumbSize))
                    .placeholder(R.drawable.ic_content_picture)
                    .noFade()
                    .into(mImageView, new ImageLoadedCallback());
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

    @OptionsItem(R.id.menu_hq)
    void onHighQualityClicked() {
        // If the image is small enough to be rendered, render it, otherwise grab a huge thumbnail
        String imageUrl;
        if (mImage != null && mImage.width < 2048 && mImage.height < 2048) {
            imageUrl = mImage.link;
        }
        else {
            imageUrl = mImage.getThumb("h");
        }
        RedditHeadlinesApplication.toast("Getting HQ image...");
        Picasso.with(mImageView.getContext())
                .load(imageUrl)
                .placeholder(mImageView.getDrawable())
                .into(mImageView);
    }
}
