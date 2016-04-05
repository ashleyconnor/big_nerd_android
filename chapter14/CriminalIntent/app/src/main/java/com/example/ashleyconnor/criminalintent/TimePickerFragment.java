package com.example.ashleyconnor.criminalintent;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimePickerFragment extends DialogFragment {

  public final static String EXTRA_TIME = "com.bignerdranch.android.criminalintent.time";
  private static final String ARG_TIME = "time";

  private TimePicker mTimePicker;

  private Date mDate;

  public static TimePickerFragment newInstance(Date date) {
    Bundle args = new Bundle();
    args.putSerializable(ARG_TIME, date);

    TimePickerFragment fragment = new TimePickerFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public Dialog onCreateDialog(Bundle saveInstanceState) {
    mDate = (Date) getArguments().getSerializable(ARG_TIME);

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(mDate);
    int hour = calendar.get(Calendar.HOUR);
    int minute = calendar.get(Calendar.MINUTE);

    View v = LayoutInflater.from(getActivity())
        .inflate(R.layout.dialog_time, null);

    mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_time_picker);
    mTimePicker.setCurrentHour(hour);
    mTimePicker.setCurrentMinute(minute);

    return new AlertDialog.Builder(getActivity())
        .setView(v)
        .setTitle(R.string.date_picker_title)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            final int hour = mTimePicker.getCurrentHour();
            final int minute = mTimePicker.getCurrentMinute();

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(mDate);
            calendar.set(Calendar.HOUR, hour);
            calendar.set(Calendar.MINUTE, minute);

            sendResult(CrimeListActivity.RESULT_OK, calendar.getTime());
          }
        })
        .create();
  }

  private void sendResult(int resultCode, Date date) {
    if(getTargetFragment() == null) {
      return;
    }

    Intent intent = new Intent();
    intent.putExtra(EXTRA_TIME, date);

    getTargetFragment()
        .onActivityResult(getTargetRequestCode(), resultCode, intent);
  }
}
