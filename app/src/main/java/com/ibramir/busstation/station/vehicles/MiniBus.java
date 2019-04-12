package com.ibramir.busstation.station.vehicles;

import com.google.firebase.firestore.DocumentSnapshot;

import javax.annotation.Nullable;

public class MiniBus extends Vehicle {
    private static final int MAX_ECONOMY = 12;
    private final static int MAX_COMFORT = 4;

    private int economySeats = MAX_ECONOMY;
    private int comfortSeats = MAX_COMFORT;

    MiniBus() {
        super(16);
    }

    public int getEconomySeats() {
        return economySeats;
    }
    public int getComfortSeats() {
        return comfortSeats;
    }

    @Override
    public void reserveSeats(int numOfSeats, SeatClass seatClass) {
        if(seatClass == SeatClass.ECONOMY)
            economySeats -= numOfSeats;
        else if(seatClass == SeatClass.COMFORT)
            comfortSeats -= numOfSeats;
        super.reserveSeats(numOfSeats);
    }
    @Override
    public boolean availableSeats(int numOfSeats, @Nullable SeatClass seatClass) {
        if(seatClass == null)
            return getAvailableSeats() >= numOfSeats;
        if(seatClass == SeatClass.ECONOMY)
            return economySeats-numOfSeats >= 0;
        if(seatClass == SeatClass.COMFORT)
            return comfortSeats-numOfSeats >= 0;
        return false;
    }

    @Override
    void initFromDocument(DocumentSnapshot d) {
        economySeats = d.getLong("economySeats").intValue();
        comfortSeats = d.getLong("comfortSeats").intValue();
    }

    @Override
    public double getSeatPrice(@Nullable SeatClass seatClass) {
        if(seatClass == SeatClass.COMFORT)
            return 20;
        return 0;
    }
}
