package com.planit.planit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewFlipper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by HP on 29-Jun-17.
 */

public class AddEvent extends AppCompatActivity implements View.OnClickListener{
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

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
                Log.i("PICKER CHECK", "date is: " + new SimpleDateFormat("dd/MM/yyyy"));
                eventName.setText("hi");
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
        }
    }
}
