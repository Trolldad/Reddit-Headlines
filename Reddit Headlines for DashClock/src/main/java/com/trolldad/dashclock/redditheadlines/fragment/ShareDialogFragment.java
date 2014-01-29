package com.trolldad.dashclock.redditheadlines.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.trolldad.dashclock.redditheadlines.R;
import com.trolldad.dashclock.redditheadlines.reddit.RedditLink;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;

/**
 * Created by jacob-tabak on 1/28/14.
 */
@EFragment
public class ShareDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {
    private ArrayAdapter<String> mArrayAdapter;
    private ListView mListView;
    private AlertDialog mDialog;

    @StringArrayRes(R.array.share_methods)
    String[] mSharedMethods;

    @StringRes(R.string.share_link)
    String mShareLink;

    @StringRes(R.string.share_comments)
    String mShareComments;

    @StringRes(R.string.share_app)
    String mShareApp;

    @StringRes(R.string.reddit_base_url)
    String mRedditUrl;

    @StringRes(R.string.play_store_url)
    String mPlayStoreLink;

    @FragmentArg
    RedditLink mLink;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mListView = new ListView(getActivity());
        mListView.setOnItemClickListener(this);
        mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mSharedMethods);
        mListView.setAdapter(mArrayAdapter);
        mDialog = new AlertDialog.Builder(getActivity())
                .setTitle("What would you like to share?")
                .setView(mListView)
                .create();
        return mDialog;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mDialog != null) {
            mDialog.dismiss();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String selectedItem = mArrayAdapter.getItem(position).toString();
            if (selectedItem.equals(mShareLink)) {
                intent.putExtra(Intent.EXTRA_SUBJECT, mLink.title);
                intent.putExtra(Intent.EXTRA_TEXT, mLink.url);
            }
            else if (selectedItem.equals(mShareComments)) {
                intent.putExtra(Intent.EXTRA_SUBJECT, mLink.title);
                intent.putExtra(Intent.EXTRA_TEXT, mRedditUrl + mLink.permalink);
            }
            else if (selectedItem.equals(mShareApp)) {
                intent.putExtra(Intent.EXTRA_SUBJECT, "Reddit Headlines in the Google Play Store");
                intent.putExtra(Intent.EXTRA_TEXT, mPlayStoreLink);
            }
            startActivity(intent);
        }
    }
}
