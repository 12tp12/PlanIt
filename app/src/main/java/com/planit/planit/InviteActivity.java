package com.planit.planit;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.planit.planit.utils.Event;
import com.planit.planit.utils.User;

import java.util.HashMap;

/**
 * Created by HP on 01-Aug-17.
 */

public class InviteActivity extends AppCompatActivity implements View.OnClickListener {

    Event currentEvent;
    User currentUser;

    private InviteFragment inviteFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        Toolbar toolbar = (Toolbar) findViewById(R.id.event_activity_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        currentUser = new Gson().fromJson(extras.getString("user"), User.class);
        currentEvent = new Gson().fromJson(extras.getString("event"), Event.class);

        inviteFragment = new InviteFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.contacts_fragment_container, inviteFragment);
        fragmentTransaction.commitNow();
        inviteFragment.setData(currentUser, currentEvent);

        SearchView sView = (SearchView) findViewById(R.id.invite_search_input);
        sView.setIconifiedByDefault(false);
        sView.clearFocus();
        sView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                SearchView sv = (SearchView) v;
                if (hasFocus)
                {
                    sv.setElevation(16);
                }
                else
                {
                    sv.setElevation(8);
                }
            }
        });
        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("search debug", "called querynotify");
                inviteFragment.onQueryNotify(newText);
                return true;
            }
        });


        FloatingActionButton inviteSelected = (FloatingActionButton) findViewById(R.id.invite_selected_contacts);
        inviteSelected.setOnClickListener(this);
    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.invite_selected_contacts:
                inviteFragment.invite();
        }
    }
}
