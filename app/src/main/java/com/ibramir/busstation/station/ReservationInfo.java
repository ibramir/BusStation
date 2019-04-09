package com.ibramir.busstation.station;

import android.support.annotation.Nullable;

public class ReservationInfo {
    private String uid;
    private String ticketId;
    private int numOfSeats;

    public ReservationInfo(String uid, String ticketId, int numOfSeats) {
        this.uid = uid;
        this.ticketId = ticketId;
        this.numOfSeats = numOfSeats;
    }

    public String getUid() {
        return uid;
    }
    public String getTicketId() {
        return ticketId;
    }
    public int getNumOfSeats() {
        return numOfSeats;
    }

    public static ReservationInfo ofUser(String uid) {
        return new ReservationInfo(uid, null, 0);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == this)
            return true;
        if(!(obj instanceof ReservationInfo))
            return false;
        ReservationInfo r = (ReservationInfo) obj;
        return this.uid.equals(r.uid);
    }
}
