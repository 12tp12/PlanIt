package com.planit.planit;

//region android_base_imports
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.planit.planit.utils.Event;
//endregion
//region java_imports
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
//endregion

public class AddEvent extends AppCompatActivity implements View.OnClickListener{

    //region Data Members
    ViewFlipper viewFlipper;

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
        getSupportActionBar().setTitle("Add New Event");

        viewFlipper = (ViewFlipper) findViewById(R.id.add_event_flipper);
        viewFlipper.showNext();

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
//                eventName.setText("hi");
                eventDate.setText(dayOfMonth + "/" + month + "/" + year);
                datePickerDialog.onDateChanged(view, year, month, dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                eventTime.setText(hourOfDay + ":" + minute);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
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
            eventAbout.setError("About can't be empty.");
            return false;
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

        Intent homeIntent = new Intent(AddEvent.this, Home.class);
        startActivity(homeIntent);
        finish();
        //
    }

    private void writeNewEvent(String name, String date, String time, String location, String about){

        String key = mDatabase.child("events").push().getKey();
        String userCreator = fUser.getEmail();
        Event event = new Event(name, date, time, location, about, userCreator);
        Map<String, Object> postValues = event.toMapBaseEvent();

        Map<String, Object> childUpdates = new HashMap<>();
        //puts the full event in events root in firebase
        childUpdates.put("/events/" + key, postValues);
        mDatabase.updateChildren(childUpdates);

        //puts new entry in events of this user
        DatabaseReference f = FirebaseDatabase.getInstance().getReference("users").child(userCreator).child("events").push();
        f.setValue(key);
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
