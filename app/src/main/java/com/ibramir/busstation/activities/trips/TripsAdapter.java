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
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d/M\nh:mm aa", Locale.ENGLISH);

    TripsAdapter(Context context, List<Trip> data) {
        this.context = context;
        this.data = data;
        notifyDataSetChanged();
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
        tripHolder.updateData(data.get(i));
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    class TripHolder extends RecyclerView.ViewHolder {
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

        private void updateData(Trip t) {
            sourceText.setText(t.getSource());
            destinationText.setText(t.getDestination());
            timeText.setText(DATE_FORMAT.format(t.getTime()));
            if(t.getVehicle() == null)
                return;
            if(t.isFull()) {
                availableText.setTextColor(ContextCompat.getColor(context,R.color.fullRed));
                availableText.setText("FULL");
            }
            else {
                availableText.setTextColor(Color.BLACK);
                availableText.setText(String.valueOf(t.getAvailableSeats()));
            }

            Vehicle v = t.getVehicle();
            if(v instanceof Bus)
                vehicleIcon.setImageResource(R.drawable.ic_bus_24dp);
            else if(v instanceof MiniBus)
                vehicleIcon.setImageResource(R.drawable.ic_minibus_24dp);
            else if(v instanceof Car)
                vehicleIcon.setImageResource(R.drawable.ic_car_24dp);
        }
    }

}
