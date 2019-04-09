package com.ibramir.busstation.station;

import android.support.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ibramir.busstation.station.vehicles.Vehicle;
import com.ibramir.busstation.users.Driver;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Trip {
    private DocumentReference documentReference;

    private String id;
    private String source;
    private String destination;
    private double price;
    private Date time;
    private Vehicle vehicle;
    private Driver driver;
    private Collection<ReservationInfo> reservations;
    private List<String> stops;

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
    public Date getTime() {
        return time;
    }
    public Vehicle getVehicle() {
        return vehicle;
    }
    public Driver getDriver() {
        return driver;
    }
    public Collection<ReservationInfo> getReservations() {
        return reservations;
    }
    public List<String> getStops() {
        return stops;
    }

    void initializeDocumentListener() {
        documentReference = FirebaseFirestore.getInstance().collection("trips")
                .document(id);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null) {
                    return;
                }
                if(documentSnapshot == null || !documentSnapshot.exists()) {
                    Trips.deleteTrip(Trip.this);
                    return;
                }
                updateData(documentSnapshot.getData());
            }
        });
    }
    private void updateData(Map<String, Object> data) {
        for(String field: data.keySet()) {
            //TODO update data
        }
    }

    public boolean reserveSeats(Ticket ticket) {
        if(vehicle.availableSeats() < ticket.getNumOfSeats())
            return false;
        reservations.add(new ReservationInfo(ticket.getUid(), ticket.getTicketId(), ticket.getNumOfSeats()));
        return true;
    }

    public static Trip ofId(String id) {
        //TODO from firebase
        return null;
    }
    private Trip() {
        this.id = UUID.randomUUID().toString();
    }
    public static class Builder {
        private String source;
        private String destination;
        private float price;
        private Date time;
        private Vehicle vehicle;
        private Driver driver;
        private Collection<ReservationInfo> reservations = null;
        private List<String> stops;

        public Builder from(String source) {
            this.source = source;
            return this;
        }
        public Builder to(String destination) {
            this.destination = destination;
            return this;
        }
        public Builder ofPrice(float price) {
            this.price = price;
            return this;
        }
        public Builder atTime(Date time) {
            this.time = time;
            return this;
        }
        public Builder withDriver(Driver driver) {
            this.driver = driver;
            return this;
        }
        public Builder withVehicle(Vehicle vehicle) {
            this.vehicle = vehicle;
            return this;
        }
        public Builder withCustomers(Collection<ReservationInfo> customersUid) {
            this.reservations = customersUid;
            return this;
        }
        public Builder withStops(List<String> stops) {
            this.stops = stops;
            return this;
        }

        public Trip build() {
            Trip trip = new Trip();
            trip.source = source;
            trip.destination = destination;
            trip.price = price;
            trip.time = time;
            trip.vehicle = vehicle;
            trip.driver = driver;
            trip.reservations = reservations;
            trip.stops = stops;
            new Trips.SaveTripTask().execute(trip);
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