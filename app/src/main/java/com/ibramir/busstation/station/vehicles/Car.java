package com.ibramir.busstation.station.vehicles;

import javax.annotation.Nullable;

public class Car extends Vehicle {
    private static final int MAX_SEATS = 4;

    public Car() {
        super(MAX_SEATS);
    }

    @Override
    public double getSeatPrice(@Nullable SeatClass seatClass) {
        return 100;
    }

    @Override
    public boolean availableSeats(int numOfSeats, @Nullable SeatClass seatClass) {
        return getAvailableSeats() >= numOfSeats;
    }
}
