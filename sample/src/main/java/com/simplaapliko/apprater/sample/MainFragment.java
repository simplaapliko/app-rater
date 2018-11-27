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

package com.simplaapliko.apprater.sample;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.simplaapliko.apprater.AppRater;
import com.simplaapliko.apprater.RateAppDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment implements DialogInterface.OnClickListener {

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppRater.appLaunched(getActivity(), this, this, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        view.findViewById(R.id.show_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment dialog = new RateAppDialog.Builder()
                        .build();

                ((RateAppDialog) dialog).setOnPositiveButtonListener(MainFragment.this);
                ((RateAppDialog) dialog).setOnNegativeButtonListener(MainFragment.this);
                ((RateAppDialog) dialog).setOnNeutralButtonListener(MainFragment.this);

                dialog.show(getFragmentManager(), RateAppDialog.class.getSimpleName());
            }
        });

        view.findViewById(R.id.get_first_launch_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat format = SimpleDateFormat.getDateInstance(DateFormat.LONG);
                Date firstLaunch = AppRater.getFirstLaunchDate(getContext());
                Toast.makeText(getContext(), format.format(firstLaunch), Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.get_launch_count).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int launchCount = AppRater.getLaunchCount(getContext());
                Toast.makeText(getContext(), String.valueOf(launchCount), Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.is_do_not_show_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isTimeToRate;
                if (AppRater.isDoNotShowAgain(getContext())) {
                    isTimeToRate = "yes";
                } else {
                    isTimeToRate = "no";
                }
                Toast.makeText(getContext(), isTimeToRate, Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.is_time_to_rate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isTimeToRate;
                if (AppRater.isTimeToRate(getContext())) {
                    isTimeToRate = "yes";
                } else {
                    isTimeToRate = "no";
                }
                Toast.makeText(getContext(), isTimeToRate, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                Toast.makeText(getContext(), "onPositiveButtonClick()", Toast.LENGTH_SHORT).show();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                Toast.makeText(getContext(), "onNegativeButtonClick()", Toast.LENGTH_SHORT).show();
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                Toast.makeText(getContext(), "onNeutralButtonClick()", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
