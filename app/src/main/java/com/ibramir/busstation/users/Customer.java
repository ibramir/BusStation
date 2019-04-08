package com.ibramir.busstation.users;

import com.ibramir.busstation.station.ReservationInfo;
import com.ibramir.busstation.station.Trip;

import java.util.Collection;
import java.util.HashSet;

public class Customer extends User {

    private Collection<ReservationInfo> reservations;

    public Customer(String uid) {
        this(uid, null);
    }
    Customer(String uid, String email) {
        super(uid, email);
        reservations = new HashSet<>();
    }

    public boolean reserveSeats(Trip trip, int numOfSeats) {
        ReservationInfo reservation = new ReservationInfo(getUid(), trip.getId(), numOfSeats);
        if(trip.reserveSeats(reservation)) {
            reservations.add(reservation);
            //TODO firebase reserve
            return true;
        }
        return false;
    }
    public void cancelReservation(String tripId) {
        ReservationInfo reservation = ReservationInfo.withInfo(getUid(), tripId);
        if(!reservations.contains(reservation))
            return;
        //TODO firebase cancel
        reservations.remove(reservation);
    }

    @Override
    public boolean saveInfo() {
        return false;
    }
}
