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

import android.app.Activity;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.dashtechnologies.inspire.AppSettings;
import com.dashtechnologies.inspire.R;
import com.dashtechnologies.inspire.appwidget.AppWidget;

/**
 * The configuration screen for the {@link AppWidget AppWidget} AppWidget.
 */
public class AppWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "vladyslavpohrebniakov.depressingthoughts.AppWidget";
    private static final String PREF_BACKGROUND_COLOR_KEY = "appwidgetbackground_";
    private static final String PREF_BUTTON_COLOR_KEY = "appwidgetbutton_";
    private static final String PREF_TEXT_COLOR_KEY = "appwidgettext_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    int[] colors;
    int backgroundColor;
    int buttonColor;
    int textColor;
    View.OnClickListener mAddOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = AppWidgetConfigureActivity.this;

            // When the button is clicked, store the color locally
            saveBackgroundColorPref(context, mAppWidgetId, backgroundColor);
            saveButtonColorPref(context, mAppWidgetId, buttonColor);
            saveTextColorPref(context, mAppWidgetId, textColor);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            AppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };
    private RelativeLayout mWidgetBackgroundPreview;
    private TextView mTextWidgetPreview, mButtonTextPreview;
    View.OnClickListener mPreviewOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            mTextWidgetPreview.setTextColor(textColor);
            mWidgetBackgroundPreview.setBackgroundColor(backgroundColor);
            mButtonTextPreview.setTextColor(buttonColor);
        }
    };

    public AppWidgetConfigureActivity() {
        super();
    }

    static void saveBackgroundColorPref(Context context, int appWidgetId, int color) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_BACKGROUND_COLOR_KEY + appWidgetId, color);
        prefs.apply();
    }

    static void saveButtonColorPref(Context context, int appWidgetId, int color) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_BUTTON_COLOR_KEY + appWidgetId, color);
        prefs.apply();
    }

    static void saveTextColorPref(Context context, int appWidgetId, int color) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_TEXT_COLOR_KEY + appWidgetId, color);
        prefs.apply();
    }

    public static int loadBackgroundColorPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int colorValue = prefs.getInt(PREF_BACKGROUND_COLOR_KEY + appWidgetId, -1);
        if (colorValue != -1) {
            return colorValue;
        } else {
            return Color.BLACK;
        }
    }

    public static int loadButtonColorPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int colorValue = prefs.getInt(PREF_BUTTON_COLOR_KEY + appWidgetId, -1);
        if (colorValue != -1) {
            return colorValue;
        } else {
            return Color.WHITE;
        }
    }

    public static int loadTextColorPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int colorValue = prefs.getInt(PREF_TEXT_COLOR_KEY + appWidgetId, -1);
        if (colorValue != -1) {
            return colorValue;
        } else {
            return Color.WHITE;
        }
    }

    public static void deleteBackgroundColorPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_BACKGROUND_COLOR_KEY + appWidgetId);
        prefs.apply();
    }

    public static void deleteButtonColorPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_BUTTON_COLOR_KEY + appWidgetId);
        prefs.apply();
    }

    public static void deleteTextColorPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_TEXT_COLOR_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        AppSettings.setDarkTheme(this);

        colors = new int[]{
                ContextCompat.getColor(this, R.color.colorWidgetDark),
                ContextCompat.getColor(this, R.color.colorWidgetLight),
                Color.TRANSPARENT,
                ContextCompat.getColor(this, R.color.colorWidgetTranslucentDark),
                ContextCompat.getColor(this, R.color.colorWidgetTranslucentLight),
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorWidgetYellow),
                Color.BLACK,
                Color.WHITE,
                ContextCompat.getColor(this, R.color.colorWidgetRed),
                ContextCompat.getColor(this, R.color.colorWidgetPurple),
                ContextCompat.getColor(this, R.color.colorWidgetBlue),
                ContextCompat.getColor(this, R.color.colorWidgetCyan),
                ContextCompat.getColor(this, R.color.colorWidgetTeal),
                ContextCompat.getColor(this, R.color.colorWidgetOrange),
                ContextCompat.getColor(this, R.color.colorWidgetBrown),
                ContextCompat.getColor(this, R.color.colorWidgetGrey),
                ContextCompat.getColor(this, R.color.colorWidgetBlueGrey),
                ContextCompat.getColor(this, R.color.colorWidgetIndigo),
                ContextCompat.getColor(this, R.color.colorWidgetGreen)
        };
        backgroundColor = colors[0];
        buttonColor = colors[5];
        textColor = colors[8];

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_app_widget_configure);

        settingUpViews();

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

    }

    private void settingUpViews() {
        Spinner spinnerBackgroundColor = findViewById(R.id.spinnerBackgroundColor);
        String[] backgroundColors = getResources().getStringArray(R.array.widget_colors);
        ArrayAdapter<String> adapterBackground = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, backgroundColors);
        spinnerBackgroundColor.setAdapter(adapterBackground);
        spinnerBackgroundColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView parent, View view, int pos, long id) {
                if (pos < colors.length && pos >= 0) {
                    backgroundColor = colors[pos];
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner spinnerButtonColor = findViewById(R.id.spinnerButtonColor);
        String[] buttonColors = getResources().getStringArray(R.array.widget_colors);
        ArrayAdapter<String> adapterButton = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, buttonColors);
        spinnerButtonColor.setAdapter(adapterButton);
        spinnerButtonColor.setSelection(5);
        spinnerButtonColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView parent, View view, int pos, long id) {
                if (pos < colors.length && pos >= 0) {
                    buttonColor = colors[pos];
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner spinnerTextColor = findViewById(R.id.spinnerTextColor);
        String[] textColors = getResources().getStringArray(R.array.widget_colors);
        ArrayAdapter<String> adapterText = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, textColors);
        spinnerTextColor.setAdapter(adapterText);
        spinnerTextColor.setSelection(8);
        spinnerTextColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView parent, View view, int pos, long id) {
                if (pos < colors.length && pos >= 0) {
                    textColor = colors[pos];
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mButtonTextPreview = findViewById(R.id.preview_button_text);
        mTextWidgetPreview = findViewById(R.id.preview_text);
        mWidgetBackgroundPreview = findViewById(R.id.preview_background);
        LinearLayout previewArea = findViewById(R.id.preview_area);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        previewArea.setBackground(wallpaperDrawable);

        findViewById(R.id.addBtn).setOnClickListener(mAddOnClickListener);
        findViewById(R.id.previewBtn).setOnClickListener(mPreviewOnClickListener);
    }
}

