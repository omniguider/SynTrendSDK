package com.omni.syntrendsdkdemo;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

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
