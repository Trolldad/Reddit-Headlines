package com.trolldad.dashclock.redditheadlines.testactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.trolldad.dashclock.redditheadlines.activity.ImgurPreviewActivity_;

/**
 * Created by jacob-tabak on 1/25/14.
 */
public class LaunchGalleryActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = ImgurPreviewActivity_.intent(this)
                .mCommentsUrl("/r/wisconsin/comments/1w57xn/around_wisconsin/")
                .mLinkName("t3_1w57xn")
                .mLinkUrl("http://imgur.com/gallery/8mjHh")
                .get();
        startActivity(intent);
        finish();
    }
}
