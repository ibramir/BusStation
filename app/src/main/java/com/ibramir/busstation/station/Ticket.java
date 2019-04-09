package com.ibramir.busstation.station;

import java.util.UUID;

public class Ticket {
    public static final double TWO_WAY_RATE = 0.85;

    private String ticketId;
    private String uid;
    private String trip1Id;
    private String trip2Id;
    private int numOfSeats;
    private double price;

    public Ticket(String uid, String trip1Id, String trip2Id, int numOfSeats, double price) {
        this.uid = uid;
        this.trip1Id = trip1Id;
        this.trip2Id = trip2Id;
        this.numOfSeats = numOfSeats;
        this.price = price;
        this.ticketId = UUID.randomUUID().toString();
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
}
