package com.ibramir.busstation.activities.picker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ibramir.busstation.R;
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
    }
}
