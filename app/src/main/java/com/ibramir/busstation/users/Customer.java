package com.ibramir.busstation.users;

import com.ibramir.busstation.station.Ticket;
import com.ibramir.busstation.station.Trip;

import java.util.Collection;
import java.util.HashSet;

public class Customer extends User {

    private Collection<Ticket> tickets;

    public Customer(String uid) {
        this(uid, null);
    }
    Customer(String uid, String email) {
        super(uid, email);
        tickets = new HashSet<>();
    }

    public boolean reserveSeats(Trip trip1, Trip trip2, int numOfSeats) {
        double price = trip1.getPrice();
        String trip2Id = null;
        if(trip2 != null) {
            trip2Id = trip2.getId();
            price += trip2.getPrice();
            price *= Ticket.TWO_WAY_RATE;
        }
        Ticket ticket = new Ticket(getUid(), trip1.getId(), trip2Id, numOfSeats, price);
        if(trip1.reserveSeats(ticket)) {
            tickets.add(ticket);
            //TODO firebase reserve
            return true;
        }
        return false;
    }
    public void cancelReservation(String ticketId) {
        //TODO cancel
    }

    @Override
    public boolean saveInfo() {
        return false;
    }
}
