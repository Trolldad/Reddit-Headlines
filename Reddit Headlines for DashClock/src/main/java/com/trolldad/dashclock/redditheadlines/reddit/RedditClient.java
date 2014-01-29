package com.trolldad.dashclock.redditheadlines.reddit;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.trolldad.dashclock.redditheadlines.R;
import com.trolldad.dashclock.redditheadlines.RedditHeadlinesApplication;
import com.trolldad.dashclock.redditheadlines.RedditHeadlinesExtension;
import com.trolldad.dashclock.redditheadlines.preferences.MyPrefs_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by jacob-tabak on 1/19/14.
 */
@EBean
public class RedditClient {
    @StringRes(R.string.reddit_base_url) String mRedditApiUrl;
    @Pref MyPrefs_ mPrefs;

    public RedditService getService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setServer(mRedditApiUrl)
                .setRequestInterceptor(new RedditServiceInterceptor())
                .setConverter(new GsonConverter(getGson()))
                .setLog(new RedditLog())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        return restAdapter.create(RedditService.class);
    }

    class RedditLog implements RestAdapter.Log {
        @Override
        public void log(String s) {
            Log.d("RedditLog", s);
        }
    }

    class RedditServiceInterceptor implements RequestInterceptor {

        @Override
        public void intercept(RequestFacade requestFacade) {
            try {
                requestFacade.addHeader("cookie", "reddit_session=" + URLEncoder.encode(mPrefs.cookie().get(), "UTF-8"));
                requestFacade.addHeader("X-Modhash", URLEncoder.encode(mPrefs.modHash().get(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                Log.e(RedditHeadlinesApplication.TAG, Log.getStackTraceString(e));
            }
        }
    }

    public static class DateTimeTypeConverter implements JsonDeserializer<DateTime> {
        @Override
        public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            try {
                return new DateTime(json.getAsLong() * 1000).withZoneRetainFields(DateTimeZone.UTC);
            } catch (IllegalArgumentException e) {
                Log.e(RedditHeadlinesApplication.TAG, Log.getStackTraceString(e));
                return null;
            }
        }
    }

    public static Gson getGson() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeAdapter(RedditListing.class, new ListingTypeConverter())
                .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();
        return gson;
    }

    public static class ListingTypeConverter implements JsonDeserializer<RedditListing> {

        @Override
        public RedditListing deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject object = jsonElement.getAsJsonObject();
            String typeCode = object.get("kind").getAsString();
            return getGson().fromJson(object.get("data"), RedditType.getType(typeCode));
        }
    }
}
