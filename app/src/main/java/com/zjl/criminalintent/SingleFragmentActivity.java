package com.zjl.criminalintent;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by lenovo on 2017/8/8.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFagment();

    @LayoutRes
    protected int getLayoutResId(){
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        //获取fragmentManager
        FragmentManager fm = getSupportFragmentManager();
        //获取fragment 的容器
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null){
            fragment = createFagment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
