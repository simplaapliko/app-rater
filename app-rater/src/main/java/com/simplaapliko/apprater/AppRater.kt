/*
 * Copyright (C) 2015 Oleg Kan
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
package com.simplaapliko.apprater

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import java.util.Date

@Suppress("MemberVisibilityCanBePrivate")
object AppRater {
    private const val DAY = 24 * 60 * 60 * 1000

    /**
     * Check if 'rate app' dialog needs to be displayed,
     * based on the first launch date, launch count and do not show again
     * shared preferences.
     *
     * @param activity Activity
     * @param onPositiveButtonListener Positive button click listener
     * @param onNegativeButtonListener Negative button click listener
     * @param onNeutralButtonListener Neutral button click listener
     */
    @JvmStatic
    @JvmOverloads
    fun appLaunched(
        activity: Activity,
        onPositiveButtonListener: DialogInterface.OnClickListener? = null,
        onNegativeButtonListener: DialogInterface.OnClickListener? = null,
        onNeutralButtonListener: DialogInterface.OnClickListener? = null
    ) {
        val preferences = PreferencesHelper(activity)
        if (preferences.isDoNoShowAgain) {
            return
        }

        increment(activity)

        if (isTimeToRate(activity)) {
            showDialog(activity, onPositiveButtonListener, onNegativeButtonListener, onNeutralButtonListener)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showDialog(
        activity: Activity,
        onPositiveButtonListener: DialogInterface.OnClickListener? = null,
        onNegativeButtonListener: DialogInterface.OnClickListener? = null,
        onNeutralButtonListener: DialogInterface.OnClickListener? = null
    ) {
        val builder = AlertDialog.Builder(activity)
            .setCancelable(true)
            .setTitle(R.string.ar_dialog_rate_title)
            .setMessage(R.string.ar_dialog_rate_message)

        onPositiveButtonListener?.let { listener ->
            builder.setPositiveButton(
                R.string.ar_dialog_rate_positive_button_rate
            ) { dialog: DialogInterface?, which: Int ->
                rateApp(activity)
                listener.onClick(dialog, which)
            }
        }

        onNegativeButtonListener?.let { listener ->
            builder.setNegativeButton(
                R.string.ar_dialog_rate_negative_button
            ) { dialog: DialogInterface?, which: Int ->
                remindLater(activity)
                listener.onClick(dialog, which)
            }
        }

        onNeutralButtonListener?.let { listener ->
            builder.setNeutralButton(
                R.string.ar_dialog_rate_neutral_button
            ) { dialog: DialogInterface?, which: Int ->
                cancelReminders(activity)
                listener.onClick(dialog, which)
            }
        }

        builder.create()
            .show()
    }

    /**
     * Increment launch count and set first launch date
     * @param context Context
     */
    fun increment(context: Context) {
        val preferences = PreferencesHelper(context)

        // Increment the launch counter
        val launchCount = preferences.launchCount + 1
        preferences.setLaunchCount(launchCount)

        // Get date of the first launch
        val firstLaunchDate = preferences.firstLaunchDate
        if (firstLaunchDate == 0L) {
            preferences.setFirstLaunchDate(System.currentTimeMillis())
        }
    }

    @JvmStatic
    fun getFirstLaunchDate(context: Context): Date {
        val preferences = PreferencesHelper(context)
        val firstLaunchDate = preferences.firstLaunchDate
        return Date(firstLaunchDate)
    }

    @JvmStatic
    fun getLaunchCount(context: Context): Int {
        val preferences = PreferencesHelper(context)
        return preferences.launchCount
    }

    @JvmStatic
    fun isDoNotShowAgain(context: Context): Boolean {
        val preferences = PreferencesHelper(context)
        return preferences.isDoNoShowAgain
    }

    @JvmStatic
    fun isTimeToRate(context: Context): Boolean {
        val resources = context.resources
        val dayUntilPrompt = resources.getInteger(R.integer.ar_app_rater_days_until_prompt)
        val launchesUntilPrompt = resources.getInteger(R.integer.ar_app_rater_launches_until_prompt)

        val preferences = PreferencesHelper(context)
        val launchCount = preferences.launchCount
        val firstLaunchDate = preferences.firstLaunchDate

        return !preferences.isDoNoShowAgain &&
            launchCount >= launchesUntilPrompt &&
            System.currentTimeMillis() >= firstLaunchDate + dayUntilPrompt * DAY
    }

    /**
     * Open Play Store for the related application.
     */
    fun rateApp(context: Context) {
        PreferencesHelper(context)
            .setDoNoShowAgain(true)

        // create Play Store intent
        var rate = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.packageName))
        if (!isIntentCallable(context, rate)) {
            // if Play Store doesn't exist show in browser
            rate = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
            )
        }

        if (!isIntentCallable(context, rate)) {
            // if browser app doesn't exist, show toast and quit
            Toast.makeText(context, context.getString(R.string.ar_unable_to_find_google_play), Toast.LENGTH_LONG).show()
            return
        }

        if (context is Activity) {
            context.startActivity(rate)
        } else {
            val intent = Intent.createChooser(rate, null)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    /**
     * Reset launch counter.
     */
    fun remindLater(context: Context) {
        PreferencesHelper(context)
            .setLaunchCount(0)
            .setFirstLaunchDate(System.currentTimeMillis())
    }

    /**
     * Cancel reminders.
     */
    fun cancelReminders(context: Context) {
        PreferencesHelper(context)
            .setDoNoShowAgain(true)
    }

    /**
     * Check if this is a first launch
     */
    fun isFirstLaunch(context: Context): Boolean {
        return PreferencesHelper(context)
            .firstLaunchDate == 0L
    }

    private fun isIntentCallable(context: Context, intent: Intent): Boolean {
        val list = context.packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return list.size > 0
    }
}
