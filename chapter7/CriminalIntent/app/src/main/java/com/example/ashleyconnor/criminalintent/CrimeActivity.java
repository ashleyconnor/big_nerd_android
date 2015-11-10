package com.example.ashleyconnor.criminalintent;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

public class CrimeActivity extends FragmentActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_crime);

    FragmentManager fm = getSupportFragmentManager();

    Fragment fragment = fm.findFragmentById(R.id.fragment_container);

    if(fragment == null) {
      fragment = new CrimeFragment();
      fm.beginTransaction()
          .add(R.id.fragment_container, fragment)
          .commit();
    }
  }
}
