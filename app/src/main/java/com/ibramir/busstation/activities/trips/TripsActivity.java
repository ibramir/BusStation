package com.ibramir.busstation.activities.trips;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.ibramir.busstation.R;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.trips.TripManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText dateText;
    private Date dateFilter;
    private RecyclerView recyclerView;
    private TripsAdapter adapter;
    private List<Trip> filteredTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        dateText = findViewById(R.id.dateText);
        dateText.setKeyListener(null);
        recyclerView = findViewById(R.id.tripsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        filteredTrips = new ArrayList<>();
        adapter = new TripsAdapter(this, filteredTrips);
        adapter.setOnClickListener(this);
        recyclerView.setAdapter(adapter);
        Spinner sourceSpinner = findViewById(R.id.sourceSpinner);
        sourceSpinner.setAdapter(new SourceSpinnerAdapter(this));
        Spinner destinationSpinner = findViewById(R.id.destinationSpinner);
        DestinationSpinnerAdapter destinationAdapter = new DestinationSpinnerAdapter(this, new ArrayList<Trip>());
        destinationSpinner.setAdapter(destinationAdapter);
        sourceSpinner.setOnItemSelectedListener(destinationAdapter);
        destinationSpinner.setOnItemSelectedListener(this);
    }

    public void openPicker(View v) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        c.set(year, month, dayOfMonth, 0, 0, 0);
                        dateFilter = c.getTime();
                        dateText.setText(new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(dateFilter));
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
    public void onClick(View v) {
        int pos = recyclerView.getChildAdapterPosition(v);
        //TODO clicked trip
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        filteredTrips.clear();
        Trip filter = (Trip) parent.getItemAtPosition(position);
        for(Trip t: TripManager.getInstance().getTrips()) {
            if(t.getSource().equals(filter.getSource()) && t.getDestination().equals(filter.getDestination()))
                filteredTrips.add(t);
        }
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
