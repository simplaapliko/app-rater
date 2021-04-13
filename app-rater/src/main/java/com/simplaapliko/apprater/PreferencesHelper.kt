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

import android.content.Context

class PreferencesHelper(private val context: Context) {
    val firstLaunchDate: Long
        get() = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .getLong(PREF_FIRST_LAUNCH_DATE, NOT_SET.toLong())

    fun setFirstLaunchDate(date: Long): PreferencesHelper {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putLong(PREF_FIRST_LAUNCH_DATE, date)
            .apply()
        return this
    }

    val launchCount: Int
        get() = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .getInt(PREF_LAUNCH_COUNT, NOT_SET)

    fun setLaunchCount(count: Int): PreferencesHelper {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putInt(PREF_LAUNCH_COUNT, count)
            .apply()
        return this
    }

    val isDoNoShowAgain: Boolean
        get() = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .getBoolean(PREF_DO_NOT_SHOW_AGAIN, false)

    fun setDoNoShowAgain(enabled: Boolean): PreferencesHelper {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(PREF_DO_NOT_SHOW_AGAIN, enabled)
            .apply()
        return this
    }

    companion object {
        private const val PREFERENCES = "com.simplaapliko.apprater.preferences"

        private const val PREF_FIRST_LAUNCH_DATE = "first_launch_date"
        private const val PREF_LAUNCH_COUNT = "launch_count"
        private const val PREF_DO_NOT_SHOW_AGAIN = "do_not_show_again"

        private const val NOT_SET = 0
    }
}
