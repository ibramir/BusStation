package com.ibramir.busstation.activities.trips;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ibramir.busstation.R;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.trips.TripManager;

import java.util.List;

public class DestinationSpinnerAdapter extends BaseAdapter implements AdapterView.OnItemSelectedListener {
    private Context context;
    private List<Trip> tripList;

    DestinationSpinnerAdapter(Context context, List<Trip> tripList) {
        this.context = context;
        this.tripList = tripList;
    }

    @Override
    public int getCount() {
        return tripList.size();
    }

    @Override
    public Object getItem(int position) {
        return tripList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View item = convertView;
        if(item == null)
            item = LayoutInflater.from(context).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
        TextView textView = item.findViewById(android.R.id.text1);
        textView.setText(tripList.get(position).getDestination());
        return item;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tripList.clear();
        Trip filter = (Trip) parent.getItemAtPosition(position);
        for(Trip t: TripManager.getInstance().getTrips()) {
            if(t.getSource().equals(filter.getSource()) && !tripList.contains(t))
                tripList.add(t);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
