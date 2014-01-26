package com.trolldad.dashclock.redditheadlines.testactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.trolldad.dashclock.redditheadlines.activity.ImgurPreviewActivity_;

/**
 * Created by jacob-tabak on 1/25/14.
 */
public class LaunchImageActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = ImgurPreviewActivity_.intent(this)
                .mCommentsUrl("/r/wallpaper/comments/1w48wn/macintosh_30_year_anniversary_3456x2160/")
                .mLinkName("t3_1w48wn")
                .mLinkUrl("http://i.imgur.com/gCRx67r.jpg")
                .get();
        startActivity(intent);
        finish();
    }
}
