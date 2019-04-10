package com.ibramir.busstation.users;

import java.util.Collection;
import java.util.HashSet;

public class Driver extends User {

    private Collection<String> assignedTrips;

    public Driver(String uid) {
        this(uid, null);
    }
    public Driver(String uid, String email) {
        super(uid, email);
        assignedTrips = new HashSet<>();
    }

    @Override
    public boolean saveInfo() {
        return false;
    }
}
