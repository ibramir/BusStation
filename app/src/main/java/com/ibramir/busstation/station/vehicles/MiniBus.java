package com.ibramir.busstation.station.vehicles;

import javax.annotation.Nullable;

public class MiniBus extends Vehicle {
    private static final int MAX_ECONOMY = 12;
    private final static int MAX_COMFORT = 4;

    private int economySeats = MAX_ECONOMY;
    private int comfortSeats = MAX_COMFORT;

    public MiniBus() {
        super(16);
    }

    @Override
    public void reserveSeats(int numOfSeats, SeatClass seatClass) {
        super.reserveSeats(numOfSeats);
        if(seatClass == SeatClass.ECONOMY)
            economySeats -= numOfSeats;
        else if(seatClass == SeatClass.COMFORT)
            comfortSeats -= numOfSeats;
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
    public double getSeatPrice(@Nullable SeatClass seatClass) {
        if(seatClass == SeatClass.COMFORT)
            return 20;
        return 0;
    }
}
