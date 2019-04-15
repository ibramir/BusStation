package com.ibramir.busstation.activities.tickets;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibramir.busstation.R;
import com.ibramir.busstation.station.tickets.Ticket;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.vehicles.Bus;
import com.ibramir.busstation.station.vehicles.Car;
import com.ibramir.busstation.station.vehicles.MiniBus;
import com.ibramir.busstation.station.vehicles.Vehicle;
import com.ibramir.busstation.users.Customer;
import com.ibramir.busstation.users.User;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ViewTicketActivity extends AppCompatActivity {

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("h:m a d/M/yyyy", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ticket);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.ticketInfo);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String ticketId = getIntent().getStringExtra("ticketId");
        Ticket t = null;
        for(Ticket ticket: ((Customer) User.getCurrentUser()).getTickets()) {
            if(ticket.equals(ticketId)) {
                t = ticket;
                break;
            }
        }
        if(t == null) {
            finish();
            return;
        }

        ImageView vIcon = findViewById(R.id.vIcon);
        vIcon.setImageResource(getVehicleIcon(t.getTrip().getVehicle()));
        TextView sourceText = findViewById(R.id.sourceText),
                destinationText = findViewById(R.id.destinationText),
                dateText = findViewById(R.id.dateText),
                classText = findViewById(R.id.seatClassText),
                priceText = findViewById(R.id.priceText);
        sourceText.setText(t.getTrip().getSource());
        destinationText.setText(t.getTrip().getDestination());
        classText.setText(t.getSeatClass().toString());
        dateText.setText(DATE_FORMAT.format(t.getTrip().getTime()));
        priceText.setText(String.valueOf((int)t.getPrice()));

        Trip t2 = t.getTrip2();
        if(t2 == null)
            return;
        findViewById(R.id.returnTripContainer).setVisibility(View.VISIBLE);
        ImageView vIcon2 = findViewById(R.id.vIcon2);
        vIcon2.setImageResource(getVehicleIcon(t2.getVehicle()));
        TextView returnDateText = findViewById(R.id.returnDateText),
                returnClassText = findViewById(R.id.returnClassText);
        returnClassText.setText(t.getSeatClass2().toString());
        returnDateText.setText(DATE_FORMAT.format(t2.getTime()));
    }

    private int getVehicleIcon(Vehicle v) {
        if(v instanceof Bus)
            return R.drawable.ic_bus_24dp;
        if(v instanceof MiniBus)
            return R.drawable.ic_minibus_24dp;
        if(v instanceof Car)
            return R.drawable.ic_car_24dp;
        return 0;
    }

    public void revokeTicket(View v) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
