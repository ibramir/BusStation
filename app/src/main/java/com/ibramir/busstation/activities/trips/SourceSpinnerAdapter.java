package com.ibramir.busstation.activities.trips;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ibramir.busstation.R;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.trips.TripManager;

import java.util.ArrayList;
import java.util.List;

class SourceSpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<String> sourceList;

    SourceSpinnerAdapter(Context context) {
        this.context = context;
        sourceList = new ArrayList<>();
        sourceList.add("All");
        for(Trip t: TripManager.getInstance().getTrips()) {
            sourceList.add(t.getSource());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return sourceList.size();
    }

    @Override
    public String getItem(int position) {
        return sourceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View item = convertView;
        if(item == null)
            item = LayoutInflater.from(context).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
        TextView textView = item.findViewById(android.R.id.text1);
        textView.setText(getItem(position));
        return item;
    }
}
