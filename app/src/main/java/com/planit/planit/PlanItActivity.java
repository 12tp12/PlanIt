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

import com.planit.planit.PlanItTabs.Tab1Food;
import com.planit.planit.PlanItTabs.Tab2Equipment;
import com.planit.planit.PlanItTabs.Tab3Playlist;
import com.planit.planit.PlanItTabs.Tab4Friends;


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

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_it);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        //TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        //tabLayout.setupWithViewPager(mViewPager);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plan_it, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        public PlaceholderFragment() {
//        }
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.planit_tab1_food, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
//            return rootView;
//        }
//    }

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
                    return tab1;
                case 1:
                    Tab2Equipment tab2 = new Tab2Equipment();
                    return tab2;
                case 2:
                    Tab3Playlist tab3 = new Tab3Playlist();
                    return tab3;
                case 3:
                    Tab4Friends tab4 = new Tab4Friends();
                    return tab4;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }
        Drawable myDrawable = ContextCompat.getDrawable(PlanItActivity.this, R.drawable.food_and_drinks); //Drawable you want to display
        Drawable myDrawable2 = ContextCompat.getDrawable(PlanItActivity.this, R.drawable.equip);
        Drawable myDrawable3 = ContextCompat.getDrawable(PlanItActivity.this, R.drawable.playlist);
        Drawable myDrawable4 = ContextCompat.getDrawable(PlanItActivity.this, R.drawable.friends);
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    //return "Food & Drinks";
                    SpannableStringBuilder sb = new SpannableStringBuilder(" "); // space added before text for convenience

                    myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
                    ImageSpan span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE);
                    sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    return sb;
                case 1:
                    //return "Equipment";
                    SpannableStringBuilder sb2 = new SpannableStringBuilder(" "); // space added before text for convenience

                    myDrawable2.setBounds(0, 0, myDrawable2.getIntrinsicWidth(), myDrawable2.getIntrinsicHeight());
                    ImageSpan span2 = new ImageSpan(myDrawable2, ImageSpan.ALIGN_BASELINE);
                    sb2.setSpan(span2, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return sb2;
                case 2:
                    //return "Playlist";
                    SpannableStringBuilder sb3 = new SpannableStringBuilder("  "); // space added before text for convenience

                    myDrawable3.setBounds(0, 0, myDrawable3.getIntrinsicWidth(), myDrawable3.getIntrinsicHeight());
                    ImageSpan span3 = new ImageSpan(myDrawable3, ImageSpan.ALIGN_BASELINE);
                    sb3.setSpan(span3, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return sb3;
                case 3:
                    //return "Friends";
                    SpannableStringBuilder sb4 = new SpannableStringBuilder("  "); // space added before text for convenience

                    myDrawable4.setBounds(0, 0, myDrawable4.getIntrinsicWidth(), myDrawable4.getIntrinsicHeight());
                    ImageSpan span4 = new ImageSpan(myDrawable4, ImageSpan.ALIGN_BASELINE);
                    sb4.setSpan(span4, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return sb4;
            }
            return null;


        }
    }
}