package com.ibramir.busstation.activities.trips;

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

import java.util.ArrayList;
import java.util.List;

class DestinationSpinnerAdapter extends BaseAdapter implements AdapterView.OnItemSelectedListener {
    private TripsActivity context;
    private List<String> destinationList;

    DestinationSpinnerAdapter(TripsActivity context) {
        this.context = context;
        this.destinationList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return destinationList.size();
    }

    @Override
    public String getItem(int position) {
        if(position >= destinationList.size())
            return null;
        return destinationList.get(position);
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
        if(destinationList.size() <= position)
            return item;
        TextView textView = item.findViewById(android.R.id.text1);
        textView.setText(getItem(position));
        return item;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        destinationList.clear();
        String filter = (String) parent.getItemAtPosition(position);
        if(!filter.equals("All"))
            for (Trip t : TripManager.getInstance().getTrips()) {
                if (t.getSource().equals(filter) && !destinationList.contains(t.getDestination()))
                    destinationList.add(t.getDestination());
            }
        context.setSourceFilter(filter);
        notifyDataSetChanged();
        context.setDestinationFilter(getItem(0));
        context.updateData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
