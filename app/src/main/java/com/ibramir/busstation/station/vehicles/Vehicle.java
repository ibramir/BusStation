package com.ibramir.busstation.station.vehicles;

import com.ibramir.busstation.station.Trip;

public abstract class Vehicle {
    private int maxSeats;
    private Trip assignedTrip;

    Vehicle(Trip assignedTrip, int maxSeats) {
        this.assignedTrip = assignedTrip;
        this.maxSeats = maxSeats;
    }

    public int getMaxSeats() {
        return maxSeats;
    }
    public Trip getAssignedTrip() {
        return assignedTrip;
    }

    public int availableSeats() {
        return maxSeats - assignedTrip.getReservations().size();
    }

}
