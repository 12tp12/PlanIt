package com.planit.planit.PlanItTabs;

/**
 * Created by תומר on 8/1/2017.
 */
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.planit.planit.R;

public class Tab2Equipment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.planit_tab2_equipment, container, false);
        return rootView;
    }
}