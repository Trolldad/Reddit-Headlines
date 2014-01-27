package com.trolldad.dashclock.redditheadlines.testactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.trolldad.dashclock.redditheadlines.activity.ImgurPreviewActivity_;

/**
 * Created by jacob-tabak on 1/26/14.
 */
public class LaunchAmpersandActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = ImgurPreviewActivity_.intent(this)
                .mCommentsUrl("/r/pics/comments/1w5wjo/grilled_cheese_mac_cheese_in_shots_of_tomato_soup/")
                .mLinkName("t3_1w5wjo")
                .mLinkUrl("http://i.imgur.com/mUyhf6x.jpg")
                .get();
        startActivity(intent);
        finish();
    }
}
