package com.ibramir.busstation.activities.trips;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
        setSupportActionBar(toolbar);

        trip = TripManager.getInstance().findTripById(getIntent().getStringExtra("tripId"));
        TextView sourceText = findViewById(R.id.sourceText), destinationText = findViewById(R.id.destinationText),
        timeText = findViewById(R.id.timeText), vehicleText = findViewById(R.id.vehicleText),
        economyText = findViewById(R.id.economyPrice),
        comfortText = findViewById(R.id.comfortPrice), luxuryText = findViewById(R.id.luxuryPrice);
        sourceText.setText(trip.getSource()); destinationText.setText(trip.getDestination());
        timeText.setText(new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.ENGLISH).format(trip.getTime()));
        vehicleText.setText(trip.getVehicle().getClass().getSimpleName());
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

        if(trip.getVehicle().hasSeatClass(Vehicle.SeatClass.ECONOMY)) {
            economyText.setText(String.valueOf(trip.getVehicle().getSeatPrice(Vehicle.SeatClass.ECONOMY)+trip.getPrice()));
        }
        else {
            economyText.setVisibility(View.GONE);
            economyButton.setVisibility(View.INVISIBLE);
        }
        if(trip.getVehicle().hasSeatClass(Vehicle.SeatClass.COMFORT)) {
            comfortText.setText(String.valueOf(trip.getVehicle().getSeatPrice(Vehicle.SeatClass.COMFORT)+trip.getPrice()));
        }
        else {
            comfortText.setVisibility(View.GONE);
            comfortButton.setVisibility(View.INVISIBLE);
        }
        if(trip.getVehicle().hasSeatClass(Vehicle.SeatClass.LUXURY)) {
            luxuryText.setText(String.valueOf(trip.getVehicle().getSeatPrice(Vehicle.SeatClass.LUXURY)+trip.getPrice()));
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
    public void onRetrieve(User obj) {
        driverText.setText(obj.getName());
    }
}
