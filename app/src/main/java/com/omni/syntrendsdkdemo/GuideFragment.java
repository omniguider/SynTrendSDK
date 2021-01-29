package com.omni.syntrendsdkdemo;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.omni.syntrendsdk.SynTrendSDKActivity;

public class GuideFragment extends Fragment {

    public static final String TAG = "fragment_tag_guide";
    private static final String ARG_KEY_GUIDE_CATEGORY = "arg_key_guide_category";
    private static final String ARG_KEY_AUTO_HEADING = "arg_key_auto_heading";

    //parameter to sdk
    private static final String TYPE_GUIDE = "type_guide";
    private static final String CATEGORY_ALL = "category_all";
    private static final String CATEGORY_STORE = "category_store";
    private static final String CATEGORY_RESTAURENT = "category_restaurant";
    private static final String CATEGORY_THEATER = "category_theater";
    private static final String CATEGORY_FACILITY = "category_facility";

    private static String category = CATEGORY_ALL;
    private EditText editText;

    public static GuideFragment newInstance() {

        return new GuideFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);

        view.findViewById(R.id.fragment_guide_fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.fragment_guide_tv_go_shopping).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = CATEGORY_ALL;
                Intent intent = new Intent(getActivity(), SynTrendSDKActivity.class);
                intent.putExtra(ARG_KEY_GUIDE_CATEGORY, category);
                startActivity(intent);
            }
        });

        editText = view.findViewById(R.id.fragment_guide_et_category);

        view.findViewById(R.id.fragment_guide_tv_go_shopping_specific).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryEdit = editText.getText().toString();
                if (categoryEdit.equals(getResources().getString(R.string.fragment_guide_cate_store))) {
                    category = CATEGORY_STORE;
                } else if (categoryEdit.equals(getResources().getString(R.string.fragment_guide_cate_restaurant))) {
                    category = CATEGORY_RESTAURENT;
                } else if (categoryEdit.equals(getResources().getString(R.string.fragment_guide_cate_theater))) {
                    category = CATEGORY_THEATER;
                } else if (categoryEdit.equals(getResources().getString(R.string.fragment_guide_cate_facility))) {
                    category = CATEGORY_FACILITY;
                }

                Intent intent = new Intent(getActivity(), SynTrendSDKActivity.class);
                intent.putExtra(ARG_KEY_GUIDE_CATEGORY, category);
                intent.putExtra(ARG_KEY_AUTO_HEADING, true);
                startActivity(intent);
            }
        });

        return view;
    }
}
