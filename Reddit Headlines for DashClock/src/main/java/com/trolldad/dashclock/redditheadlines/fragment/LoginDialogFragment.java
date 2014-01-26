package com.trolldad.dashclock.redditheadlines.fragment;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.trolldad.dashclock.redditheadlines.R;
import com.trolldad.dashclock.redditheadlines.RedditHeadlinesApplication;
import com.trolldad.dashclock.redditheadlines.RedditHeadlinesExtension;
import com.trolldad.dashclock.redditheadlines.otto.LoginService;
import com.trolldad.dashclock.redditheadlines.preferences.MyPrefs_;
import com.trolldad.dashclock.redditheadlines.reddit.RedditClient;
import com.trolldad.dashclock.redditheadlines.reddit.RedditLogin;
import com.trolldad.dashclock.redditheadlines.reddit.RedditLoginResponse;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by jacob-tabak on 1/3/14.
 */
@EFragment(R.layout.dialog_reddit_login)
public class LoginDialogFragment extends DialogFragment {
    @Pref
    MyPrefs_ mPrefs;

    @ViewById(R.id.dialog_login_username)
    EditText mUserEdit;

    @ViewById(R.id.dialog_login_password)
    EditText mPasswordEdit;

    @ViewById(R.id.dialog_login_ok_button)
    Button mLoginButton;

    @ViewById(R.id.dialog_login_cancel_button)
    Button mCancelButton;

    @Bean
    RedditClient mRedditClient;

    @Bean
    LoginService mLoginService;

    ProgressDialog mLoginProgressDialog;

    @AfterViews
    public void initViews() {
        getDialog().setTitle("Login to reddit");
        mUserEdit.setText(mPrefs.redditUser().get());
        mPasswordEdit.setText(mPrefs.redditPassword().get());
    }

    @Click(R.id.dialog_login_ok_button)
    void onLoginClick() {
        dismiss();
        mPrefs.edit()
                .redditUser().put(mUserEdit.getText().toString())
                .redditPassword().put(mPasswordEdit.getText().toString())
                .apply();

        mLoginProgressDialog = new ProgressDialog(getActivity());
        mLoginProgressDialog.setIndeterminate(true);
        mLoginProgressDialog.setTitle(getActivity().getString(R.string.logging_in));
        mLoginProgressDialog.setMessage(getActivity().getString(R.string.please_wait_while_we_log_you_in));
        mLoginProgressDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mLoginProgressDialog.show();
        performLogin();
    }

    @Click(R.id.dialog_logout_button)
    void onLogoutClick() {
        mPrefs.redditUser().put("");
        mPrefs.redditPassword().put("");
        mPrefs.cookie().put("");
        dismiss();
    }

    @Background
    void performLogin() {
        try {
            // unset the cookie to invalidate prevous session
            mPrefs.cookie().put("");
            RedditLoginResponse response = mRedditClient.getService().login(
                    mPrefs.redditUser().get(),
                    mPrefs.redditPassword().get(),
                    true,
                    "json"
            );
            if (response.json.errors.length > 0) {
                loginFailed(response.json.errors);
            }
            else {
                loginSucceeded(response.json.data);
            }
        }
        catch (Exception e) {
            Log.e(RedditHeadlinesExtension.TAG, Log.getStackTraceString(e));
            loginFailed(new String[0][0]);
        }
    }

    @UiThread
    void loginFailed(String[][] errors) {
        if (mLoginProgressDialog != null) {
            mLoginProgressDialog.dismiss();
        }
        RedditHeadlinesApplication.toast("Login Failed");
        mLoginService.onLoginResult(false);
        Log.d(RedditHeadlinesExtension.TAG, "Login failed: " + errors);
    }

    @UiThread
    void loginSucceeded(RedditLogin loginData) {
        if (mLoginProgressDialog != null) {
            mLoginProgressDialog.dismiss();
        }
        RedditHeadlinesApplication.toast("Login Successful");

        mPrefs.edit()
                .modHash().put(loginData.modhash)
                .cookie().put(loginData.cookie)
                .apply();
        mLoginService.onLoginResult(true);
        Log.d(RedditHeadlinesExtension.TAG, "Login successful");
    }

    @Click(R.id.dialog_login_cancel_button)
    void onCancelClicked() {
        dismiss();
    }
}