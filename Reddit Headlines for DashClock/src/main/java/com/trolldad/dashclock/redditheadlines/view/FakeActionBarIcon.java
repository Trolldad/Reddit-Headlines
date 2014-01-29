package com.trolldad.dashclock.redditheadlines.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.trolldad.dashclock.redditheadlines.R;
import com.trolldad.dashclock.redditheadlines.RedditHeadlinesApplication;

/**
 * Created by jacob-tabak on 1/25/14.
 */
public class FakeActionBarIcon extends ImageView implements View.OnLongClickListener {
    public FakeActionBarIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        int pixels = (int) getResources().getDimension(R.dimen.fake_actionbar_icon_padding);
        setPadding(pixels, 0, pixels, 0);
        setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        RedditHeadlinesApplication.toast(getContentDescription().toString());
        return true;
    }
}
