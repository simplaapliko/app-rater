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

import android.content.Context;

public class PreferencesHelper {

    private static final String PREFERENCES = "com.simplaapliko.apprater.preferences";

    private static final String PREF_FIRST_LAUNCH_DATE = "first_launch_date";
    private static final String PREF_LAUNCH_COUNT = "launch_count";
    private static final String PREF_DO_NOT_SHOW_AGAIN = "do_not_show_again";

    static final int NOT_SET = 0;

    private Context mContext;

    PreferencesHelper(Context context) {
        mContext = context;
    }

    long getFirstLaunchDate() {
        return mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getLong(PREF_FIRST_LAUNCH_DATE, NOT_SET);
    }

    PreferencesHelper setFirstLaunchDate(long date) {
        mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putLong(PREF_FIRST_LAUNCH_DATE, date)
                .apply();

        return this;
    }

    int getLaunchCount() {
        return mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getInt(PREF_LAUNCH_COUNT, NOT_SET);
    }

    PreferencesHelper setLaunchCount(int count) {
        mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putInt(PREF_LAUNCH_COUNT, count)
                .apply();

        return this;
    }

    boolean isDoNoShowAgain() {
        return mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(PREF_DO_NOT_SHOW_AGAIN, false);
    }

    PreferencesHelper setDoNoShowAgain(boolean enabled) {
        mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PREF_DO_NOT_SHOW_AGAIN, enabled)
                .apply();

        return this;
    }
}
