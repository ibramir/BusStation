package com.ibramir.busstation.activities.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ibramir.busstation.R;
import com.ibramir.busstation.activities.picker.PickerActivity;
import com.ibramir.busstation.station.trips.TripManager;
import com.ibramir.busstation.users.LoginListener;
import com.ibramir.busstation.users.User;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1;

    private User.Type userType = User.Type.CUSTOMER;

    private LoginListener loginListener = new LoginListener() {
        @Override
        public void onLogin() {
            if(User.getCurrentUser() == null) {
                findViewById(R.id.progressFrame).setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                return;
            }
            startActivity(new Intent(MainActivity.this, PickerActivity.class));
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Trip.Builder builder = new Trip.Builder();
        Trip trip = builder.newID(true)
                .from("Alexandria").to("Cairo")
                .atTime(new Date(119,5,25))
                .withDriver("oLlp7w3iStYFHHW6EN4UuZMMkoF3")
                .ofPrice(50)
                .withVehicle(Vehicle.Type.BUS)
                .build();
        Trip trip2 = builder.newID(true)
                .from("Cairo").to("Alexandria")
                .atTime(new Date(119,5,27))
                .withDriver("oLlp7w3iStYFHHW6EN4UuZMMkoF3")
                .ofPrice(50)
                .withVehicle(Vehicle.Type.BUS)
                .build();
        TripManager.getInstance().save(trip);
        TripManager.getInstance().save(trip2);*/
        //Intent intent = new Intent(this, TripsActivity.class);
        //startActivity(intent);
        Toolbar toolbar = findViewById(R.id.loginToolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        TripManager.getInstance().fetchTrips();
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user != null) {
            loggedIn();
        }
        else {
            User.logout();
        }
    }

    private void loggedIn() {
        findViewById(R.id.progressFrame).setVisibility(View.VISIBLE);
        User.login(FirebaseAuth.getInstance().getCurrentUser().getUid(), userType, loginListener);
    }


    public void login(View v) {
        switch (v.getId()) {
            case R.id.customerButton: userType = User.Type.CUSTOMER; break;
            case R.id.driverButton: userType = User.Type.DRIVER; break;
            case R.id.managerButton: userType = User.Type.MANAGER; break;
        }
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build()
                        )).setIsSmartLockEnabled(false).build(),
                RC_SIGN_IN
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN ) {
            if(FirebaseAuth.getInstance().getCurrentUser() != null)
                loggedIn();
        }
    }
}
