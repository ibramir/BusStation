package com.ibramir.busstation.activities.tickets;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibramir.busstation.R;
import com.ibramir.busstation.station.tickets.Ticket;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.vehicles.Bus;
import com.ibramir.busstation.station.vehicles.Car;
import com.ibramir.busstation.station.vehicles.MiniBus;
import com.ibramir.busstation.station.vehicles.Vehicle;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.TicketHolder> {
    private List<Ticket> tickets;
    private View.OnClickListener onItemClick;
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("h:mm a - d/M", Locale.ENGLISH);

    TicketsAdapter(List<Ticket> tickets, View.OnClickListener onItemClick) {
        this.tickets = tickets;
        this.onItemClick = onItemClick;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TicketHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.ticket_holder, viewGroup, false);
        itemView.setOnClickListener(onItemClick);
        return new TicketHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketHolder ticketHolder, int i) {
        ticketHolder.updateData(tickets.get(i));
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    class TicketHolder extends RecyclerView.ViewHolder {
        private TextView destinationText, dateText, destinationText2, dateText2, priceText;
        private ImageView vIcon, vIcon2;
        private ConstraintLayout returnTripContainer;
        private TicketHolder(@NonNull View itemView) {
            super(itemView);
            destinationText = itemView.findViewById(R.id.destinationText);
            dateText = itemView.findViewById(R.id.dateText);
            destinationText2 = itemView.findViewById(R.id.destinationText2);
            dateText2 = itemView.findViewById(R.id.dateText2);
            priceText = itemView.findViewById(R.id.priceText);
            vIcon = itemView.findViewById(R.id.vIcon);
            vIcon2 = itemView.findViewById(R.id.vIcon2);
            returnTripContainer = itemView.findViewById(R.id.returnTripContainer);
        }

        private void updateData(Ticket t) {
            Trip t1 = t.getTrip();
            destinationText.setText(t1.getSource()+" - "+ t1.getDestination());
            dateText.setText(DATE_FORMAT.format(t1.getTime()));
            vIcon.setImageResource(getVehicleIcon(t1));
            priceText.setText(String.valueOf((int)t.getPrice()));
            Trip t2 = t.getTrip2();
            if(t2 == null) {
                returnTripContainer.setVisibility(View.GONE);
                return;
            }
            returnTripContainer.setVisibility(View.VISIBLE);
            destinationText2.setText(t2.getSource()+" - "+t2.getDestination());
            dateText2.setText(DATE_FORMAT.format(t2.getTime()));
            vIcon2.setImageResource(getVehicleIcon(t2));
        }
        private int getVehicleIcon(Trip t) {
            Vehicle v = t.getVehicle();
            if(v instanceof Bus)
                return R.drawable.ic_bus_24dp;
            if(v instanceof MiniBus)
                return R.drawable.ic_minibus_24dp;
            if(v instanceof Car)
                return R.drawable.ic_car_24dp;
            return -1;
        }
    }
}
