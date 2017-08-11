package com.planit.planit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.planit.planit.utils.Event;
import com.planit.planit.utils.User;

public class EventActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth fAuth;
    FirebaseUser fUser;
    User currentUser;
    Event currentEvent;
    DatabaseReference fDatabase;

    TextView eventTitle; // title in toolbar
    TextView eventLocation;
    TextView eventDate;
    TextView eventTime;
    TextView eventAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        setSupportActionBar((Toolbar) findViewById(R.id.event_activity_toolbar));

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

        Bundle extras = getIntent().getExtras();

        currentUser = new Gson().fromJson(extras.getString("user"), User.class);
        currentEvent = new Gson().fromJson(extras.getString("event"), Event.class);

        eventTitle = (TextView) findViewById(R.id.event_title);
        eventLocation = (TextView) findViewById(R.id.info_location);
        eventDate = (TextView) findViewById(R.id.info_date);
        eventTime = (TextView) findViewById(R.id.info_hour);
        eventAbout = (TextView) findViewById(R.id.about_text);

        fDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventTitle.setText(currentEvent.getName());
        eventLocation.setText(currentEvent.getLocation());
        eventDate.setText(currentEvent.getDate());
        eventTime.setText(currentEvent.getTime());
        eventAbout.setText(currentEvent.getAbout());

        //getPeople();
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
