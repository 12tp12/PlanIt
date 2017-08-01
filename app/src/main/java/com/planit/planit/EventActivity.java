package com.planit.planit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EventActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth fAuth;
    FirebaseUser fUser;
    DatabaseReference fDatabase;

    AppCompatButton logout;
    FloatingActionButton addEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        setSupportActionBar((Toolbar) findViewById(R.id.event_activity_toolbar));
//
        //need to change to the relevent event
        getSupportActionBar().setTitle(null);
        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser() == null)
        {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

        fAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null)
                {
                    Intent loginIntent = new Intent(EventActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
            }
        });
        fUser = fAuth.getCurrentUser();
        if(fUser == null)
        {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
        fDatabase = FirebaseDatabase.getInstance().getReference();

        logout = (AppCompatButton) findViewById(R.id.event_activity_signout);
        addEvent = (FloatingActionButton) findViewById(R.id.event_activity_add_event);

        logout.setOnClickListener(this);
        addEvent.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
//        switch (v.getId())
//        {
//            case R.id.home_signout:
//                signOut();
//                break;
//            case R.id.home_add_event:
//                Intent addEventIntent = new Intent(this, AddEvent.class);
//                startActivity(addEventIntent);
//                break;
//        }
    }
}
