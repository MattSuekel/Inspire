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

package com.dashtechnologies.rapidchat.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import de.psdev.licensesdialog.LicensesDialog;
import com.dashtechnologies.rapidchat.R;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twittersettings);
    }

    @Override
    public void onBackPressed() {
        navigateUpFromSameTask(this);
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            CharSequence keyLicenses = getResources().getString(R.string.key_licenses_pref);
            Preference licensesPref = getPreferenceScreen().findPreference(keyLicenses);
            licensesPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new LicensesDialog.Builder(getActivity())
                            .setNotices(R.raw.notices)
                            .build()
                            .show();

                    return false;
                }
            });

            CharSequence keyRate = getResources().getString(R.string.key_rate_pref);
            Preference ratePref = getPreferenceScreen().findPreference(keyRate);
            final String googlePlayAppLink = getResources().getString(R.string.google_play_app_link);
            ratePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayAppLink));
                    startActivity(i);
                    return true;
                }
            });

            CharSequence keyTwi = getResources().getString(R.string.key_twi_pref);
            Preference twiPref = getPreferenceScreen().findPreference(keyTwi);
            final String twitterLink = getResources().getString(R.string.twitter_depressing_thoughts_author_link);
            twiPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterLink));
                    startActivity(i);
                    return true;
                }
            });

            CharSequence keyDevPage = getResources().getString(R.string.key_dev_page_pref);
            Preference devPagePref = getPreferenceScreen().findPreference(keyDevPage);
            final String googlePlayDevPageLink = getResources().getString(R.string.google_play_dev_page_link);
            devPagePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayDevPageLink));
                    startActivity(i);
                    return true;
                }
            });
        }
    }
}
