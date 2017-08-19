package com.planit.planit;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.planit.planit.PlanItTabs.Tab1Food;
import com.planit.planit.PlanItTabs.Tab2Equipment;
import com.planit.planit.utils.Event;
import com.planit.planit.utils.User;


public class PlanItActivity extends AppCompatActivity{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    User currentUser;
    Event currentEvent;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_it);

        Toolbar toolbar = (Toolbar) findViewById(R.id.event_planit_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        //setSupportActionBar((Toolbar) findViewById(R.id.planit_toolbar));
        //TODO: change to the specific event title
        //getSupportActionBar().setTitle("My Event");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        Bundle extras = getIntent().getExtras();
        currentUser = new Gson().fromJson(extras.getString("user"), User.class);
        currentEvent = new Gson().fromJson(extras.getString("event"), Event.class);
        TextView title =(TextView) findViewById(R.id.event_name);
        title.setText(currentEvent.getName());

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter{

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
//            // getItem is called to instantiate the fragment for the given page.
//            // Return a PlaceholderFragment (defined as a static inner class below).
//            return PlaceholderFragment.newInstance(position + 1);
            switch (position){
                case 0:
                    Tab1Food tab1 = new Tab1Food();
                    Bundle foodArgs = new Bundle();
                    foodArgs.putString("user", new Gson().toJson(currentUser));
                    foodArgs.putString("event", new Gson().toJson(currentEvent));
                    tab1.setArguments(foodArgs);
                    return tab1;
                case 1:
                    Tab2Equipment tab2 = new Tab2Equipment();
                    Bundle equipArgs = new Bundle();
                    equipArgs.putString("user", new Gson().toJson(currentUser));
                    equipArgs.putString("event", new Gson().toJson(currentEvent));
                    tab2.setArguments(equipArgs);
                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
        Drawable foodDrinksIcon = ContextCompat.getDrawable(PlanItActivity.this, R.drawable.food_and_drinks); //Drawable you want to display
        Drawable equipmentIcon = ContextCompat.getDrawable(PlanItActivity.this, R.drawable.equip);
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    //return "Food & Drinks";
                    SpannableStringBuilder sb = new SpannableStringBuilder(" "); // space added before text for convenience

                    foodDrinksIcon.setBounds(0, 0, foodDrinksIcon.getIntrinsicWidth(), foodDrinksIcon.getIntrinsicHeight());
                    ImageSpan span = new ImageSpan(foodDrinksIcon, ImageSpan.ALIGN_BASELINE);
                    sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    return sb;
                case 1:
                    //return "Equipment";
                    SpannableStringBuilder sb2 = new SpannableStringBuilder(" "); // space added before text for convenience

                    equipmentIcon.setBounds(0, 0, equipmentIcon.getIntrinsicWidth(), equipmentIcon.getIntrinsicHeight());
                    ImageSpan span2 = new ImageSpan(equipmentIcon, ImageSpan.ALIGN_BASELINE);
                    sb2.setSpan(span2, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return sb2;
            }
            return null;


        }
    }
}