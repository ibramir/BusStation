package com.ibramir.busstation.station;

import com.google.firebase.firestore.ListenerRegistration;
import com.ibramir.busstation.station.vehicles.Vehicle;
import com.ibramir.busstation.users.Customer;

import java.util.UUID;

import javax.annotation.Nullable;

public class Ticket {
    public static final double TWO_WAY_RATE = 0.85;

    private ListenerRegistration listenerRegistration;

    private String ticketId;
    private String uid;
    private String trip1Id;
    private String trip2Id;
    private int numOfSeats;
    private double price;
    private Vehicle.SeatClass seatClass;

    public static Ticket reserveTicket(Customer customer, Trip trip1, @Nullable Trip trip2,
                                       int numOfSeats, Vehicle.SeatClass seatClass) {
        String trip2Id = null;
        if(trip2 != null)
            trip2Id = trip2.getId();
        Ticket t = new Ticket(customer.getUid(), trip1.getId(), trip2Id, numOfSeats, seatClass);
        //TODO reserve
        return t;
    }

    public Ticket(String uid, String trip1Id, String trip2Id, int numOfSeats, Vehicle.SeatClass seatClass) {
        this.uid = uid;
        this.trip1Id = trip1Id;
        this.trip2Id = trip2Id;
        this.numOfSeats = numOfSeats;
        this.seatClass = seatClass;
        this.ticketId = UUID.randomUUID().toString();
        this.price = calculatePrice();
    }

    private double calculatePrice() {
        //TODO calculate price
        return 0.0;
    }

    public void cancelTicket() {
        //TODO cancel
    }

    public String getTicketId() {
        return ticketId;
    }
    public String getUid() {
        return uid;
    }
    public String getTrip1Id() {
        return trip1Id;
    }
    public String getTrip2Id() {
        return trip2Id;
    }
    public int getNumOfSeats() {
        return numOfSeats;
    }
    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == this)
            return true;
        if(obj instanceof String)
            return this.ticketId.equals(obj);
        if(!(obj instanceof Ticket))
            return false;
        return this.ticketId.equals(((Ticket)obj).ticketId);
    }
}
