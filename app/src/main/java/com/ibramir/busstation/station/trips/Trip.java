package com.ibramir.busstation.station.trips;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.tickets.Ticket;
import com.ibramir.busstation.station.vehicles.Vehicle;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

public class Trip implements RetrieveListener<Vehicle> {
    private ListenerRegistration listenerRegistration;

    private String id;
    private String source;
    private String destination;
    private double price;
    private Date time;
    private Vehicle vehicle;
    private String driverId;
    private Collection<String> ticketIds;

    public String getId() {
        return id;
    }
    public String getSource() {
        return source;
    }
    public String getDestination() {
        return destination;
    }
    public double getPrice() {
        return price;
    }
    public double getBasePrice() {
        return price + vehicle.getSeatPrice(null);
    }
    public Date getTime() {
        return time;
    }
    public Vehicle getVehicle() {
        return vehicle;
    }
    public String getDriverId() {
        return driverId;
    }
    public Collection<String> getTicketIds() {
        return ticketIds;
    }
    public ListenerRegistration getListenerRegistration() {
        return listenerRegistration;
    }

    void initializeDataListener() {
        DocumentReference tripReference = FirebaseFirestore.getInstance().collection("trips")
                .document(id);
        listenerRegistration = tripReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null) {
                    return;
                }
                if(documentSnapshot == null || !documentSnapshot.exists()) {
                    TripManager.getInstance().delete(Trip.this);
                    return;
                }
                updateData(documentSnapshot.getData());
            }
        });
    }
    private void updateData(Map<String, Object> data) {
        source = (String) data.get("source");
        destination = (String) data.get("destination");
        price = (double) data.get("price");
        time = ((Timestamp)data.get("time")).toDate();
        driverId = (String) data.get("driverId");
        ticketIds.addAll((Collection<String>) data.get("ticketIds"));
    }

    public boolean isFull() {
        return vehicle.availableSeats(1, null);
    }
    public int getAvailableSeats() {
        return vehicle.getAvailableSeats();
    }
    public void reserveSeats(Ticket ticket) {
        vehicle.reserveSeats(ticket.getNumOfSeats(), ticket.getSeatClass());

    }

    @Override
    public void onRetrieve(Vehicle obj) {
        vehicle = obj;
    }

    public static Trip ofId(String id) {
        return new Trip(id);
    }
    private Trip() {
        this.id = UUID.randomUUID().toString();
    }
    private Trip(String id) {
        this.id = id;
    }
    public static class Builder {
        private String id = null;
        private String source;
        private String destination;
        private double price;
        private Date time;
        private Vehicle vehicle;
        private String driverId;
        private Collection<String> ticketIds = null;

        public Builder ofId(String id) {
            this.id = id;
            return this;
        }
        public Builder from(String source) {
            this.source = source;
            return this;
        }
        public Builder to(String destination) {
            this.destination = destination;
            return this;
        }
        public Builder ofPrice(double price) {
            this.price = price;
            return this;
        }
        public Builder atTime(Date time) {
            this.time = time;
            return this;
        }
        public Builder withDriver(String driver) {
            this.driverId = driver;
            return this;
        }
        public Builder withVehicle(Vehicle vehicle) {
            this.vehicle = vehicle;
            return this;
        }
        public Builder withVehicle(Vehicle.Type type) {
            this.vehicle = Vehicle.createVehicle(type);
            return this;
        }
        public Builder withCustomers(Collection<String> customersUid) {
            this.ticketIds = customersUid;
            return this;
        }
        public Builder withTickets(Collection<String> ticketIds) {
            this.ticketIds = ticketIds;
            return this;
        }

        public Trip build() {
            Trip trip;
            if(id != null)
                trip = new Trip(id);
            else
                trip = new Trip();
            trip.source = source;
            trip.destination = destination;
            trip.price = price;
            trip.time = time;
            trip.vehicle = vehicle;
            trip.driverId = driverId;
            trip.ticketIds = ticketIds;
            if(id == null)
                TripManager.getInstance().save(trip);
            return trip;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(this == obj)
            return true;
        if(!(obj instanceof Trip))
            return false;
        return this.id.equals(((Trip)obj).id);
    }
}
