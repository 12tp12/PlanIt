package com.planit.planit;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by HP on 01-Aug-17.
 */

public class InviteActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mDatabase;

    private InviteFragment inviteFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        Log.d("DEBUG", "IN INVITE ACTIVITY");
        inviteFragment = new InviteFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.contacts_fragment_container, inviteFragment);
        fragmentTransaction.commit();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        SearchView sView = (SearchView) findViewById(R.id.invite_search_input);
        sView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
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

                inviteFragment.onQueryNotify(newText);
                return true;
            }
        });


        FloatingActionButton inviteSelected = (FloatingActionButton) findViewById(R.id.invite_selected_contacts);
        inviteSelected.setOnClickListener(this);
    }

    public void invite()
    {
        HashMap<String, Boolean> selectedContacts = inviteFragment.getSelectedContacts();
        /* TODO get already invited and append the new selectedContacts to this hasmap,
        *  so already invited people will still be invited.
        */
        mDatabase.child("events").child("1234567").child("Invited").setValue(selectedContacts);
    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.invite_selected_contacts:
                invite();
        }
    }
}
