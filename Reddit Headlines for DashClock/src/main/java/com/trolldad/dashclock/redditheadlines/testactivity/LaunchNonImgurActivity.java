package com.trolldad.dashclock.redditheadlines.testactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.trolldad.dashclock.redditheadlines.activity.PreviewActivity_;

/**
 * Created by jacob-tabak on 1/28/14.
 */
public class LaunchNonImgurActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = PreviewActivity_.intent(this)
                .mCommentsUrl("http://www.reddit.com/r/AdviceAnimals/comments/1wdqd3/after_my_second_paper_jam_in_the_office_today_i/")
                .mLinkName("t3_1wdqd3")
                .mLinkUrl("http://www.livememe.com/qk7bcf5.jpg")
                .get();
        startActivity(intent);
        finish();
    }
}
