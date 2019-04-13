package com.ibramir.busstation.station.vehicles;

import com.google.firebase.firestore.DocumentSnapshot;

import javax.annotation.Nullable;

public class Car extends Vehicle {
    private static final int MAX_SEATS = 4;

    Car() {
        super(MAX_SEATS);
    }

    @Override
    public void reserveSeats(int numOfSeats, SeatClass seatClass) {
        super.reserveSeats(numOfSeats);
    }

    @Override
    public double getSeatPrice(@Nullable SeatClass seatClass) {
        return 100;
    }

    @Override
    public boolean availableSeats(int numOfSeats, @Nullable SeatClass seatClass) {
        return getAvailableSeats() >= numOfSeats;
    }

    @Override
    void initFromDocument(DocumentSnapshot d) { }

    @Override
    public boolean hasSeatClass(SeatClass seatClass) {
        return seatClass == SeatClass.COMFORT;
    }
}
