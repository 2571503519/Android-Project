package com.zjl.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.zjl.criminalintent.domain.Crime;
import com.zjl.criminalintent.domain.CrimeLab;

/**
 * Created by lenovo on 2017/8/8.
 */
public class CrimeListActivity extends SingleFragmentActivity
    implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks{

    private static final String TAG = "CrimeListActivity";

    @Override
    protected Fragment createFagment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null){
            //手机
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            //平板
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI(0);
    }
}
