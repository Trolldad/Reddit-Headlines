package com.trolldad.dashclock.redditheadlines.testactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.trolldad.dashclock.redditheadlines.activity.ImgurPreviewActivity_;

/**
 * Created by jacob-tabak on 1/25/14.
 */
public class LaunchAnimatedGifActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = ImgurPreviewActivity_.intent(this)
                .mCommentsUrl("/r/funny/comments/1w47lw/concentration/")
                .mLinkName("t3_1w47lw")
                .mLinkUrl("http://i.imgur.com/3ELAGhP.gif")
                .get();
        startActivity(intent);
        finish();
    }
}
