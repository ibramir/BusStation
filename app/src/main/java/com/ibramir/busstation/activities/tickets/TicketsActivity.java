package com.ibramir.busstation.activities.tickets;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ibramir.busstation.R;
import com.ibramir.busstation.station.tickets.Ticket;
import com.ibramir.busstation.users.Customer;
import com.ibramir.busstation.users.User;

import java.util.List;

public class TicketsActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private TicketsAdapter adapter;
    private List<Ticket> tickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.myTickets);
        toolbar.setTitleTextColor(Color.WHITE);
        recyclerView = findViewById(R.id.ticketsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Customer user = (Customer) User.getCurrentUser();
        tickets = user.getTickets();
        adapter = new TicketsAdapter(tickets, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        Ticket clicked = tickets.get(recyclerView.getChildAdapterPosition(v));
        //TODO on ticket click
    }
}
