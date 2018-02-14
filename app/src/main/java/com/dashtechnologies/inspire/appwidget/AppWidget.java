/*
 * Copyright 2017 Vladyslav Pohrebniakov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dashtechnologies.inspire.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.dashtechnologies.inspire.AppSettings;
import com.dashtechnologies.inspire.R;
import com.dashtechnologies.inspire.activities.AppWidgetConfigureActivity;
import com.dashtechnologies.inspire.activities.MainTwitterActivity;
import com.dashtechnologies.inspire.retrofit.ApiKeys;
import com.dashtechnologies.inspire.retrofit.OAuthToken;
import com.dashtechnologies.inspire.retrofit.Tweet;
import com.dashtechnologies.inspire.retrofit.TwitterApiClient;

public class AppWidget extends AppWidgetProvider {

    private static RemoteViews views;
    private TwitterApiClient twitterApi;
    private OAuthToken token;
    private Call<OAuthToken> oAuthTokenCall;
    private Call<List<Tweet>> getTweetsCall;
    private String credentials = Credentials.basic(ApiKeys.CONSUMER_KEY, ApiKeys.CONSUMER_SECRET);
    private List<Tweet> tweets;
    private Random random = new Random();

    public static void updateAppWidget(final Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {

        settingUpViews(context, appWidgetId);

        Intent intent = new Intent(context, AppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.updateButton, updatePendingIntent);

        Intent configIntent = new Intent(context, MainTwitterActivity.class);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
        views.setOnClickPendingIntent(R.id.appwidgetText, activityPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void settingUpViews(Context context, int appWidgetId) {
        CharSequence widgetText = context.getString(R.string.appwidget_text_loading);
        int backgroundColor = AppWidgetConfigureActivity.loadBackgroundColorPref(context, appWidgetId);
        int buttonColor = AppWidgetConfigureActivity.loadButtonColorPref(context, appWidgetId);
        int textColor = AppWidgetConfigureActivity.loadTextColorPref(context, appWidgetId);

        // Construct the RemoteViews object
        views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.appwidgetText, widgetText);
        views.setTextViewTextSize(R.id.appwidgetText, TypedValue.COMPLEX_UNIT_SP, AppSettings.textSizeWidget(context));
        views.setInt(R.id.relativeLayout, "setBackgroundColor", backgroundColor);
        views.setTextColor(R.id.updateButton, buttonColor);
        views.setTextColor(R.id.appwidgetText, textColor);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        startTwitterApi(context);

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            final ComponentName cn = new ComponentName(context, AppWidget.class);
            int[] appWidgetIds = mgr.getAppWidgetIds(cn);
            onUpdate(context, mgr, appWidgetIds);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            AppWidgetConfigureActivity.deleteBackgroundColorPref(context, appWidgetId);
            AppWidgetConfigureActivity.deleteButtonColorPref(context, appWidgetId);
            AppWidgetConfigureActivity.deleteTextColorPref(context, appWidgetId);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        startTwitterApi(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {
        startTwitterApi(context);
        super.onEnabled(context);
    }

    private void getTweet(final Context context) {
        getTweetsCall.clone().enqueue(new Callback<List<Tweet>>() {
            @Override
            public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
                if (response.isSuccessful()) {
                    tweets = response.body();
                    if (tweets != null && views != null) {
                        int index = random.nextInt(tweets.size());
                        String text = tweets.get(index) != null ? tweets.get(index).getText() : "null :(";
                        views.setTextViewText(R.id.appwidgetText, text);
                        final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                        final ComponentName cn = new ComponentName(context, AppWidget.class);
                        mgr.updateAppWidget(cn, views);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Tweet>> call, Throwable t) {
                Log.e(getClass().getSimpleName(), t.getMessage());
            }
        });
    }

    private void startTwitterApi(final Context context) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                        token != null ? token.getAuthorization() : credentials);

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TwitterApiClient.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        twitterApi = retrofit.create(TwitterApiClient.class);

        oAuthTokenCall = twitterApi.postCredentials(TwitterApiClient.CLIENT_CREDENTIALS);
        getTweetsCall = twitterApi.getTweet(TwitterApiClient.USER_ID, TwitterApiClient.COUNT);

        oAuthTokenCall.enqueue(new Callback<OAuthToken>() {
            @Override
            public void onResponse(Call<OAuthToken> call, Response<OAuthToken> response) {
                if (response.isSuccessful()) {
                    token = response.body();
                    getTweet(context);
                }
            }

            @Override
            public void onFailure(Call<OAuthToken> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}

