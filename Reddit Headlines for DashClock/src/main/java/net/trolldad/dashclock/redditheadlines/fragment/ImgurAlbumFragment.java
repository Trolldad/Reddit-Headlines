package net.trolldad.dashclock.redditheadlines.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.viewpagerindicator.UnderlinePageIndicator;

import net.trolldad.dashclock.redditheadlines.R;
import net.trolldad.dashclock.redditheadlines.imgur.ImgurAlbum;
import net.trolldad.dashclock.redditheadlines.imgur.ImgurImage;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

/**
 * Created by jacob-tabak on 1/19/14.
 */
@EFragment(R.layout.fragment_album_viewpager)
public class ImgurAlbumFragment extends Fragment {
    @FragmentArg
    ImgurAlbum mAlbum;

    @ViewById(R.id.album_placeholder)
    ImageView mPlaceholder;

    @ViewById(R.id.album_viewpager)
    ViewPager mPager;

    @ViewById(R.id.album_line_page_indicator)
    UnderlinePageIndicator mIndicator;

    @InstanceState
    int mCurrentPage;

    private AlbumAdapter mAdapter;

    @AfterViews
    void init() {
        mAdapter = new AlbumAdapter(getChildFragmentManager());
        mPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mPager, mCurrentPage);
        mIndicator.setFades(false);
        updateImages(mAlbum.images);
    }

    @UiThread
    void updateImages(ImgurImage[] images) {
        mAdapter.setImages(images);
        mPager.setCurrentItem(mCurrentPage, false);
        mPlaceholder.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mCurrentPage = mPager.getCurrentItem();
    }

    static class AlbumAdapter extends FragmentStatePagerAdapter {
        private ImgurImage[] mImages = new ImgurImage[0];

        public AlbumAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setImages(ImgurImage[] images) {
            mImages = images;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mImages.length;
        }

        @Override
        public Fragment getItem(int i) {
            return ImgurImageFragment_.builder().mImage(mImages[i]).build();
        }
    }
}
