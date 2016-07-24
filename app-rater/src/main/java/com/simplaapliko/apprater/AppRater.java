/*
 * Copyright (C) 2015 Oleg Kan, @Simplaapliko
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

package com.simplaapliko.apprater;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.util.List;

public final class AppRater {

    private static final int DAY = 24 * 60 * 60 * 1000;

    /**
     * Check if 'rate app' dialog needs to be displayed,
     * based on the first launch date, launch count and do not show again
     * shared preferences.
     *
     * @param activity
     */

    public static void appLaunched(FragmentActivity activity) {
        appLaunched(activity, null, null, null);
    }

    /**
     * Check if 'rate app' dialog needs to be displayed,
     * based on the first launch date, launch count and do not show again
     * shared preferences.
     *
     * @param activity
     * @param onPositiveButtonListener Positive button click listener
     * @param onNegativeButtonListener Negative button click listener
     * @param onNeutralButtonListener Neutral button click listener
     */
    public static void appLaunched(
            FragmentActivity activity,
            DialogInterface.OnClickListener onPositiveButtonListener,
            DialogInterface.OnClickListener onNegativeButtonListener,
            DialogInterface.OnClickListener onNeutralButtonListener) {

        int dayUntilPrompt = activity.getResources().getInteger(R.integer.ar_app_rater_days_until_prompt);
        int launchesUntilPrompt = activity.getResources().getInteger(R.integer.ar_app_rater_launches_until_prompt);

        PreferencesHelper preferences = new PreferencesHelper(activity);

        if (preferences.isDoNoShowAgain()) {
            return;
        }

        // Increment the launch counter
        int launchCount = preferences.getLaunchCount() + 1;
        preferences.setLaunchCount(launchCount);

        // Get date of the first launch
        long firstLaunchDate = preferences.getFirstLaunchDate();
        if (firstLaunchDate == 0) {
            preferences.setFirstLaunchDate(System.currentTimeMillis());
        }

        // Wait at least n days before opening
        if (launchCount >= launchesUntilPrompt &&
                (System.currentTimeMillis() >= firstLaunchDate + dayUntilPrompt * DAY)) {

            RateAppDialog dialog = new RateAppDialog.Builder()
                    .build();

            if (onPositiveButtonListener != null) {
                dialog.setOnPositiveButtonListener(onPositiveButtonListener);
            }

            if (onNegativeButtonListener != null) {
                dialog.setOnNegativeButtonListener(onNegativeButtonListener);
            }

            if (onNeutralButtonListener != null) {
                dialog.setOnNeutralButtonListener(onNeutralButtonListener);
            }

            dialog.show((activity).getSupportFragmentManager(), null);
        }
    }

    /**
     * Open Play Store for the related application.
     */
    public static void rateApp(Context context) {

        new PreferencesHelper(context)
                .setDoNoShowAgain(true);

        // create Play Store intent
        Intent rate = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName()));

        if (!isIntentCallable(context, rate)) {
            // if Play Store doesn't exist show in browser
            rate = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName()));
        }

        if (!isIntentCallable(context, rate)) {
            // if browser app doesn't exist, show toast and quit
            Toast.makeText(context, context.getString(R.string.ar_unable_to_find_google_play), Toast.LENGTH_LONG).show();
            return;
        }

        if (context instanceof Activity) {
            context.startActivity(rate);
        } else {
            Intent intent = Intent.createChooser(rate, null);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * Reset launch counter.
     */
    public static void remindLater(Context context) {
        new PreferencesHelper(context)
                .setLaunchCount(0)
                .setFirstLaunchDate(System.currentTimeMillis());
    }

    /**
     * Cancel reminders.
     */
    public static void cancelReminders(Context context) {
        new PreferencesHelper(context)
                .setDoNoShowAgain(true);
    }

    /**
     * Check if this is a first launch
     */
    public static boolean isFirstLaunch(Context context) {
        return new PreferencesHelper(context)
                .getFirstLaunchDate() == 0;
    }

    private static boolean isIntentCallable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
