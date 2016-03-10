package com.example.ashleyconnor.criminalintent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import java.text.SimpleDateFormat;

public class CrimeFragment extends Fragment {
  private Crime mCrime;
  private EditText mTitleField;
  private Button mDateButton;
  private CheckBox mSolvedCheckBox;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mCrime = new Crime();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_crime, container, false);

    mTitleField = (EditText)v.findViewById(R.id.crime_title);

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

    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy");

    mDateButton.setText(dateFormat.format(mCrime.getDate()));
    mDateButton.setEnabled(false);

    mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
    mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mCrime.setSolved(isChecked);
      }
    });

    return v;
  }
}
