package com.ibramir.busstation.station.vehicles;

import javax.annotation.Nullable;

public class Bus extends Vehicle {
    private static final int MAX_ECONOMY = 40;
    private static final int MAX_COMFOT = 15;
    private static final int MAX_LUXURY = 5;

    private int economySeats = MAX_ECONOMY;
    private int comfortSeats = MAX_COMFOT;
    private int luxurySeats = MAX_LUXURY;

    public Bus() {
        super(60);
    }

    @Override
    public void reserveSeats(int numOfSeats, SeatClass seatClass) {
        super.reserveSeats(numOfSeats);
        if(seatClass == SeatClass.ECONOMY)
            economySeats -= numOfSeats;
        else if(seatClass == SeatClass.COMFORT)
            comfortSeats -= numOfSeats;
        else if(seatClass == SeatClass.LUXURY)
            luxurySeats -= numOfSeats;
    }
    @Override
    public boolean availableSeats(int numOfSeats, @Nullable SeatClass seatClass) {
        if(seatClass == null)
            return getAvailableSeats() >= numOfSeats;
        if(seatClass == SeatClass.ECONOMY)
            return economySeats >= numOfSeats;
        if(seatClass == SeatClass.COMFORT)
            return comfortSeats >= numOfSeats;
        if(seatClass == SeatClass.LUXURY)
            return luxurySeats >= numOfSeats;
        return false;
    }
    @Override
    public double getSeatPrice(@Nullable SeatClass seatClass) {
        if(seatClass == SeatClass.COMFORT)
            return 30;
        if(seatClass == SeatClass.LUXURY)
            return 50;
        if(seatClass == SeatClass.ECONOMY)
            return 10;
        return 0;
    }
}
