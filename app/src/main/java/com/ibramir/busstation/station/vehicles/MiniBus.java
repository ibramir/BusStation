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
        switch (seatClass) {
            case ECONOMY: economySeats -= numOfSeats; break;
            case COMFORT: comfortSeats -= numOfSeats; break;
        }
        super.reserveSeats(numOfSeats);
    }
    @Override
    public void cancelReservation(int numOfSeats, SeatClass seatClass) {
        switch (seatClass) {
            case ECONOMY: economySeats += numOfSeats; break;
            case COMFORT: comfortSeats += numOfSeats; break;
        }
        super.cancelReservation(numOfSeats);
    }

    @Override
    public boolean availableSeats(int numOfSeats, @Nullable SeatClass seatClass) {
        return getAvailableSeats(seatClass) >= numOfSeats;
    }
    @Override
    public int getAvailableSeats(@Nullable SeatClass seatClass) {
        if(seatClass == null)
            return super.getAvailableSeats();
        if(seatClass == SeatClass.ECONOMY)
            return economySeats;
        if(seatClass == SeatClass.COMFORT)
            return comfortSeats;
        return 0;
    }
    @Override
    public int getMaxSeats(SeatClass seatClass) {
        if(seatClass == null)
            return MAX_COMFORT + MAX_ECONOMY;
        if(seatClass == SeatClass.ECONOMY)
            return MAX_ECONOMY;
        if(seatClass == SeatClass.COMFORT)
            return MAX_COMFORT;
        return 0;
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

    @Override
    public boolean hasSeatClass(SeatClass seatClass) {
        return seatClass != SeatClass.LUXURY;
    }
}
