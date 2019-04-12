package com.ibramir.busstation.users;

import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.Trip;

import java.util.Collection;
import java.util.HashSet;

public class Driver extends User implements RetrieveListener<Trip> {

    private Collection<Trip> assignedTrips;

    public Driver(String uid) {
        this(uid, null);
    }
    public Driver(String uid, String email) {
        super(uid, email);
        assignedTrips = new HashSet<>();
    }

    public Collection<Trip> getAssignedTrips() {
        return assignedTrips;
    }

    @Override
    public synchronized void onRetrieve(Trip obj) {
        assignedTrips.add(obj);
    }
}
