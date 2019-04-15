package com.ibramir.busstation.activities.picker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ibramir.busstation.R;
import com.ibramir.busstation.activities.main.MainActivity;
import com.ibramir.busstation.activities.tickets.TicketsActivity;
import com.ibramir.busstation.activities.trips.TripsActivity;
import com.ibramir.busstation.users.Customer;
import com.ibramir.busstation.users.Driver;
import com.ibramir.busstation.users.Manager;
import com.ibramir.busstation.users.User;

public class PickerActivity extends AppCompatActivity {

    public static final String BOOK_ONE = "trips.bookOne";
    public static final String BOOK_ROUND = "trips.bookTwo";
    public static final String MANAGE = "trips.manage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.home);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        User user = User.getCurrentUser();
        View manageButton = findViewById(R.id.manageButton);
        View bookButton = findViewById(R.id.bookOneButton);
        View bookRound = findViewById(R.id.bookTwoButton);
        if(user instanceof Customer) {
            manageButton.setVisibility(View.GONE);
        }
        else if(user instanceof Manager) {
            findViewById(R.id.myTripsButton).setVisibility(View.GONE);
            bookButton.setVisibility(View.GONE);
            bookRound.setVisibility(View.GONE);
        }
        else if(user instanceof Driver) {
            manageButton.setVisibility(View.GONE);
            bookButton.setVisibility(View.GONE);
            bookRound.setVisibility(View.GONE);
        }
    }

    public void showTrips(View v) {
        Intent intent = new Intent(this, TripsActivity.class);
        switch (v.getId()) {
            case R.id.bookOneButton: intent.setAction(BOOK_ONE); break;
            case R.id.bookTwoButton: intent.setAction(BOOK_ROUND); break;
            case R.id.manageButton: intent.setAction(MANAGE); break;
        }
        startActivity(intent);
    }
    public void myTrips(View v) {
        if(User.getCurrentUser() instanceof Customer)
            startActivity(new Intent(this, TicketsActivity.class));
        else if(User.getCurrentUser() instanceof Driver)
            startActivity(new Intent(this, TripsActivity.class));
    }
    public void signOut(View v) {
        findViewById(R.id.progressFrame).setVisibility(View.VISIBLE);
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                User.logout();
                startActivity(new Intent(PickerActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
