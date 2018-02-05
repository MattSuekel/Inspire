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

package com.dashtechnologies.rapidchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

public class AppSettings {

    public static boolean isBackgroundWithParticles(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getBoolean(context.getString(R.string.key_particles_pref), true);
    }

    public static void setDarkTheme(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean darkTheme = sharedPrefs.getBoolean(context.getString(R.string.key_themes_pref), false);
        if (darkTheme) {
            context.setTheme(R.style.AppThemeDark);
        } else {
            context.setTheme(R.style.AppThemeLight);
        }
    }

    public static boolean isDarkThemeTurnedOn(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getBoolean(context.getString(R.string.key_themes_pref), false);
    }

    public static Typeface font(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int font = Integer.parseInt(sharedPrefs.getString(context.getString(R.string.pref_key_font), "4"));
        switch (font) {
            case 1:
                return Typeface.MONOSPACE;
            case 2:
                return Typeface.create("casual", Typeface.NORMAL);
            case 3:
                return Typeface.create("cursive", Typeface.NORMAL);
            case 4:
                return Typeface.SANS_SERIF;
            case 5:
                return Typeface.create("sans-serif-condensed", Typeface.NORMAL);
            case 6:
                return Typeface.create("sans-serif-smallcaps", Typeface.NORMAL);
            case 7:
                return Typeface.SERIF;
            default:
                return Typeface.create("serif-monospace", Typeface.NORMAL);
        }
    }

    public static float textSize(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Float.parseFloat(sharedPrefs.getString(context.getString(R.string.pref_key_textsize), "24"));
    }

    public static float textSizeWidget(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Float.parseFloat(sharedPrefs.getString(context.getString(R.string.pref_key_textsize_widget), "20"));
    }

}
