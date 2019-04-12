package com.ibramir.busstation.activities.trips;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibramir.busstation.R;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.vehicles.Bus;
import com.ibramir.busstation.station.vehicles.Car;
import com.ibramir.busstation.station.vehicles.MiniBus;
import com.ibramir.busstation.station.vehicles.Vehicle;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.TripHolder> {

    private Context context;
    private View.OnClickListener onClickListener;
    private List<Trip> data;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM\nHH:mm", Locale.ENGLISH);

    TripsAdapter(Context context, List<Trip> data) {
        this.context = context;
        this.data = data;
    }

    void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public TripHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.trip_holder, viewGroup, false);
        itemView.setOnClickListener(onClickListener);
        return new TripHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull TripHolder tripHolder, int i) {
        Trip t = data.get(i);
        tripHolder.sourceText.setText(t.getSource());
        tripHolder.destinationText.setText(t.getDestination());
        tripHolder.timeText.setText(formatter.format(t.getTime()));
        if(t.isFull()) {
            tripHolder.availableText.setTextColor(ContextCompat.getColor(context,R.color.fullRed));
            tripHolder.availableText.setText("FULL");
        }
        else {
            tripHolder.availableText.setTextColor(Color.BLACK);
            tripHolder.availableText.setText(String.valueOf((int)t.getBasePrice()));
        }
        Vehicle v = t.getVehicle();
        if(v instanceof Bus)
            tripHolder.vehicleIcon.setImageResource(R.drawable.ic_bus_24dp);
        else if(v instanceof MiniBus)
            tripHolder.vehicleIcon.setImageResource(R.drawable.ic_minibus_24dp);
        else if(v instanceof Car)
            tripHolder.vehicleIcon.setImageResource(R.drawable.ic_car_24dp);
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    static class TripHolder extends RecyclerView.ViewHolder {
        private TextView sourceText, destinationText, timeText, availableText;
        private ImageView vehicleIcon;
        TripHolder(@NonNull View itemView) {
            super(itemView);
            this.sourceText = itemView.findViewById(R.id.sourceText);
            this.destinationText = itemView.findViewById(R.id.destinationText);
            this.timeText = itemView.findViewById(R.id.timeText);
            this.availableText = itemView.findViewById(R.id.availableText);
            this.vehicleIcon = itemView.findViewById(R.id.vIcon);
        }
    }

}
