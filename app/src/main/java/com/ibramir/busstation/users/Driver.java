package com.ibramir.busstation.users;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.trips.Trip;

import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nullable;


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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        assignedTrips.add(obj);
        TripListener listener = new TripListener(obj);
        ListenerRegistration r = db.collection("trips").document(obj.getId())
                .addSnapshotListener(listener);
        listener.registration = r;
    }

    private class TripListener implements EventListener<DocumentSnapshot> {
        private Trip trip;
        private ListenerRegistration registration;
        private TripListener(Trip trip) {
            this.trip = trip;
        }

        @Override
        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
            if(documentSnapshot == null) {
                assignedTrips.remove(trip);
                trip = null;
                registration.remove();
                registration = null;
            }
        }
    }
}
