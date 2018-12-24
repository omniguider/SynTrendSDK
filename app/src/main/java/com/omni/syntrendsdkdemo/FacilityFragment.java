package com.omni.syntrendsdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.omni.syntrendsdk.SynTrendSDKActivity;

public class FacilityFragment extends Fragment {

    public static final String TAG = "fragment_tag_facility";
    private static final String ARG_KEY_FACILITY_TYPE = "arg_key_facility_type";

    //parameter to sdk
    private static final String TYPE_FACILITY = "type_facility";

    private static String type;
    private EditText editText;

    public static FacilityFragment newInstance() {

        return new FacilityFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facility, container, false);

        view.findViewById(R.id.fragment_facility_fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        editText = view.findViewById(R.id.fragment_facility_et_category);

        view.findViewById(R.id.fragment_facility_tv_go_navi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryEdit = editText.getText().toString();
                if (categoryEdit.equals("電梯")) {
                    type = "Elevator";
                } else if (categoryEdit.equals("出入口")) {
                    type = "Entrance";
                } else if (categoryEdit.equals("樓梯")) {
                    type = "STAIRS";
                } else if (categoryEdit.equals("滅火器")) {
                    type = "Fire Extinguisher";
                } else if (categoryEdit.equals("緊急出入口")) {
                    type = "emergency_exit";
                } else if (categoryEdit.equals("AED")) {
                    type = "AED";
                } else if (categoryEdit.equals("手扶梯向上")) {
                    type = "escalator_up";
                } else if (categoryEdit.equals("手扶梯向下")) {
                    type = "escalator_down";
                } else if (categoryEdit.equals("男女廁")) {
                    type = "Restroom";
                } else if (categoryEdit.equals("會員服務中心")) {
                    type = "SERVICE CENTER";
                } else if (categoryEdit.equals("服務臺")) {
                    type = "Information";
                } else if (categoryEdit.equals("置物櫃")) {
                    type = "Lockers";
                } else if (categoryEdit.equals("育嬰室")) {
                    type = "Breastfeeding room";
                } else if (categoryEdit.equals("無障礙廁所")) {
                    type = "Accessibility";
                } else if (categoryEdit.equals("尿布檯")) {
                    type = "Diaper Changing";
                } else if (categoryEdit.equals("包裝區")) {
                    type = "packing area";
                } else if (categoryEdit.equals("提款機")) {
                    type = "ATM";
                } else if (categoryEdit.equals("花園")) {
                    type = "GARDEN";
                } else if (categoryEdit.equals("收銀區")) {
                    type = "CASHIER";
                } else if (categoryEdit.equals("親子廁所")) {
                    type = "FAMILY RESTROOM";
                } else if (categoryEdit.equals("無障礙電梯")) {
                    type = "ELEVATOR FOR DISABLED";
                } else if (categoryEdit.equals("販賣機")) {
                    type = "vending machine";
                } else if (categoryEdit.equals("公用電話")) {
                    type = "PUBLIC PHONE";
                } else if (categoryEdit.equals("一般及資源回收垃圾")) {
                    type = "TRASH Recycle";
                } else if (categoryEdit.equals("消防栓及警報器")) {
                    type = "Fire Hydrant";
                }

                Intent intent = new Intent(getActivity(), SynTrendSDKActivity.class);
                intent.putExtra(MainActivity.ARG_KEY_TYPE, TYPE_FACILITY);
                intent.putExtra(ARG_KEY_FACILITY_TYPE, type);
                startActivity(intent);
            }
        });


        return view;
    }
}
