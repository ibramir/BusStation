package com.ibramir.busstation.station;

import android.support.annotation.Nullable;

public class ReservationInfo {
    private String uid;
    private String tripId;
    private int numOfSeats;

    public ReservationInfo(String uid, String tripId, int numOfSeats) {
        this.uid = uid;
        this.tripId = tripId;
        this.numOfSeats = numOfSeats;
    }

    public String getUid() {
        return uid;
    }
    public String getTripId() {
        return tripId;
    }
    public int getNumOfSeats() {
        return numOfSeats;
    }

    public static ReservationInfo withInfo(String uid, String tripId) {
        return new ReservationInfo(uid, tripId, 0);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == this)
            return true;
        if(!(obj instanceof ReservationInfo))
            return false;
        ReservationInfo r = (ReservationInfo) obj;
        return this.uid.equals(r.uid) && this.tripId.equals(r.tripId);
    }
}
