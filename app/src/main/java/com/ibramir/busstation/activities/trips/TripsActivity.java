package com.ibramir.busstation.activities.trips;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.ibramir.busstation.R;
import com.ibramir.busstation.activities.picker.PickerActivity;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.trips.TripManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Date dateFilter;
    private RecyclerView recyclerView;
    private TripsAdapter adapter;
    private List<Trip> filteredTrips;
    private Trip filter;
    private String mode;

    private Trip trip1, trip2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);
        mode = getIntent().getAction();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.tripsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        filteredTrips = new ArrayList<>();
        adapter = new TripsAdapter(this, filteredTrips);
        adapter.setOnClickListener(this);
        recyclerView.setAdapter(adapter);
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
                        if(filter != null) {
                            updateData();
                        }
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dateFilter = null;
            }
        });
        datePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mode.equals(PickerActivity.BOOK_ONE)) {
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
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("Filter trips");
        builder.setCancelable(true);
        View body =getLayoutInflater().inflate(R.layout.trips_filter, null);

        final TextView dateText = body.findViewById(R.id.dateText);
        dateText.setKeyListener(null);
        Spinner sourceSpinner = body.findViewById(R.id.sourceSpinner);
        sourceSpinner.setAdapter(new SourceSpinnerAdapter(this));
        Spinner destinationSpinner = body.findViewById(R.id.destinationSpinner);
        DestinationSpinnerAdapter destinationAdapter = new DestinationSpinnerAdapter(this, new ArrayList<Trip>());
        destinationSpinner.setAdapter(destinationAdapter);
        sourceSpinner.setOnItemSelectedListener(destinationAdapter);
        destinationSpinner.setOnItemSelectedListener(this);
        body.findViewById(R.id.dateText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPicker(dateText);
            }
        });
        builder.setView(body).create().show();
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildAdapterPosition(v);
        //TODO clicked trip
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        filteredTrips.clear();
        filter = (Trip) parent.getItemAtPosition(position);
        updateData();
    }

    private void updateData() {
        if (dateFilter == null) {
            for (Trip t : TripManager.getInstance().getTrips()) {
                if (t.getSource().equals(filter.getSource())
                        && t.getDestination().equals(filter.getDestination()))
                    filteredTrips.add(t);
            }
        }
        else {
            for (Trip t : TripManager.getInstance().getTrips()) {
                if (t.getSource().equals(filter.getSource())
                        && t.getDestination().equals(filter.getDestination())
                        && t.getTime().after(dateFilter))
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
        //TODO selected trip?
    }
}
