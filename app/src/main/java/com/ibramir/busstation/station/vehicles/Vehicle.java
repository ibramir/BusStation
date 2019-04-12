package com.ibramir.busstation.station.vehicles;

import com.google.firebase.firestore.DocumentSnapshot;
import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.trips.Trip;

import java.util.UUID;

import javax.annotation.Nullable;

public abstract class Vehicle implements RetrieveListener<Trip> {

    @Override
    public void onRetrieve(Trip obj) {
        assignedTrip = obj;
    }

    public enum SeatClass {
        ECONOMY,
        COMFORT,
        LUXURY
    }

    public enum Type {
        CAR,
        MINIBUS,
        BUS
    }

    private String vehicleId;
    private int availableSeats;
    private Trip assignedTrip;

    Vehicle(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    static Vehicle fromDocument(DocumentSnapshot d) {
        Vehicle ret = ofType(Type.valueOf((String)d.get("type")));
        if(ret != null) {
            ret.vehicleId = d.getId();
            ret.availableSeats = (int) d.get("availableSeats");
            ret.initFromDocument(d);
        }
        return ret;
    }

    public static Vehicle createVehicle(Type type) {
        Vehicle ret = ofType(type);
        if(ret != null)
            ret.vehicleId = UUID.randomUUID().toString();
        VehicleManager.getInstance().save(ret);
        return ret;
    }
    private static Vehicle ofType(Type type) {
        Vehicle ret = null;
        switch (type) {
            case CAR: ret = new Car(); break;
            case MINIBUS: ret = new MiniBus(); break;
            case BUS: ret = new Bus(); break;
            default: return null;
        }
        return ret;
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
        VehicleManager.getInstance().save(this);
    }
    public abstract void reserveSeats(int numOfSeats, SeatClass seatClass);

    public abstract double getSeatPrice(@Nullable SeatClass seatClass);
    public abstract boolean availableSeats(int numOfSeats, @Nullable SeatClass seatClass);
    abstract void initFromDocument(DocumentSnapshot d);
}
