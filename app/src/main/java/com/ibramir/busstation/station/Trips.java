package com.ibramir.busstation.station;

import android.os.AsyncTask;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class Trips {
    private Trips() {}
    //TODO firebase
    public static void reserve(String tripId, String uid, int numOfSeats) {

    }
    static void deleteTrip(Trip trip) {

    }

    static class SaveTripTask extends AsyncTask<Trip, Void, Void> {
        @Override
        protected Void doInBackground(Trip... trips) {
            Trip t = trips[0];
            Map<String, Object> data = new HashMap<>();
            data.put("source", t.getSource());
            data.put("destination", t.getDestination());
            data.put("price", t.getPrice());
            data.put("time", t.getTime());
            data.put("vehicle", t.getVehicle());
            data.put("driver", t.getDriver().getUid());
            data.put("reservations", t.getReservations());
            data.put("stops", t.getStops());
            try {
                Tasks.await(FirebaseFirestore.getInstance()
                                .collection("trips")
                                .document(t.getId()).set(data),
                        10, TimeUnit.SECONDS);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return null;
            } catch (TimeoutException e) {
                e.printStackTrace();
                return null;
            }
            t.initializeDocumentListener();
            return null;
        }
    }
}
