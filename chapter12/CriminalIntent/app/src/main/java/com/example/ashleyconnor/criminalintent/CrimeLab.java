package com.example.ashleyconnor.criminalintent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import database.CrimeBaseHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
  private static CrimeLab sCrimeLab;

  private List<Crime> mCrimes;
  private Context mContext;
  private SQLiteDatabase mDatabase;

  public static CrimeLab get(Context context) {
    if (sCrimeLab == null) {
      sCrimeLab = new CrimeLab(context);
    }
    return sCrimeLab;
  }

  private CrimeLab(Context context) {
    mContext = context.getApplicationContext();
    mDatabase = new CrimeBaseHelper(mContext)
        .getWritableDatabase();

    mCrimes = new ArrayList<>();

    for (int i = 0; i < 100; i++) {
      Crime crime = new Crime();
      crime.setTitle("crime #" + i);
      crime.setSolved(i % 2 == 0);
      mCrimes.add(crime);
    }
  }

  public List<Crime> getCrimes() {
    return mCrimes;
  }

  public Crime getCrime(UUID id) {
    for (Crime crime: mCrimes) {
      if (crime.getId().equals(id)) {
        return crime;
      }
    }
    return null;
  }
}
