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

    AppCompatButton logout;
    FloatingActionButton addEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setSupportActionBar((Toolbar) findViewById(R.id.home_toolbar));
        getSupportActionBar().setTitle(null);

//        Log.d("DEBUG", "Launching invite act");
//        Intent login2Intent = new Intent(this, InviteActivity.class);
//        startActivity(login2Intent);

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

        setFirebaseUser();
        getUser();

        logout.setOnClickListener(this);
        addEvent.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        fUser.getToken(true)
//                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
//                    public void onComplete(@NonNull Task<GetTokenResult> task) {
//                        if (task.isSuccessful()) {
//                            String idToken = task.getResult().getToken();
//                            // Todo: Update firebase
//                            FirebaseDatabase.getInstance().getReference("users").child(fUser.getEmail()).child("UserToken").setValue(idToken);
//                        }
//                    }
//                });
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
        String email = fUser.getEmail();
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
                                        Log.d("getUser", "user's name is " + currentUser.getFullName());
                                        Log.d("getUser", "user's email is " + currentUser.getEmail());
                                        Log.d("getUser", "user's token is " + currentUser.getToken());
                                        Log.d("getUser", "user's invited is " + currentUser.getInvited());
                                        Log.d("getUser", "user's hosted is " + currentUser.getHosted());
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
        Log.d("getUser", "user's name is " + currentUser.getFullName());
        Log.d("getUser", "user's email is " + currentUser.getEmail());
        Log.d("getUser", "user's token is " + currentUser.getToken());
        Log.d("getUser", "user's invited is " + currentUser.getInvited());
        Log.d("getUser", "user's hosted is " + currentUser.getHosted());
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
                if (currentUser == null)
                {
                    Toast.makeText(getApplicationContext(), "App is retrieving info", Toast.LENGTH_SHORT).show();
                    break;
                }
                Intent addEventIntent = new Intent(this, AddEvent.class);
                addEventIntent.putExtra("user", new Gson().toJson(currentUser));
                startActivity(addEventIntent);
                break;
        }
    }
}
