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
                .mCommentsUrl("/r/geek/comments/1w4sek/so_this_happened_in_case_you_missed_it/")
                .mLinkName("t3_1w4sek")
                .mLinkUrl("http://imgur.com/a/kKTVk")
                .get();
        startActivity(intent);
        finish();
    }
}
