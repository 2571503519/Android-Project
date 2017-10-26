package com.zjl.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Date;

/**
 * Created by lenovo on 2017/8/9.
 */

public class TimePickerFragment extends DialogFragment{
    private static final String ARG_TIME = "time";
    public static final String EXTRA_TIME = "com.zjl.criminalIntent.time";
    private TimePicker mTimePicker;

    private int mHour;
    private int mMinute;
    private Date mDate;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time, null);

        mDate = (Date) getArguments().getSerializable(ARG_TIME);

        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_picker);

        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mMinute = minute;
                mHour = hourOfDay;
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDate.setHours(mHour);
                                mDate.setMinutes(mMinute);
                                sendResult(Activity.RESULT_OK, mDate);
                            }
                        })
                .create();
    }

    public static TimePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode, Date date){
        if(getTargetFragment() == null){
            return ;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
