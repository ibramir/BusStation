package com.ibramir.busstation.activities.picker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ibramir.busstation.R;
import com.ibramir.busstation.activities.trips.TripsActivity;
import com.ibramir.busstation.users.Customer;
import com.ibramir.busstation.users.Driver;
import com.ibramir.busstation.users.Manager;
import com.ibramir.busstation.users.User;

public class PickerActivity extends AppCompatActivity {

    public static final String BROWSE = "trips.browse";
    public static final String MANAGE = "trips.manage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        User user = User.getCurrentUser();
        View manageButton = findViewById(R.id.manageButton);
        View tripsButton = findViewById(R.id.tripsButton);
        if(user instanceof Customer) {
            manageButton.setVisibility(View.GONE);
        }
        else if(user instanceof Manager) {
            findViewById(R.id.myTripsButton).setVisibility(View.GONE);
            //tripsButton.setVisibility(View.GONE);
        }
        else if(user instanceof Driver) {
            manageButton.setVisibility(View.GONE);
            tripsButton.setVisibility(View.GONE);
        }
    }

    public void showTrips(View v) {
        Intent intent = new Intent(this, TripsActivity.class);
        switch (v.getId()) {
            case R.id.tripsButton: intent.setAction(BROWSE); break;
            case R.id.manageButton: intent.setAction(MANAGE); break;
        }
        startActivity(intent);
    }
    public void myTrips(View v) {

    }
}
