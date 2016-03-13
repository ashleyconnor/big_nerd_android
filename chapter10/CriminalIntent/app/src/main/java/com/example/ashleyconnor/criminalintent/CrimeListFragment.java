package com.example.ashleyconnor.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class CrimeListFragment extends Fragment {

  private static final String ARG_CRIME_POSITION = "crime_position";
  private RecyclerView mCrimeRecyclerView;
  private CrimeAdapter mAdapter;

  public static CrimeListFragment newInstance() {
    Bundle args = new Bundle();

    CrimeListFragment crimeListFragment = new CrimeListFragment();
    crimeListFragment.setArguments(args);
    return crimeListFragment;
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

    mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
    mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    updateUI();

    return view;
  }

  private void updateUI() {
    CrimeLab crimeLab = CrimeLab.get(getActivity());
    List<Crime> crimes = crimeLab.getCrimes();

    if (mAdapter == null) {
      mAdapter = new CrimeAdapter(crimes);
      mCrimeRecyclerView.setAdapter(mAdapter);
    } else {
      if (getArguments() != null) {
        mAdapter.notifyItemChanged(getArguments().getInt(ARG_CRIME_POSITION));
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    updateUI();
  }

  private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Crime mCrime;
    private TextView mTitleTextView;
    private TextView mDateTextView;
    private CheckBox mSolvedCheckbox;

    public CrimeHolder(View itemView) {
      super(itemView);
      itemView.setOnClickListener(this);

      mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
      mDateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
      mSolvedCheckbox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);

      mSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          mSolvedCheckbox.setChecked(isChecked);
          mCrime.setSolved(isChecked);
        }
      });
    }

    public void bindCrime(Crime crime) {
      mCrime = crime;
      mTitleTextView.setText(mCrime.getTitle());
      mDateTextView.setText(mCrime.getDate().toString());
      mSolvedCheckbox.setChecked(mCrime.isSolved());
    }

    @Override
    public void onClick(View v) {
      getArguments().putInt(ARG_CRIME_POSITION, getLayoutPosition());
      Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getId());
      startActivity(intent);
    }
  }

  private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
    private List<Crime> mCrimes;

    public CrimeAdapter(List<Crime> crimes) {
      mCrimes = crimes;
    }

    @Override public int getItemCount() {
      return mCrimes.size();
    }

    @Override public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

      View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);

      return new CrimeHolder(view);
    }

    @Override public void onBindViewHolder(CrimeHolder holder, int position) {
      Crime crime = mCrimes.get(position);
      holder.bindCrime(crime);
    }
  }
}
