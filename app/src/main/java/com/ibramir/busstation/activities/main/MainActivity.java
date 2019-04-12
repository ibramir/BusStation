package com.ibramir.busstation.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ibramir.busstation.R;
import com.ibramir.busstation.station.trips.TripManager;
import com.ibramir.busstation.users.User;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1;

    private User.Type userType = User.Type.CUSTOMER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Intent intent = new Intent(this, TripsActivity.class);
        //startActivity(intent);
        Toolbar toolbar = findViewById(R.id.loginToolbar);
        TripManager.getInstance().fetchTrips();
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user != null) {
            User.login(user.getUid(), userType);
            loggedIn();
        }
        else {
            User.logout();
        }
    }

    private void loggedIn() {
        //TODO next activity
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
                        )).build(),
                RC_SIGN_IN
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            User.login(FirebaseAuth.getInstance().getCurrentUser().getUid(), userType);
            loggedIn();
        }
    }
}
