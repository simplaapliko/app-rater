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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

public class RateAppDialog extends DialogFragment {

    public static class Builder {

        // set default values
        private int mTitle = R.string.ar_dialog_rate_title;
        private int mMessage = R.string.ar_dialog_rate_message;
        private int mPositiveButton = R.string.ar_dialog_rate_positive_button_rate;
        private int mNegativeButton = R.string.ar_dialog_rate_negative_button;
        private int mNeutralButton = R.string.ar_dialog_rate_neutral_button;

        public Builder() {
        }

        public Builder setTitle(int title) {
            mTitle = title;
            return this;
        }

        public Builder setMessage(int message) {
            mMessage = message;
            return this;
        }

        public Builder setPositiveButton(int positiveButton) {
            mPositiveButton = positiveButton;
            return this;
        }

        public Builder setNegativeButton(int negativeButton) {
            mNegativeButton = negativeButton;
            return this;
        }

        public Builder setNeutralButton(int neutralButton) {
            mNeutralButton = neutralButton;
            return this;
        }

        public RateAppDialog build() {
            return newInstance(mTitle, mMessage, mPositiveButton, mNegativeButton, mNeutralButton);
        }
    }

    private static final String TITLE_KEY = "TITLE_KEY";
    private static final String MESSAGE_KEY = "MESSAGE_KEY";
    private static final String POSITIVE_BUTTON_KEY = "POSITIVE_BUTTON_KEY";
    private static final String NEGATIVE_BUTTON_KEY = "NEGATIVE_BUTTON_KEY";
    private static final String NEUTRAL_BUTTON_KEY = "NEUTRAL_BUTTON_KEY";

    private int mTitle;
    private int mMessage;
    private int mPositiveButton;
    private int mNegativeButton;
    private int mNeutralButton;

    private DialogInterface.OnClickListener mOnPositiveButtonListener;
    private DialogInterface.OnClickListener mOnNegativeButtonListener;
    private DialogInterface.OnClickListener mOnNeutralButtonListener;

    private static RateAppDialog newInstance(
            int title, int message,
            int positiveButton, int negativeButton, int neutralButton) {

        RateAppDialog fragment = new RateAppDialog();
        Bundle args = new Bundle();
        args.putInt(TITLE_KEY, title);
        args.putInt(MESSAGE_KEY, message);
        args.putInt(POSITIVE_BUTTON_KEY, positiveButton);
        args.putInt(NEGATIVE_BUTTON_KEY, negativeButton);
        args.putInt(NEUTRAL_BUTTON_KEY, neutralButton);

        fragment.setArguments(args);
        return fragment;
    }

    public RateAppDialog() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(true);

        Bundle args = getArguments();
        if (args != null) {
            mTitle = args.getInt(TITLE_KEY);
            mMessage = args.getInt(MESSAGE_KEY);
            mPositiveButton = args.getInt(POSITIVE_BUTTON_KEY);
            mNegativeButton = args.getInt(NEGATIVE_BUTTON_KEY);
            mNeutralButton = args.getInt(NEUTRAL_BUTTON_KEY);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.ar_dialog_fragment_rate_app, null);

        initUiWidgets(rootView);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        
        builder.setTitle(mTitle);

        builder.setPositiveButton(
                mPositiveButton,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AppRater.rateApp(getContext());

                        if (mOnPositiveButtonListener != null) {
                            mOnPositiveButtonListener.onClick(dialog, which);
                        }

                        dialog.dismiss();
                    }
                });

        builder.setNegativeButton(
                mNegativeButton,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AppRater.remindLater(getContext());

                        if (mOnNegativeButtonListener != null) {
                            mOnNegativeButtonListener.onClick(dialog, which);
                        }

                        dialog.dismiss();
                    }
                });

        builder.setNeutralButton(
                mNeutralButton,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AppRater.cancelReminders(getContext());

                        if (mOnNeutralButtonListener != null) {
                            mOnNeutralButtonListener.onClick(dialog, which);
                        }

                        dialog.dismiss();
                    }
                });

        builder.setView(rootView);

        return builder.create();
    }

    public DialogInterface.OnClickListener getOnPositiveButtonListener() {
        return mOnPositiveButtonListener;
    }

    public void setOnPositiveButtonListener(final DialogInterface.OnClickListener onPositiveButtonListener) {
        mOnPositiveButtonListener = onPositiveButtonListener;
    }

    public DialogInterface.OnClickListener getOnNegativeButtonListener() {
        return mOnNegativeButtonListener;
    }

    public void setOnNegativeButtonListener(final DialogInterface.OnClickListener onNegativeButtonListener) {
        mOnNegativeButtonListener = onNegativeButtonListener;
    }

    public DialogInterface.OnClickListener getOnNeutralButtonListener() {
        return mOnNeutralButtonListener;
    }

    public void setOnNeutralButtonListener(final DialogInterface.OnClickListener onNeutralButtonListener) {
        mOnNeutralButtonListener = onNeutralButtonListener;
    }

    private void initUiWidgets(View rootView) {

        TextView appNameTextView = (TextView) rootView.findViewById(R.id.message);
        appNameTextView.setText(mMessage);

    }
}