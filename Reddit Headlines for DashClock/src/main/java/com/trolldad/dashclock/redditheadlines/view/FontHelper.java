package com.trolldad.dashclock.redditheadlines.view;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by jacob-tabak on 1/19/14.
 */
public class FontHelper {
    private static Typeface regular;
    private static Typeface bold;
    private static Typeface light;

    public static final String TAG_NORMAL = "regular";
    public static final String TAG_BOLD = "bold";
    public static final String TAG_LIGHT = "light";

    public static void setCustomFont(View topView, AssetManager assetsManager) {
        if (regular == null || bold == null || light == null) {
            regular = Typeface.createFromAsset(assetsManager, "Roboto-Regular.ttf");
            bold = Typeface.createFromAsset(assetsManager, "Roboto-Bold.ttf");
            light = Typeface.createFromAsset(assetsManager, "Roboto-Light.ttf");
        }

        if (topView instanceof ViewGroup) {
            processViewGroup((ViewGroup) topView);
        } else if (topView instanceof TextView) {
            setCustomFont((TextView) topView);
        }
    }

    private static void processViewGroup(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            final View c = viewGroup.getChildAt(i);
            if (c instanceof TextView) {
                setCustomFont((TextView) c);
            } else if (c instanceof ViewGroup) {
                processViewGroup((ViewGroup) c);
            }
        }
    }

    private static void setCustomFont(TextView textView) {
        Object tag = textView.getTag();
        if (tag instanceof String) {
            if (tag.equals(TAG_BOLD)) {
                textView.setTypeface(bold);
                return;
            }
            else if (tag.equals(TAG_NORMAL)) {
                textView.setTypeface(regular);
            }
            else {
                textView.setTypeface(light);
            }
        }
        else {
            textView.setTypeface(light);
        }

    }
}
