package com.ibramir.busstation.users;

import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.trips.OnTripsChangedListener;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.trips.TripManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Driver extends User implements RetrieveListener<Trip>, OnTripsChangedListener {

    private List<Trip> assignedTrips;

    Driver(String uid) {
        this(uid, null);
    }
    Driver(String uid, String email) {
        super(uid, email);
        assignedTrips = new ArrayList<>();
        TripManager.getInstance().addTripsChangedListener(this);
    }
    Driver(String uid, String email, String name) {
        super(uid, email, name);
    }

    public List<Trip> getAssignedTrips() {
        return assignedTrips;
    }

    @Override
    public synchronized void onRetrieve(Trip obj) {
        assignedTrips.add(obj);
    }

    @Override
    public void onTripsChanged() {
        List<Trip> trips = TripManager.getInstance().getTrips();
        Set<Trip> assignedCopy = new HashSet<>(assignedTrips);
        for(Trip t: assignedCopy) {
            if(!trips.contains(t))
                assignedTrips.remove(t);
        }
    }
}
