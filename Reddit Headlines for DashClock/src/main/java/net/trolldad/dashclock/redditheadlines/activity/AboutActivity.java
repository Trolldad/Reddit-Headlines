package net.trolldad.dashclock.redditheadlines.activity;

import android.app.Activity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import net.trolldad.dashclock.redditheadlines.R;
import net.trolldad.dashclock.redditheadlines.view.FontHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FromHtml;
import org.androidannotations.annotations.ViewById;

/**
 * Created by jacob-tabak on 1/26/14.
 */
@EActivity(R.layout.activity_about)
public class AboutActivity extends Activity {
    @ViewById(R.id.about_textview)
    @FromHtml(R.string.about_html)
    TextView mAboutTextView;

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @AfterViews
    void setFont() {
        FontHelper.setCustomFont(mAboutTextView, getAssets());
        mAboutTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
