package com.ibramir.busstation.station.trips;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.tickets.Ticket;
import com.ibramir.busstation.station.vehicles.Vehicle;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

public class Trip implements RetrieveListener<Vehicle> {
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

    /*void initializeDataListener() {
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
    }*/
    void updateData(Map<String, Object> data) {
        source = (String) data.get("source");
        destination = (String) data.get("destination");
        price = (double) data.get("price");
        time = ((Timestamp)data.get("time")).toDate();
        driverId = ((DocumentReference)data.get("driver")).getId();
        Collection<String> tickets = (Collection<String>) data.get("tickets");
        if(tickets != null)
            ticketIds.addAll(tickets);
    }
    public void deleteTrip() {
        vehicle.deleteVehicle();
        TripManager.getInstance().delete(this);
    }

    public boolean isFull() {
        return !vehicle.availableSeats(1, null);
    }
    public int getAvailableSeats() {
        return vehicle.getAvailableSeats();
    }
    public void reserveSeats(Ticket ticket, Vehicle.SeatClass seatClass) {
        vehicle.reserveSeats(ticket.getNumOfSeats(), seatClass);
        ticketIds.add(ticket.getTicketId());
        TripManager.getInstance().save(this);
    }
    public void cancelReservation(Ticket t) {
        cancelReservation(t, false);
    }
    public void cancelReservation(Ticket t, boolean roundTrip) {
        vehicle.cancelReservation(t.getNumOfSeats(),(roundTrip)?t.getSeatClass2():t.getSeatClass());
        TripManager.getInstance().save(this);
    }

    @Override
    public void onRetrieve(Vehicle obj) {
        vehicle = obj;
        vehicle.setAssignedTrip(this);
        TripManager.getInstance().notifyListeners();
    }

    public static Trip ofId(String id) {
        return new Trip(id);
    }
    private Trip() { }
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

        private boolean newID = false;

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
        public Builder newID(boolean newID) {
            this.newID = newID;
            return this;
        }

        public Trip build() {
            Trip trip = new Trip();
            trip.source = source;
            trip.destination = destination;
            trip.price = price;
            trip.time = time;
            trip.vehicle = vehicle;
            if(vehicle != null)
                vehicle.setAssignedTrip(trip);
            trip.driverId = driverId;
            trip.ticketIds = ticketIds;
            if(ticketIds == null)
                trip.ticketIds = new HashSet<>();
            if(newID)
                trip.id = UUID.randomUUID().toString();
            else
                trip.id = id;
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
