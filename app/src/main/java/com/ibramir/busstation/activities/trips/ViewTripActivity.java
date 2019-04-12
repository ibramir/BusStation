package com.ibramir.busstation.activities.trips;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ibramir.busstation.R;
import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.trips.TripManager;
import com.ibramir.busstation.station.vehicles.Car;
import com.ibramir.busstation.station.vehicles.Vehicle;
import com.ibramir.busstation.users.User;
import com.ibramir.busstation.users.UserManager;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ViewTripActivity extends AppCompatActivity implements RetrieveListener<User> {

    private Trip trip;
    private TextView driverText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip);
        trip = TripManager.getInstance().findTripById(getIntent().getStringExtra("tripId"));
        TextView sourceText = findViewById(R.id.sourceText), destinationText = findViewById(R.id.destinationText),
        timeText = findViewById(R.id.timeText), vehicleText = findViewById(R.id.vehicleText),
        economyText = findViewById(R.id.economyPrice),
        comfortText = findViewById(R.id.comfortPrice), luxuryText = findViewById(R.id.luxuryPrice);
        sourceText.setText(trip.getSource()); destinationText.setText(trip.getDestination());
        timeText.setText(new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.ENGLISH).format(trip.getTime()));
        vehicleText.setText(trip.getVehicle().getClass().getSimpleName());
        RadioButton economyButton = findViewById(R.id.economyButton), comfortButton = findViewById(R.id.comfortButton),
                luxuryButton = findViewById(R.id.luxuryButton);
        driverText = findViewById(R.id.driverText);
        driverText.setText(trip.getDriverId());
        UserManager.getInstance().retrieve(trip.getDriverId(),this);

        if(trip.getVehicle() instanceof Car) {
            economyText.setVisibility(View.GONE);
            luxuryText.setVisibility(View.GONE);
            economyButton.setVisibility(View.INVISIBLE);
            luxuryButton.setVisibility(View.INVISIBLE);
            comfortButton.setChecked(true);
        }
        economyText.setText(String.valueOf(trip.getVehicle().getSeatPrice(Vehicle.SeatClass.ECONOMY)+trip.getPrice()));
        comfortText.setText(String.valueOf(trip.getVehicle().getSeatPrice(Vehicle.SeatClass.COMFORT)+trip.getPrice()));
        luxuryText.setText(String.valueOf(trip.getVehicle().getSeatPrice(Vehicle.SeatClass.LUXURY)+trip.getPrice()));
    }

    public void book(View v) {
        //TODO book
    }

    @Override
    public void onRetrieve(User obj) {
        driverText.setText(obj.getName());
    }
}
