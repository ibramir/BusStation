package com.ibramir.busstation.station;

import android.os.AsyncTask;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.ibramir.busstation.FirestoreActions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TripManager implements FirestoreActions<Trip> {

    private static final TripManager ourInstance = new TripManager();
    public static TripManager getInstance() {
        return ourInstance;
    }

    private TripManager() { }

    @Override
    public synchronized void delete(Trip obj) {
        obj.getListenerRegistration().remove();
        FirebaseFirestore.getInstance().collection("trips").document(obj.getId()).delete();
    }
    @Override
    public synchronized void save(Trip obj) {
        new SaveTripTask().execute(obj);
    }

    class SaveTripTask extends AsyncTask<Trip, Void, Void> {
        @Override
        protected Void doInBackground(Trip... trips) {
            Trip t = trips[0];
            Map<String, Object> data = new HashMap<>();
            data.put("source", t.getSource());
            data.put("destination", t.getDestination());
            data.put("price", t.getPrice());
            data.put("time", t.getTime());
            data.put("vehicle", t.getVehicle().getVehicleId());
            data.put("driverId", t.getDriverId());
            try {
                DocumentReference tripReference = FirebaseFirestore.getInstance()
                        .collection("trips").document(t.getId());
                Tasks.await(tripReference.set(data, SetOptions.merge()), 10, TimeUnit.SECONDS);
                Tasks.await(tripReference.update("ticketIds",
                        FieldValue.arrayUnion(t.getTicketIds().toArray())),
                        10,TimeUnit.SECONDS);
                t.initializeDataListener();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return null;
            } catch (TimeoutException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }
    }
}
