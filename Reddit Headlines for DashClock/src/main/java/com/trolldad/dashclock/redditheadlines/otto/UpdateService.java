package com.trolldad.dashclock.redditheadlines.otto;

import android.util.Log;

import com.squareup.otto.Produce;
import com.trolldad.dashclock.redditheadlines.RedditHeadlinesApplication;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

/**
 * Created by jacob-tabak on 1/29/14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class UpdateService {
    @Bean
    MyBus mBus;

    @AfterInject
    void init() {
        mBus.register(this);
    }

    @UiThread
    public void onUpdateDashClock() {
        Log.d(RedditHeadlinesApplication.TAG, "Posting update event to service");
        mBus.post(produceEvent());
    }

    @Produce
    public UpdateDashClockEvent produceEvent() {
        return new UpdateDashClockEvent();
    }

    public class UpdateDashClockEvent { }
}
