package com.planit.planit;

//region android_base_imports
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewFlipper;
//endregion
//region firebase_imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.planit.planit.utils.Event;
import com.planit.planit.utils.FirebaseTables;
import com.planit.planit.utils.User;
//endregion
//region java_imports
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
//endregion

public class AddEvent extends AppCompatActivity implements View.OnClickListener{

    // edit texts
    EditText eventName;
    EditText eventLocation;
    EditText eventAbout;

    // text views
    TextView eventDate;
    TextView eventTime;

    // Dialogs
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    AppCompatButton addEventButton;

    User currentUser;

    //endregion

    //region FireBase
    private DatabaseReference mDatabase;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        //region firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
                    Intent loginIntent = new Intent(AddEvent.this, LoginActivity.class);
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
        //endregion

        setSupportActionBar((Toolbar) findViewById(R.id.add_event_toolbar));
        getSupportActionBar().setTitle(null);

        Bundle extras = getIntent().getExtras();

        currentUser = new Gson().fromJson(extras.getString("user"), User.class);

        Log.d("user between intents", "current users is " + currentUser.getPhoneNumber());

        eventName = (EditText) findViewById(R.id.event_name_input);
        eventLocation = (EditText) findViewById(R.id.event_location_input);
        eventAbout = (EditText) findViewById(R.id.event_about_input);

        eventDate = (TextView) findViewById(R.id.event_date_picker);
        eventTime = (TextView) findViewById(R.id.event_time_picker);

        eventDate.setOnClickListener(this);
        eventTime.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();

        //eventDate.setText(calendar.get(Calendar.DATE));

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.i("PICKER CHECK", "Date is: " + new SimpleDateFormat("dd/MM/yyyy"));
                eventDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                datePickerDialog.onDateChanged(view, year, month, dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                eventTime.setText((hourOfDay != 0 ? hourOfDay : hourOfDay + "0") + ":" +
                        (minute != 0 ? minute : minute + "0"));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        addEventButton = (AppCompatButton) findViewById(R.id.add_event_button);
        addEventButton.setOnClickListener(this);
    }

    public boolean validate(){
        String eventNameStr = eventName.getText().toString();
        String eventLocationStr = eventLocation.getText().toString();
        String eventAboutStr = eventAbout.getText().toString();
        String eventDateStr = eventDate.getText().toString();
        String eventTimeStr = eventTime.getText().toString();

        if (eventDateStr.isEmpty())
        {
            eventDate.setError("Event date can't be empty.");
            return false;
        }
        if (eventTimeStr.isEmpty())
        {
            eventTime.setError("Event time can't be empty.");
            return false;
        }
        if (eventNameStr.isEmpty())
        {
            eventName.setError("Event name can't be empty.");
            return false;
        }
        if (eventLocationStr.isEmpty())
        {
            eventLocation.setError("Event location can't be empty.");
            return false;
        }
        if (eventAboutStr.isEmpty())
        {
            eventAbout.setText(R.string.no_info);
        }
        return true;

    }

    public void AddEvent(){
        if(!validate())
        {
            return;
        }

        String eventNameStr = eventName.getText().toString();
        String eventLocationStr = eventLocation.getText().toString();
        String eventAboutStr = eventAbout.getText().toString();
        String eventDateStr = eventDate.getText().toString();
        String eventTimeStr = eventTime.getText().toString();

        writeNewEvent(eventNameStr, eventDateStr, eventTimeStr, eventLocationStr, eventAboutStr);

        finish();
        //
    }

    private void writeNewEvent(final String name, final String date, final String time,
                               final String location, final String about){
        String eventKey = mDatabase.child("events").push().getKey();
        Event event = new Event(name, date, time, location, about, currentUser.getPhoneNumber());
        Map<String, Object> postValues = event.toMapBaseEventInfoTable();

        Map<String, Object> childUpdates = new HashMap<>();
        //puts the full event in events root in firebase
        childUpdates.put(FirebaseTables.eventsInfoTable + "/" + eventKey, postValues);
        mDatabase.updateChildren(childUpdates);

        currentUser.addHostedEvent(eventKey);
        //puts new entry in events of this user
        childUpdates.clear();
        childUpdates.put(FirebaseTables.usersToEvents + "/" + currentUser.getPhoneNumber() + "/hosted/" + eventKey
                , true);
        mDatabase.updateChildren(childUpdates);

        // put entries in this event's invited/hosted
        childUpdates.clear();
        childUpdates.put(FirebaseTables.eventsToUsers + "/" + eventKey + "/hosted/" +
                currentUser.getPhoneNumber(), true);
        mDatabase.updateChildren(childUpdates);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.event_date_picker:
                datePickerDialog.show();
                break;
            case R.id.event_time_picker:
                timePickerDialog.show();
                break;
            case R.id.add_event_button:
                AddEvent();
                break;
        }
    }
}
