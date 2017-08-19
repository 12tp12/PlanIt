package com.planit.planit;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.planit.planit.FireBaseService.MyFirebaseInstanceIDService.MyFirebaseInstanceIDService;
import com.planit.planit.utils.User;
import com.planit.planit.utils.Utilities;

public class Home extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    DatabaseReference fDatabase;

    EventFragment eventFragment;

    final User currentUser = new User();

    boolean initialRun = true;

    AppCompatButton logout;
    FloatingActionButton addEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setSupportActionBar((Toolbar) findViewById(R.id.home_toolbar));
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
                    Intent loginIntent = new Intent(Home.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
            }
        });

        fDatabase = FirebaseDatabase.getInstance().getReference();

        logout = (AppCompatButton) findViewById(R.id.home_signout);
        addEvent = (FloatingActionButton) findViewById(R.id.home_add_event);

        eventFragment = new EventFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.events_fragment_container, eventFragment);
        fragmentTransaction.commit();

        logout.setOnClickListener(this);
        addEvent.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (initialRun)
        {
            initialRun = false;
            setFirebaseUser();
            getUser();
        }
    }

    public void setFirebaseUser()
    {
        if(fAuth.getCurrentUser() == null)
        {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

        fUser = fAuth.getCurrentUser();
    }

    public void getUser()
    {
        String email = fAuth.getCurrentUser().getEmail();
        fDatabase.child("emailsToPhones").child(Utilities.encodeKey(email)).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String phone = dataSnapshot.getValue(String.class);
                        Log.d("getUser", "Retrieved phone, phone is " + phone);
                        if (phone == null)
                        {
                            Log.d("BUG", "PHONE IS NULL!");
                            finish();
                        }
                        // got phone, now retrieve user and populate currentUser
                        fDatabase.child("users").child(phone).addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User u = dataSnapshot.getValue(User.class);
                                        if (u == null)
                                        {
                                            Log.d("getUser", "user not found");
                                            fAuth.signOut();
                                            finish();
                                        }
                                        u.setPhoneNumber(dataSnapshot.getKey());
                                        currentUser.setUser(u);
                                        eventFragment.setData(currentUser);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                }
                        );
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void signOut()
    {
        fAuth.signOut();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.home_signout:
                signOut();
                break;
            case R.id.home_add_event:
                Intent addEventIntent = new Intent(this, AddEvent.class);
                addEventIntent.putExtra("user", new Gson().toJson(currentUser));
                startActivity(addEventIntent);
                break;
        }
    }
}
