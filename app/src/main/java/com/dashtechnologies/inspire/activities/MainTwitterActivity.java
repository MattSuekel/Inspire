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

package com.dashtechnologies.inspire.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doctoror.particlesdrawable.ParticlesDrawable;

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
import com.dashtechnologies.inspire.retrofit.ApiKeys;
import com.dashtechnologies.inspire.retrofit.OAuthToken;
import com.dashtechnologies.inspire.retrofit.OnExecuteCallListener;
import com.dashtechnologies.inspire.retrofit.Tweet;
import com.dashtechnologies.inspire.retrofit.TwitterApiClient;

public class MainTwitterActivity extends AppCompatActivity implements OnExecuteCallListener, View.OnClickListener {

    private TwitterApiClient twitterApi;
    private OAuthToken token;
    private Call<OAuthToken> oAuthTokenCall;
    private Call<List<Tweet>> getTweetsCall;
    private String credentials = Credentials.basic(ApiKeys.CONSUMER_KEY, ApiKeys.CONSUMER_SECRET);

    private List<Tweet> tweets;
    private Random random = new Random();

    private TextView mDepressingThoughtTxtView;
    private LinearLayout mLinearLayout;
    private ParticlesDrawable mDrawable = new ParticlesDrawable();
    private ProgressBar mProgressBar;

    private ConnectivityManager connMgr;
    private NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppSettings.setDarkTheme(this);
        setContentView(R.layout.activity_maintwitter);

        boolean usingDarkTheme = AppSettings.isDarkThemeTurnedOn(this);
        setParticlesOnBackground(usingDarkTheme);
        settingUpViews();

        createTwitterApiClient();

        oAuthTokenCall = twitterApi.postCredentials(TwitterApiClient.CLIENT_CREDENTIALS);
        getTweetsCall = twitterApi.getTweet(TwitterApiClient.USER_ID, TwitterApiClient.COUNT);

        showProgressBar(true);

        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            oAuth();
        } else {
            showProgressBar(false);
            mDepressingThoughtTxtView.setText(R.string.no_internet);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDrawable.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDrawable.stop();
    }

    @Override
    public void onExecuted() {
        showTweet();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings:
                Intent settingsIntent = new Intent(MainTwitterActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mDepressingThoughtTxtView.getText());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                break;
            case R.id.linearLayout:
                showTweet();
                break;
        }
    }

    private void createTwitterApiClient() {
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
    }

    private void oAuth() {
        oAuthTokenCall.enqueue(new Callback<OAuthToken>() {
            @Override
            public void onResponse(Call<OAuthToken> call, Response<OAuthToken> response) {
                if (response.isSuccessful()) {
                    token = response.body();
                    onExecuted();
                    showProgressBar(false);
                }
            }

            @Override
            public void onFailure(Call<OAuthToken> call, Throwable t) {
                showProgressBar(false);
                t.printStackTrace();
                Toast.makeText(MainTwitterActivity.this, "Failure while requesting token: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showTweet() {
        showProgressBar(true);

        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            getTweetsCall.clone().enqueue(new Callback<List<Tweet>>() {
                @Override
                public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
                    tweets = response.body();
                    if (tweets != null) {
                        int index = random.nextInt(tweets.size());
                        mDepressingThoughtTxtView.setText(tweets.get(index).getText());
                    } else {
                        Toast.makeText(MainTwitterActivity.this, "NullPointerException", Toast.LENGTH_SHORT).show();
                    }
                    showProgressBar(false);
                }

                @Override
                public void onFailure(Call<List<Tweet>> call, Throwable t) {
                    showProgressBar(false);
                    Log.e(getClass().getSimpleName(), t.getMessage());
                    Toast.makeText(MainTwitterActivity.this, "Fail while requesting depressing thoughts: " + t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            showProgressBar(false);
            mDepressingThoughtTxtView.setText(R.string.no_internet);
        }
    }

    private void settingUpViews() {
        mDepressingThoughtTxtView = (TextView)findViewById(R.id.thought);
        mDepressingThoughtTxtView.setTypeface(AppSettings.font(this));
        mDepressingThoughtTxtView.setTextSize(TypedValue.COMPLEX_UNIT_SP, AppSettings.textSize(this));
        findViewById(R.id.share).setOnClickListener(this);
        findViewById(R.id.settings).setOnClickListener(this);
        mLinearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        mLinearLayout.setOnClickListener(this);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
    }

    private void setParticlesOnBackground(boolean usingDarkTheme) {
        if (AppSettings.isBackgroundWithParticles(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!usingDarkTheme) {
                    mDrawable.setDotColor(getColor(R.color.colorParticles));
                    mDrawable.setLineColor(getColor(R.color.colorPrimaryLight));
                } else {
                    mDrawable.setDotColor(getColor(R.color.colorParticlesD));
                    mDrawable.setLineColor(getColor(R.color.colorPrimaryLightD));
                }
            } else {
                if (!usingDarkTheme) {
                    mDrawable.setDotColor(getResources().getColor(R.color.colorParticles));
                    mDrawable.setLineColor(getResources().getColor(R.color.colorPrimaryLight));
                } else {
                    mDrawable.setDotColor(getResources().getColor(R.color.colorParticlesD));
                    mDrawable.setLineColor(getResources().getColor(R.color.colorPrimaryLightD));
                }
            }
            findViewById(R.id.relativeLayout).setBackground(mDrawable);
        }
    }

    private void showProgressBar(boolean show) {
        if (show && mProgressBar.getVisibility() == View.GONE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mLinearLayout.setVisibility(View.GONE);
        } else if (!show && mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.GONE);
            mLinearLayout.setVisibility(View.VISIBLE);
        }
    }
}
