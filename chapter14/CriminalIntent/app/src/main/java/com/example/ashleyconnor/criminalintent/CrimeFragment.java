package com.example.ashleyconnor.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TimePicker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

  private static final String ARG_CRIME_ID = "crime_id";
  private static final String DIALOG_DATE = "DialogDate";
  private static final String DIALOG_TIME = "DialogTime";

  private static final int REQUEST_DATE = 0;
  private static final int REQUEST_TIME = 1;

  private Crime mCrime;
  private EditText mTitleField;
  private Button mDateButton;
  private Button mTimeButton;
  private CheckBox mSolvedCheckBox;
  private SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy");
  private SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");

  public static CrimeFragment newInstance(UUID crimeId) {
    Bundle args = new Bundle();
    args.putSerializable(ARG_CRIME_ID, crimeId);

    CrimeFragment fragment = new CrimeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
    mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
  }

  @Override public void onPause() {
    super.onPause();

    CrimeLab.get(getActivity())
        .updateCrime(mCrime);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_crime, container, false);

    mTitleField = (EditText)v.findViewById(R.id.crime_title);
    mTitleField.setText(mCrime.getTitle());

    mTitleField.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //left blank
      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        mCrime.setTitle(s.toString());
      }

      @Override public void afterTextChanged(Editable s) {
        //left blank
      }
    });

    mDateButton = (Button)v.findViewById(R.id.crime_date);
    mTimeButton = (Button)v.findViewById(R.id.crime_time);
    updateDate();
    updateTime();

    mDateButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        FragmentManager manager = getFragmentManager();
        DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
        dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
        dialog.show(manager, DIALOG_DATE);
      }
    });

    mTimeButton.setOnClickListener(new View.OnClickListener() {
     @Override
      public void onClick(View v) {
       FragmentManager manager = getFragmentManager();
       TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
       dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
       dialog.show(manager, DIALOG_TIME);
     }
   });

    mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
    mSolvedCheckBox.setChecked(mCrime.isSolved());
    mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mSolvedCheckBox.setChecked(isChecked);
        mCrime.setSolved(isChecked);
      }
    });

    return v;
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Activity.RESULT_OK) {
      return;
    }

    if (requestCode == REQUEST_DATE) {
      Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
      mCrime.setDate(date);
      updateDate();
    }

    if (requestCode == REQUEST_TIME) {
      Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
      mCrime.setDate(date);
      updateTime();
    }
  }

  private void updateDate() {
    mDateButton.setText(dateFormat.format(mCrime.getDate()));
  }
  private void updateTime() {
    mTimeButton.setText(timeFormat.format(mCrime.getDate()));
  }
}
