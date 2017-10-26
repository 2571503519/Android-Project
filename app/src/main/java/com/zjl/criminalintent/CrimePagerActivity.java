package com.zjl.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zjl.criminalintent.domain.Crime;
import com.zjl.criminalintent.domain.CrimeLab;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity
    implements CrimeFragment.Callbacks{
    private ViewPager mViewPager;
    private Button mJumpToFirst;
    private Button mJumpToLast;
    private List<Crime> mCrimes;

    public static final String EXTRA_CRIME_ID = "com.zjl.criminalIntent.crime_id";

    public static Intent newIntent(Context context, UUID crimeId){
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mJumpToFirst = (Button) findViewById(R.id.btn_crimePager_jumpToFirst);
        mJumpToLast = (Button) findViewById(R.id.btn_crimePager_jumpToLast);
        mViewPager = (ViewPager) findViewById(R.id.vp_crimePager);
        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        for (int i = 0; i < mCrimes.size(); i++){
            if(mCrimes.get(i).getId().equals(crimeId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

        mJumpToFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });

        mJumpToLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mCrimes.size() - 1);
            }
        });

    }

    @Override
    public void onCrimeUpdated(Crime crime) {

    }
}
