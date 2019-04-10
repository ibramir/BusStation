package com.ibramir.busstation.station.vehicles;

import com.ibramir.busstation.station.Trip;

import javax.annotation.Nullable;

public abstract class Vehicle {

    public enum SeatClass {
        ECONOMY,
        COMFORT,
        LUXURY
    }

    private String vehicleId;
    private int availableSeats;
    private Trip assignedTrip;

    Vehicle(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getVehicleId() {
        return vehicleId;
    }
    public int getAvailableSeats() {
        return availableSeats;
    }
    public Trip getAssignedTrip() {
        return assignedTrip;
    }

    public void reserveSeats(int numOfSeats) {
        availableSeats -= numOfSeats;
    }
    public void reserveSeats(int numOfSeats, SeatClass seatClass) {
        availableSeats -= numOfSeats;
    }

    public abstract double getSeatPrice(@Nullable SeatClass seatClass);
    public abstract boolean availableSeats(int numOfSeats, @Nullable SeatClass seatClass);

}
