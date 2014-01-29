package com.trolldad.dashclock.redditheadlines.imgur;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.trolldad.dashclock.redditheadlines.R;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;
import org.joda.time.DateTime;

import java.lang.reflect.Type;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by jacob-tabak on 1/18/14.
 */
@EBean
public class ImgurClient {
    public static final String ALBUM = "a";
    public static final String GALLERY = "gallery";

    @StringRes(R.string.imgur_api_url) String mImgurApiUrl;

    class ImgurRequestInterceptor implements RequestInterceptor {
        @Override
        public void intercept(RequestFacade requestFacade) {
            requestFacade.addHeader("Authorization", "Client-ID 365b54d7a0fa196");
        }
    }

    public ImgurService getService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setServer(mImgurApiUrl)
                .setRequestInterceptor(new ImgurRequestInterceptor())
                .setLog(new ImgurLog())
                .setConverter(new GsonConverter(getGson()))
                .build();
        return restAdapter.create(ImgurService.class);
    }

    class ImgurLog implements RestAdapter.Log {
        @Override
        public void log(String s) {
            Log.d("ImgurLog", s);
        }
    }

    public static Gson getGson() {
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();
        return gson;
    }

    public static class DateTimeTypeConverter implements JsonDeserializer<DateTime>, JsonSerializer<DateTime> {

        @Override
        public DateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new DateTime(jsonElement.getAsLong() * 1000);
        }

        @Override
        public JsonElement serialize(DateTime dateTime, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(dateTime.getMillis() / 1000);
        }
    }
}
