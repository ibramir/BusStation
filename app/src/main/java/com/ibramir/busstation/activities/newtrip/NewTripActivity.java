package com.ibramir.busstation.activities.newtrip;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ibramir.busstation.R;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.trips.TripManager;
import com.ibramir.busstation.station.vehicles.Vehicle;
import com.ibramir.busstation.users.Driver;
import com.ibramir.busstation.users.UserManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewTripActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, UserManager.OnRetrieveDriversListener {

    private EditText sourceText, destinationText, dateText, priceText;
    private Spinner vehicleSpinner, driverSpinner;
    private Date date;
    private Driver driver = null;
    private Vehicle.Type vehicleType = null;
    private List<Driver> drivers;

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("h:mm a d/M/yyyy", Locale.ENGLISH);

    @Override
    public void onRetrieveDrivers(Collection<Driver> drivers) {
        this.drivers = new ArrayList<>(drivers);
        initialize();
        findViewById(R.id.progressFrame).setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.newTrip);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        UserManager.getInstance().retrieveDrivers(this);
    }

    private void initialize() {
        sourceText = findViewById(R.id.sourceText);
        destinationText = findViewById(R.id.destinationText);
        dateText = findViewById(R.id.dateText);
        priceText = findViewById(R.id.priceText);
        vehicleSpinner = findViewById(R.id.vehicleSpinner);
        driverSpinner = findViewById(R.id.driverSpinner);

        dateText.setKeyListener(null);

        vehicleSpinner.setAdapter(new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, Vehicle.Type.values()));
        vehicleType = (Vehicle.Type) vehicleSpinner.getItemAtPosition(0);
        vehicleSpinner.setOnItemSelectedListener(this);

        driverSpinner.setAdapter(new DriverSpinnerAdapter(this, drivers));
        driverSpinner.setOnItemSelectedListener(this);
        if(drivers.size() > 0)
            driver = (Driver) driverSpinner.getItemAtPosition(0);
        vehicleSpinner.setOnItemSelectedListener(this);
    }

    public void confirm(View v) {
        String source = sourceText.getText().toString(), destination = destinationText.getText().toString();
        String price = priceText.getText().toString();
        if(source.equals("") || destination.equals("") || price.equals("") || driver == null
                || date == null || vehicleType == null) {
            Toast.makeText(this, "You must enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        Trip trip = new Trip.Builder().newID(true)
                .from(source)
                .to(destination)
                .atTime(date)
                .ofPrice(Double.parseDouble(price))
                .withDriver(driver.getUid())
                .withVehicle(vehicleType)
                .build();
        TripManager.getInstance().save(trip);
        setResult(RESULT_OK);
        finish();
    }

    public void chooseDate(View v) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        c.set(year, month, dayOfMonth, 0, 0, 0);
                        date = c.getTime();
                        TimePickerDialog timePickerDialog = new TimePickerDialog(NewTripActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        c.set(Calendar.MINUTE, minute);
                                        date = c.getTime();
                                        dateText.setText(DATE_FORMAT.format(date));
                                    }
                                }, 6, 0, false);
                        timePickerDialog.show();
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent == vehicleSpinner) {
            vehicleType = (Vehicle.Type) parent.getItemAtPosition(position);
        }
        else if(parent == driverSpinner) {
            driver = (Driver)parent.getItemAtPosition(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
