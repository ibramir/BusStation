package com.ibramir.busstation.station.vehicles;

import com.ibramir.busstation.station.Trip;

public class Car extends Vehicle {
    public Car(Trip assignedTrip) {
        super(assignedTrip, 4);
    }
}
