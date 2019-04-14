package com.ibramir.busstation.station.tickets;

import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.vehicles.Vehicle;
import com.ibramir.busstation.users.Customer;

import java.util.UUID;

import javax.annotation.Nullable;

public class Ticket implements RetrieveListener<Trip> {
    private static final double ROUND_RATE = 0.9;

    private String ticketId;
    private String uid;
    private Trip trip1, trip2;
    private int numOfSeats;
    private double price;
    private Vehicle.SeatClass seatClass, seatClass2;

    public static Ticket reserveTicket(Customer customer, Trip trip1, Vehicle.SeatClass seatClass, @Nullable Trip trip2,
                                       @Nullable Vehicle.SeatClass seatClass2, int numOfSeats) {
        Ticket t = new Ticket(customer.getUid(), trip1, seatClass, trip2, seatClass2, numOfSeats);
        trip1.reserveSeats(t, seatClass);
        if(trip2 != null)
            trip2.reserveSeats(t, seatClass2);
        t.ticketId = UUID.randomUUID().toString();
        TicketManager.getInstance().save(t);
        return t;
    }

    private Ticket(String uid, Trip trip1, Vehicle.SeatClass seatClass, Trip trip2, Vehicle.SeatClass seatClass2,
                   int numOfSeats) {
        this.uid = uid;
        this.trip1 = trip1;
        this.trip2 = trip2;
        this.numOfSeats = numOfSeats;
        this.seatClass = seatClass;
        this.seatClass2 = seatClass2;
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
            t2 = trip2.getPrice() + trip2.getVehicle().getSeatPrice(seatClass2);
        }
        double t1 = trip1.getPrice() + trip1.getVehicle().getSeatPrice(seatClass);
        return numOfSeats*rate*(t1 + t2);
    }

    public void revokeTicket() {
        trip1.cancelReservation(numOfSeats, seatClass);
        if(trip2 != null)
            trip2.cancelReservation(numOfSeats, seatClass2);
        TicketManager.getInstance().delete(this);
    }

    public String getTicketId() {
        return ticketId;
    }
    public String getUid() {
        return uid;
    }
    public Trip getTrip() {
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
    public Vehicle.SeatClass getSeatClass2() {
        return seatClass2;
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
