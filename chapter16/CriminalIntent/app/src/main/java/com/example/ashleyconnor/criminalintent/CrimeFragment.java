package com.example.ashleyconnor.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

  private static final String ARG_CRIME_ID = "crime_id";
  private static final String DIALOG_DATE = "DialogDate";
  private static final String DIALOG_TIME = "DialogTime";

  private static final int REQUEST_DATE = 0;
  private static final int REQUEST_TIME = 1;
  private static final int REQUEST_CONTACT = 2;
  private static final int REQUEST_PHOTO = 3;

  private Crime mCrime;
  private File mPhotoFile;
  private EditText mTitleField;
  private Button mDateButton;
  private Button mTimeButton;
  private CheckBox mSolvedCheckBox;
  private Button mSuspectButton;
  private Button mReportButton;
  private ImageButton mPhotoButton;
  private ImageView mPhotoView;

  private static final String dateFormatString = "EEEE, MMM d, yyyy";
  private static final String timeFormatString = "h:mm a";

  private SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
  private SimpleDateFormat timeFormat = new SimpleDateFormat(timeFormatString);

  public CrimeFragment() {
    super();

  }

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
    mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
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

    mReportButton = (Button) v.findViewById(R.id.crime_report);
    mReportButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
        i = Intent.createChooser(i, getString(R.string.send_report));
        startActivity(i);
      }
    });

    final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
    mSuspectButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        startActivityForResult(pickContact, REQUEST_CONTACT);
      }
    });

    if (mCrime.getSuspect() != null) {
      mSuspectButton.setText(mCrime.getSuspect());
    }

    PackageManager packageManager = getActivity().getPackageManager();
    if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
      mSuspectButton.setEnabled(false);
    }

    mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);

    final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
    mPhotoButton.setEnabled(canTakePhoto);

    if (canTakePhoto) {
      Uri uri = Uri.fromFile(mPhotoFile);
      captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
    }

    mPhotoButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivityForResult(captureImage, REQUEST_PHOTO);
      }
    });

    mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
    updatePhotoView();

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

    if (requestCode == REQUEST_CONTACT && data != null) {
      Uri contactUri = data.getData();

      String[] queryFields = new String[] {
          ContactsContract.Contacts.DISPLAY_NAME
      };

      Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

      try {
        if(c.getCount() == 0) {
          return;
        }

        c.moveToFirst();
        String suspect = c.getString(0);
        mCrime.setSuspect(suspect);
        mSuspectButton.setText(suspect);
      } finally {
        c.close();
      }
    }

    if (requestCode == REQUEST_PHOTO) {
      updatePhotoView();
    }
  }

  private void updateDate() {
    mDateButton.setText(dateFormat.format(mCrime.getDate()));
  }
  private void updateTime() {
    mTimeButton.setText(timeFormat.format(mCrime.getDate()));
  }

  private String getCrimeReport() {
    String solvedString = null;

    if(mCrime.isSolved()) {
      solvedString = getString(R.string.crime_report_solved);
    } else {
      solvedString = getString(R.string.crime_report_unsolved);
    }

    String dateString = dateFormat.format(mCrime.getDate());

    String suspect = mCrime.getSuspect();
    if(suspect == null) {
      suspect = getString(R.string.crime_report_no_suspect);
    } else {
      suspect = getString(R.string.crime_report_suspect, suspect);
    }

    String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

    return report;
  }

  private void updatePhotoView() {
    if (mPhotoFile == null || !mPhotoFile.exists()) {
      mPhotoView.setImageDrawable(null);
    } else {
      Bitmap bitmap = PictureUtilities.getScaledBitmap(mPhotoFile.getPath(), getActivity());
      mPhotoView.setImageBitmap(bitmap);
    }
  }
}
