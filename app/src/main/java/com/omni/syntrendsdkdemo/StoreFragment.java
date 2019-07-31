package com.omni.syntrendsdkdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.omni.syntrendsdk.SynTrendSDKActivity;
import com.omni.syntrendsdk.tool.PreferencesTools;

public class StoreFragment extends Fragment {

    public static final String TAG = "fragment_tag_store";
    private static final String ARG_KEY_STORE_ROUTE_A = "arg_key_store_route_a";
    private static final String ARG_KEY_STORE_ROUTE_B = "arg_key_store_route_b";
    private static final String ARG_KEY_AUTO_HEADING = "arg_key_auto_heading";
    private static final String ARG_KEY_NAVIGATE_DIRECT = "arg_key_navigate_direct";

    private EditText editTextCustom;
    private EditText editTextRecommend;

    public static StoreFragment newInstance() {

        return new StoreFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        view.findViewById(R.id.fragment_store_fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        editTextCustom = view.findViewById(R.id.fragment_store_et_custom);
        editTextRecommend = view.findViewById(R.id.fragment_store_et_recommend);
        view.findViewById(R.id.fragment_store_tv_go_navi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String custom = editTextCustom.getText().toString();
                String recommend = editTextRecommend.getText().toString();

                if (custom.length() != 0 || recommend.length() != 0) {
                    Intent intent = new Intent(getActivity(), SynTrendSDKActivity.class);
                    intent.putExtra(ARG_KEY_STORE_ROUTE_A, custom);
                    intent.putExtra(ARG_KEY_STORE_ROUTE_B, recommend);
                    intent.putExtra(ARG_KEY_AUTO_HEADING, true);
                    intent.putExtra(ARG_KEY_NAVIGATE_DIRECT, false);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                            .setTitle("提示")
                            .setMessage("請輸入正確的店家id")
                            .setPositiveButton("好", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    builder.create().show();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        final String customPreference = PreferencesTools.getInstance().getProperty(getActivity(), PreferencesTools.KEY_STORE_ROUTE_A);
        final String recommendPreference = PreferencesTools.getInstance().getProperty(getActivity(), PreferencesTools.KEY_STORE_ROUTE_B);
        if (customPreference != null && recommendPreference != null) {
            if (customPreference.length() != 0 || recommendPreference.length() != 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("您尚有未逛完之店家\n確認是否離開？")
                        .setPositiveButton("繼續導航", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getActivity(), SynTrendSDKActivity.class);
                                intent.putExtra(ARG_KEY_STORE_ROUTE_A, customPreference);
                                intent.putExtra(ARG_KEY_STORE_ROUTE_B, recommendPreference);
                                intent.putExtra(ARG_KEY_AUTO_HEADING, true);
                                intent.putExtra(ARG_KEY_NAVIGATE_DIRECT, false);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("結束導航", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PreferencesTools.getInstance().saveProperty(getActivity(), PreferencesTools.KEY_STORE_ROUTE_A, "");
                                PreferencesTools.getInstance().saveProperty(getActivity(), PreferencesTools.KEY_STORE_ROUTE_B, "");
                            }
                        })
                        .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        })
                        .setCancelable(false);
                builder.create().show();
            }
        }
    }
}
