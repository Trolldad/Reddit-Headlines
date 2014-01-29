package net.trolldad.dashclock.redditheadlines.otto;

import com.squareup.otto.Produce;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

/**
 * Created by jacob-tabak on 1/25/14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class LoginService {
    @Bean
    MyBus mBus;

    private boolean mLastResult;

    @AfterInject
    void init() {
        mBus.register(this);
    }

    @UiThread
    public void onLoginResult(boolean success) {
        mLastResult = success;
        mBus.post(produceEvent());
    }

    @Produce
    public LoginResultEvent produceEvent() {
        return new LoginResultEvent(mLastResult);
    }

    public class LoginResultEvent {
        public final boolean result;

        public LoginResultEvent(boolean result) {
            this.result = result;
        }
    }
}
