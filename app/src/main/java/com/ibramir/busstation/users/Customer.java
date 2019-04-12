package com.ibramir.busstation.users;

import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.tickets.Ticket;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.vehicles.Vehicle;

import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nullable;

public class Customer extends User implements RetrieveListener<Ticket> {

    private Collection<Ticket> tickets;

    public Customer(String uid) {
        this(uid, null);
    }
    Customer(String uid, String email) {
        super(uid, email);
        tickets = new HashSet<>();
    }

    public Collection<Ticket> getTickets() {
        return tickets;
    }

    void setTickets(Collection<Ticket> tickets) {
        this.tickets = tickets;
    }

    public boolean reserveTicket(Trip trip1, @Nullable Trip trip2, int numOfSeats, Vehicle.SeatClass seatClass) {
        if(!trip1.getVehicle().availableSeats(numOfSeats, seatClass)
        || trip2 != null && !trip2.getVehicle().availableSeats(numOfSeats, seatClass)) {
            return false;
        }
        Ticket ticket = Ticket.reserveTicket(this, trip1, trip2, numOfSeats, seatClass);
        tickets.add(ticket);
        return true;
    }
    public void cancelReservation(String ticketId) {
        for(Ticket t: tickets) {
            if(t.equals(ticketId)) {
                t.cancelTicket();
                tickets.remove(t);
                return;
            }
        }
    }

    @Override
    public synchronized void onRetrieve(Ticket obj) {
        tickets.add(obj);
    }
}
