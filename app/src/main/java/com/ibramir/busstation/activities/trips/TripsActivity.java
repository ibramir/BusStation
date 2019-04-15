package com.ibramir.busstation.activities.trips;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.ibramir.busstation.R;
import com.ibramir.busstation.activities.picker.PickerActivity;
import com.ibramir.busstation.station.trips.OnTripsChangedListener;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.trips.TripManager;
import com.ibramir.busstation.station.vehicles.Vehicle;
import com.ibramir.busstation.users.Customer;
import com.ibramir.busstation.users.Driver;
import com.ibramir.busstation.users.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final int RC_BOOK = 100;
    public static final int RC_BOOK_ROUND = 101;

    private Date dateFilter = null;
    private String sourceFilter = "All", destinationFilter;
    private RecyclerView recyclerView;
    private TripsAdapter adapter;
    private List<Trip> allTrips;
    private List<Trip> filteredTrips;
    private String mode;
    private Toolbar toolbar;
    private BaseAdapter sourceAdapter, destinationAdapter;
    private AlertDialog filterDialog;
    private OnTripsChangedListener changeListener = new OnTripsChangedListener() {
        @Override
        public void onTripsChanged() {
            findViewById(R.id.progressFrame).setVisibility(View.GONE);
            toolbar = findViewById(R.id.toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
            recyclerView = findViewById(R.id.tripsRecycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(TripsActivity.this));
            recyclerView.setHasFixedSize(true);
            if(User.getCurrentUser() instanceof Driver)
                initializeDriver();
            else if(User.getCurrentUser() instanceof Customer)
                initialize();
        }
    };

    private Driver driver;
    private Trip trip1, trip2;
    private Vehicle.SeatClass seatClass1, seatClass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);
        mode = getIntent().getAction();

        TripManager.getInstance().fetchTrips(changeListener);
    }

    private void initializeDriver() {
        toolbar.setTitle(R.string.assignedTrips);
        driver = (Driver) User.getCurrentUser();
        allTrips = driver.getAssignedTrips();
        filteredTrips = new ArrayList<>();
        adapter = new TripsAdapter(this, filteredTrips);
        updateData();
    }
    private void initialize() {
        toolbar.setTitle(R.string.bookOne);

        allTrips = TripManager.getInstance().getTrips();
        filteredTrips = new ArrayList<>();

        sourceAdapter = new SourceSpinnerAdapter(this);
        destinationAdapter = new DestinationSpinnerAdapter(this);

        adapter = new TripsAdapter(this, filteredTrips);
        adapter.setOnClickListener(this);
        recyclerView.setAdapter(adapter);

        changeListener = new OnTripsChangedListener() {
            @Override
            public void onTripsChanged() {
                updateData();
                adapter.notifyDataSetChanged();
            }
        };

        updateData();
    }

    void setSourceFilter(String sourceFilter) {
        this.sourceFilter = sourceFilter;
    }
    void setDestinationFilter(String destinationFilter) {
        this.destinationFilter = destinationFilter;
    }

    private void openPicker(final TextView dateText) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        c.set(year, month, dayOfMonth, 0, 0, 0);
                        dateFilter = c.getTime();
                        dateText.setText(new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(dateFilter));
                        updateData();
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dateFilter = null;
                dateText.setText("");
                updateData();
            }
        });
        datePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mode == null)
            return super.onCreateOptionsMenu(menu);
        else if(mode.equals(PickerActivity.BOOK_ONE) || mode.equals(PickerActivity.BOOK_ROUND)) {
            getMenuInflater().inflate(R.menu.trips_menu, menu);
            return true;
        }
        else if(mode.equals(PickerActivity.MANAGE)) {
            getMenuInflater().inflate(R.menu.manage_menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_action:
                openFilter();
                return true;
            case R.id.add_action:
                //TODO add trip activity
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void openFilter() {
        if(filterDialog != null) {
            filterDialog.show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter trips");
        builder.setCancelable(true);

        View body = getLayoutInflater().inflate(R.layout.trips_filter, null);
        final TextView dateText = body.findViewById(R.id.dateText);
        dateText.setKeyListener(null);
        Spinner sourceSpinner = body.findViewById(R.id.sourceSpinner);
        Spinner destinationSpinner = body.findViewById(R.id.destinationSpinner);
        if(!(mode.equals(PickerActivity.BOOK_ROUND) && trip1 != null)) {
            sourceSpinner.setAdapter(sourceAdapter);
            destinationSpinner.setAdapter(destinationAdapter);
            sourceSpinner.setOnItemSelectedListener((DestinationSpinnerAdapter) destinationAdapter);
            destinationSpinner.setOnItemSelectedListener(this);
        }
        else {
            sourceSpinner.setVisibility(View.GONE);
            destinationSpinner.setVisibility(View.GONE);
        }
        body.findViewById(R.id.dateText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPicker(dateText);
            }
        });
        filterDialog = builder.setView(body).create();
        filterDialog.show();
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildAdapterPosition(v);
        Trip clicked = filteredTrips.get(pos);
        if(trip1 == null)
            trip1 = clicked;
        else
            trip2 = clicked;
        Intent intent = new Intent(this, ViewTripActivity.class);
        intent.putExtra("tripId",clicked.getId());
        startActivityForResult(intent,
                (mode.equals(PickerActivity.BOOK_ONE)? RC_BOOK: RC_BOOK_ROUND));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        destinationFilter = (String) parent.getItemAtPosition(position);
        updateData();
    }

    void updateData() {
        filteredTrips.clear();
        if(sourceFilter.equals("All")) {
            if(dateFilter == null)
                filteredTrips.addAll(allTrips);
            else {
                Calendar c = Calendar.getInstance();
                c.setTime(dateFilter);
                c.add(Calendar.DAY_OF_MONTH, 1);
                Date date = c.getTime();
                for(Trip t: allTrips) {
                    Date time = t.getTime();
                    if(time.after(dateFilter) && time.before(date) || time.equals(dateFilter))
                        filteredTrips.add(t);
                }
            }
        }
        else if (dateFilter == null) {
            for (Trip t : allTrips) {
                if (t.getSource().equals(sourceFilter)
                        && t.getDestination().equals(destinationFilter))
                    filteredTrips.add(t);
            }
        }
        else {
            Calendar c = Calendar.getInstance();
            c.setTime(dateFilter);
            c.add(Calendar.DAY_OF_MONTH, 1);
            Date date = c.getTime();
            for (Trip t : allTrips) {
                Date time = t.getTime();
                if (t.getSource().equals(sourceFilter)
                        && t.getDestination().equals(destinationFilter)
                        && (time.after(dateFilter) && time.before(date) || time.equals(dateFilter)))
                    filteredTrips.add(t);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_BOOK || requestCode == RC_BOOK_ROUND) {
            if(resultCode == RESULT_OK) {
                if(requestCode == RC_BOOK) {
                    seatClass1 = (Vehicle.SeatClass) data.getSerializableExtra("seatClass");
                    finish();
                    return;
                }
                if(trip2 == null) {
                    seatClass1 = (Vehicle.SeatClass) data.getSerializableExtra("seatClass");
                    toolbar.setTitle(R.string.bookTwo);
                    sourceFilter = trip1.getDestination();
                    destinationFilter = trip1.getSource();
                    if(filterDialog != null) {
                        filterDialog.findViewById(R.id.sourceSpinner).setVisibility(View.GONE);
                        filterDialog.findViewById(R.id.destinationSpinner).setVisibility(View.GONE);
                    }
                    updateData();
                }
                else {
                    seatClass2 = (Vehicle.SeatClass) data.getSerializableExtra("seatClass");
                    finish();
                }
            }
            else if(trip2 != null)
                trip2 = null;
            else {
                trip1 = null;
            }
        }
    }

    @Override
    public void finish() {
        if(User.getCurrentUser() instanceof Customer && trip1 != null) {
            Customer customer = (Customer) User.getCurrentUser();
            customer.reserveTicket(trip1, seatClass1, trip2, seatClass2, 1);
        }
        super.finish();
    }
}
