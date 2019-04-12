package com.ibramir.busstation.station.tickets;

import com.google.firebase.firestore.ListenerRegistration;
import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.vehicles.Vehicle;
import com.ibramir.busstation.users.Customer;

import java.util.UUID;

import javax.annotation.Nullable;

public class Ticket implements RetrieveListener<Trip> {
    private static final double ROUND_RATE = 0.9;

    private ListenerRegistration listenerRegistration;

    private String ticketId;
    private String uid;
    private Trip trip1;
    private Trip trip2;
    private int numOfSeats;
    private double price;
    private Vehicle.SeatClass seatClass;

    public static Ticket reserveTicket(Customer customer, Trip trip,
                                       int numOfSeats, Vehicle.SeatClass seatClass) {
        return reserveTicket(customer, trip, null, numOfSeats, seatClass);
    }
    public static Ticket reserveTicket(Customer customer, Trip trip1, @Nullable Trip trip2,
                                       int numOfSeats, Vehicle.SeatClass seatClass) {
        Ticket t = new Ticket(customer.getUid(), trip1, trip2, numOfSeats, seatClass);
        trip1.reserveSeats(t);
        if(trip2 != null)
            trip2.reserveSeats(t);
        t.ticketId = UUID.randomUUID().toString();
        TicketManager.getInstance().save(t);
        return t;
    }

    private Ticket(String uid, Trip trip1, Trip trip2, int numOfSeats, Vehicle.SeatClass seatClass) {
        this.uid = uid;
        this.trip1 = trip1;
        this.trip2 = trip2;
        this.numOfSeats = numOfSeats;
        this.seatClass = seatClass;
        this.price = calculatePrice();
    }

    Ticket(String ticketId, String uid, int numOfSeats, double price, Vehicle.SeatClass seatClass) {
        this.ticketId = ticketId;
        this.uid = uid;
        this.numOfSeats = numOfSeats;
        this.price = price;
        this.seatClass = seatClass;
    }

    private double calculatePrice() {
        double rate = 1;
        double t2 = 0;
        if(trip2 != null) {
            rate = ROUND_RATE;
            t2 = trip2.getPrice() + trip2.getVehicle().getSeatPrice(seatClass);
        }
        double t1 = trip1.getPrice() + trip1.getVehicle().getSeatPrice(seatClass);
        return rate*(t1 + t2);
    }

    public void cancelTicket() {
        TicketManager.getInstance().delete(this);
    }

    public String getTicketId() {
        return ticketId;
    }
    public String getUid() {
        return uid;
    }
    public Trip getTrip1() {
        return trip1;
    }
    public Trip getTrip2() {
        return trip2;
    }
    public int getNumOfSeats() {
        return numOfSeats;
    }
    public double getPrice() {
        return price;
    }
    public Vehicle.SeatClass getSeatClass() {
        return seatClass;
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

    @Override
    public void onRetrieve(Trip obj) {
        if(trip1 == null)
            trip1 = obj;
        else if(trip2 == null)
            trip2 = obj;
    }
}
