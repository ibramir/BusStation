package com.ibramir.busstation.activities.viewtrip;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ibramir.busstation.R;
import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.trips.TripManager;
import com.ibramir.busstation.station.vehicles.Vehicle;
import com.ibramir.busstation.users.User;
import com.ibramir.busstation.users.UserManager;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ViewTripActivity extends AppCompatActivity implements RetrieveListener<User> {

    private Trip trip;
    private TextView driverText;
    private int result = RESULT_CANCELED;
    private Vehicle.SeatClass seatClass = null;
    private RadioButton economyButton, comfortButton, luxuryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.bookOne);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        trip = TripManager.getInstance().findTripById(getIntent().getStringExtra("tripId"));
        TextView sourceText = findViewById(R.id.sourceText), destinationText = findViewById(R.id.destinationText),
        timeText = findViewById(R.id.timeText), vehicleText = findViewById(R.id.vehicleText),
        economyText = findViewById(R.id.economyPrice),
        comfortText = findViewById(R.id.comfortPrice), luxuryText = findViewById(R.id.luxuryPrice);
        sourceText.setText(trip.getSource()); destinationText.setText(trip.getDestination());
        timeText.setText(new SimpleDateFormat("h:m a d/M/yyyy", Locale.ENGLISH).format(trip.getTime()));
        Vehicle vehicle = trip.getVehicle();
        vehicleText.setText(vehicle.getClass().getSimpleName());

        economyButton = findViewById(R.id.economyButton);
        comfortButton = findViewById(R.id.comfortButton);
        luxuryButton = findViewById(R.id.luxuryButton);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.economyButton: seatClass = Vehicle.SeatClass.ECONOMY; break;
                    case R.id.comfortButton: seatClass = Vehicle.SeatClass.COMFORT; break;
                    case R.id.luxuryButton: seatClass = Vehicle.SeatClass.LUXURY; break;
                    default: seatClass = null;
                }
            }
        });
        driverText = findViewById(R.id.driverText);
        driverText.setText(trip.getDriverId());
        UserManager.getInstance().retrieve(trip.getDriverId(),this);

        if(vehicle.hasSeatClass(Vehicle.SeatClass.ECONOMY)) {
            if(vehicle.availableSeats(1, Vehicle.SeatClass.ECONOMY))
                economyText.setText(String.valueOf(
                        (int)(vehicle.getSeatPrice(Vehicle.SeatClass.ECONOMY)+trip.getPrice())));
            else {
                economyText.setText("FULL");
                economyText.setTextColor(ResourcesCompat.getColor(getResources(),R.color.fullRed,null));
                economyButton.setEnabled(false);
            }
        }
        else {
            economyText.setVisibility(View.GONE);
            economyButton.setVisibility(View.INVISIBLE);
        }
        if(vehicle.hasSeatClass(Vehicle.SeatClass.COMFORT)) {
            if(vehicle.availableSeats(1, Vehicle.SeatClass.COMFORT))
                comfortText.setText(String.valueOf(
                        (int)(vehicle.getSeatPrice(Vehicle.SeatClass.COMFORT)+trip.getPrice())));
            else {
                comfortText.setText("FULL");
                comfortText.setTextColor(ResourcesCompat.getColor(getResources(),R.color.fullRed,null));
                comfortButton.setEnabled(false);
            }
        }
        else {
            comfortText.setVisibility(View.GONE);
            comfortButton.setVisibility(View.INVISIBLE);
        }
        if(vehicle.hasSeatClass(Vehicle.SeatClass.LUXURY)) {
            if(vehicle.availableSeats(1, Vehicle.SeatClass.LUXURY))
                luxuryText.setText(String.valueOf(
                        (int)(vehicle.getSeatPrice(Vehicle.SeatClass.LUXURY)+trip.getPrice())));
            else {
                luxuryText.setText("FULL");
                luxuryText.setTextColor(ResourcesCompat.getColor(getResources(),R.color.fullRed,null));
                luxuryButton.setEnabled(false);
            }
        }
        else{
            luxuryText.setVisibility(View.GONE);
            luxuryButton.setVisibility(View.INVISIBLE);
        }

    }

    public void book(View v) {
        if(seatClass != null) {
            result = RESULT_OK;
            finish();
        }
        else
            Toast.makeText(this,"Select seat class",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        Intent data = null;
        if(result == RESULT_OK) {
            data = new Intent();
            data.putExtra("seatClass", seatClass);
        }
        setResult(result, data);
        super.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRetrieve(User obj) {
        driverText.setText(obj.getName());
    }
}
