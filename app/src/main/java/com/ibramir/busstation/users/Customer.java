package com.ibramir.busstation.users;

import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.tickets.Ticket;
import com.ibramir.busstation.station.trips.OnTripsChangedListener;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.trips.TripManager;
import com.ibramir.busstation.station.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class Customer extends User implements RetrieveListener<Ticket>, OnTripsChangedListener {

    private List<Ticket> tickets;

    Customer(String uid) {
        this(uid, null);
    }
    Customer(String uid, String email) {
        super(uid, email);
        tickets = new ArrayList<>();
        TripManager.getInstance().addTripsChangedListener(this);
    }
    Customer(String uid, String email, String name) {
        super(uid, email, name);
    }
    public List<Ticket> getTickets() {
        return tickets;
    }

    void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public boolean reserveTicket(Trip trip1, Vehicle.SeatClass seatClass, @Nullable Trip trip2, @Nullable Vehicle.SeatClass seatClass2,
                                 int numOfSeats) {
        if(!trip1.getVehicle().availableSeats(numOfSeats, seatClass)
        || trip2 != null && !trip2.getVehicle().availableSeats(numOfSeats, seatClass)) {
            return false;
        }
        Ticket ticket = Ticket.reserveTicket(this, trip1, seatClass, trip2, seatClass2, numOfSeats);
        tickets.add(ticket);
        return true;
    }
    private void cancelReservation(Ticket t) {
        t.revokeTicket();
        tickets.remove(t);
        UserManager.getInstance().save(this);
    }

    @Override
    public synchronized void onRetrieve(Ticket obj) {
        if(tickets.contains(obj))
            return;
        tickets.add(obj);
    }

    @Override
    public void onTripsChanged() {
        List<Trip> trips = TripManager.getInstance().getTrips();
        List<Ticket> ticketsCopy = new ArrayList<>(tickets);
        boolean changed = false;
        for(Ticket t: ticketsCopy) {
            if(!trips.contains(t.getTrip()) || t.getTrip2()!= null && !trips.contains(t.getTrip2())) {
                cancelReservation(t);
                changed = true;
            }
        }
        if(changed)
            UserManager.getInstance().save(this);
    }
}
