package com.planit.planit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.planit.planit.utils.Event;
import com.planit.planit.utils.FirebaseTables;
import com.planit.planit.utils.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.os.Handler;
public class EventActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth fAuth;
    FirebaseUser fUser;
    User currentUser;
    Event currentEvent;
    boolean isHost;
    DatabaseReference fDatabase;

    TextView eventTitle; // title in toolbar
    TextView eventLocation;
    TextView eventDate;
    TextView eventTime;
    TextView eventAbout;
    private TextView countdown;
    private Handler handler;
    private Runnable runnable;

    CardView peopleCard;
    CardView planitCard;

    ChatFragment chatFragment;

    ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.event_activity_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
        isHost = extras.getBoolean("isHost");

        chatFragment = new ChatFragment();
        Bundle chatArgs = new Bundle();
        chatArgs.putString("user", new Gson().toJson(currentUser));
        chatArgs.putString("event", new Gson().toJson(currentEvent));
        chatFragment.setArguments(chatArgs);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.chat_fragment_container, chatFragment);
        fragmentTransaction.commit();

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event e = dataSnapshot.getValue(Event.class);
                e.setKey(dataSnapshot.getKey());
                currentEvent.setEvent(e);
                eventTitle.setText(currentEvent.getName());
                eventLocation.setText(currentEvent.getLocation());
                eventDate.setText(currentEvent.getDate());
                eventTime.setText(currentEvent.getTime());
                eventAbout.setText(currentEvent.getAbout());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        eventTitle = (TextView) findViewById(R.id.event_title);
        eventTitle.setMovementMethod(new ScrollingMovementMethod());
        eventTitle.requestFocus();
        eventLocation = (TextView) findViewById(R.id.info_location);
        eventLocation.setSelected(true);

        eventDate = (TextView) findViewById(R.id.info_date);
        eventTime = (TextView) findViewById(R.id.info_hour);
        eventAbout = (TextView) findViewById(R.id.about_text);

        fDatabase = FirebaseDatabase.getInstance().getReference();

        peopleCard = (CardView) findViewById(R.id.people_card);
        planitCard = (CardView) findViewById(R.id.planit_card);

        peopleCard.setOnClickListener(this);
        planitCard.setOnClickListener(this);

        //added
        //added
        countdown = (TextView) findViewById(R.id.counter_text);
        countDownStart();
    }
    public void countDownStart() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "dd/MM/yyyy hh:mm");
// Please here set your event date//YYYY-MM-DD
                    Date futureDate = dateFormat.parse(eventDate.getText().toString()+" "+eventTime.getText().toString());
                    Date currentDate = new Date();
                    if (!currentDate.after(futureDate)) {
                        long diff = futureDate.getTime()
                                - currentDate.getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;
                        countdown.setText("Starts In " + String.format("%02d", days)+" Days, "
                                + String.format("%02d", hours)+" Hours, "
                                + String.format("%02d", minutes)+" Min., "
                                + String.format("%02d", seconds) + " Sec.");
                    } else {
                        countdown.setText("The event started!");
                        //textViewGone();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1 * 1000);

    }

    @Override
    protected void onStart() {
        super.onStart();
        eventTitle.setText(currentEvent.getName());
        eventLocation.setText(currentEvent.getLocation());
        eventDate.setText(currentEvent.getDate());
        eventTime.setText(currentEvent.getTime());
        eventAbout.setText(currentEvent.getAbout());
        fDatabase.child(FirebaseTables.eventsInfoTable + "/" + currentEvent.getKey()).
                addValueEventListener(eventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        fDatabase.child(FirebaseTables.eventsInfoTable + "/" + currentEvent.getKey()).
                removeEventListener(eventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isHost)
        {
            getMenuInflater().inflate(R.menu.event_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_edit:
                Intent editActivity = new Intent(this, EditEventActivity.class);
                editActivity.putExtra("user", new Gson().toJson(currentUser));
                editActivity.putExtra("event", new Gson().toJson(currentEvent));
                startActivity(editActivity);
                //finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.planit_card:
                Intent planitPage = new Intent(this, PlanItActivity.class);
                planitPage.putExtra("user", new Gson().toJson(currentUser));
                planitPage.putExtra("event", new Gson().toJson(currentEvent));
                startActivity(planitPage);
                break;
            case R.id.people_card:
                Intent peoplePage = new Intent(this, InvitedPageActivity.class);
                peoplePage.putExtra("user", new Gson().toJson(currentUser));
                peoplePage.putExtra("event", new Gson().toJson(currentEvent));
                peoplePage.putExtra("isHost", isHost);
                startActivity(peoplePage);
                break;
        }
    }
}
