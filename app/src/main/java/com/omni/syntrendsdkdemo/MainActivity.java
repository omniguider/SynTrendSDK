package com.omni.syntrendsdkdemo;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.omni.syntrendsdk.SynTrendSDKActivity;

public class MainActivity extends AppCompatActivity {

    public static final String ARG_KEY_TYPE = "type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.activity_main_store_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragmentPage(StoreFragment.newInstance(), StoreFragment.TAG);
            }
        });

        findViewById(R.id.activity_main_guide_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragmentPage(GuideFragment.newInstance(), StoreFragment.TAG);
            }
        });

        findViewById(R.id.activity_main_facility_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragmentPage(FacilityFragment.newInstance(), StoreFragment.TAG);
            }
        });
    }

    private void openFragmentPage(Fragment fragment, String tag) {
        beginTransaction(MainActivity.this).add(R.id.activity_main_fl, fragment, tag)
                .addToBackStack(null)
                .commit();
    }

    private FragmentTransaction beginTransaction(FragmentActivity fragmentActivity) {

        return fragmentActivity.getSupportFragmentManager().beginTransaction();
    }
}
