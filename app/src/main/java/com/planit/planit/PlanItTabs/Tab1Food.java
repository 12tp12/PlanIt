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

public class Tab1Food extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.planit_tab1_food, container, false);
        return rootView;
    }
}
