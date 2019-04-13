package com.ibramir.busstation.station.vehicles;

import com.google.firebase.firestore.DocumentSnapshot;

import javax.annotation.Nullable;

public class Bus extends Vehicle {
    private static final int MAX_ECONOMY = 40;
    private static final int MAX_COMFOT = 15;
    private static final int MAX_LUXURY = 5;

    private int economySeats = MAX_ECONOMY;
    private int comfortSeats = MAX_COMFOT;
    private int luxurySeats = MAX_LUXURY;

    Bus() {
        super(60);
    }

    public int getEconomySeats() {
        return economySeats;
    }
    public int getComfortSeats() {
        return comfortSeats;
    }
    public int getLuxurySeats() {
        return luxurySeats;
    }

    @Override
    public void reserveSeats(int numOfSeats, SeatClass seatClass) {
        if(seatClass == SeatClass.ECONOMY)
            economySeats -= numOfSeats;
        else if(seatClass == SeatClass.COMFORT)
            comfortSeats -= numOfSeats;
        else if(seatClass == SeatClass.LUXURY)
            luxurySeats -= numOfSeats;
        super.reserveSeats(numOfSeats);
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
    void initFromDocument(DocumentSnapshot d) {
        economySeats = d.getLong("economySeats").intValue();
        comfortSeats = d.getLong("comfortSeats").intValue();
        luxurySeats = d.getLong("luxurySeats").intValue();
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

    @Override
    public boolean hasSeatClass(SeatClass seatClass) {
        return true;
    }
}
