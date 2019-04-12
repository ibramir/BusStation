package com.ibramir.busstation.station.trips;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.ibramir.busstation.FirestoreActions;
import com.ibramir.busstation.RetrieveListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TripManager implements FirestoreActions<Trip> {

    private static final TripManager ourInstance = new TripManager();
    public static TripManager getInstance() {
        return ourInstance;
    }
    private TripManager() { }

    final private Map<String, RetrieveListener<Trip>> listeners = new HashMap<>();
    private List<Trip> trips;

    public List<Trip> getTrips() {
        if(trips == null) {
            fetchTrips();
        }
        return trips;
    }
    public Trip findTripById(String id) {
        return getTrips().get(trips.indexOf(Trip.ofId(id)));
    }
    public void fetchTrips() {
        if(trips != null)
            return;
        trips = new ArrayList<>();
        try {
            Collection<Trip> tripCollection = new RetrieveAllTask().execute().get();
            trips.addAll(tripCollection);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        FirebaseFirestore.getInstance().collection("trips")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(queryDocumentSnapshots == null)
                            return;
                        synchronized (trips) {
                            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                Trip trip = fromSnapshot(documentChange.getDocument());
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                    int i;
                                    if((i=trips.indexOf(trip)) != -1)
                                        trips.set(i, trip);
                                    else
                                        trips.add(trip);
                                }
                                else if (documentChange.getType() == DocumentChange.Type.REMOVED)
                                    trips.remove(Trip.ofId(documentChange.getDocument().getId()));
                                else if (documentChange.getType() == DocumentChange.Type.MODIFIED)
                                    trips.set(trips.indexOf(Trip.ofId(documentChange.getDocument().getId())),
                                            trip);
                            }
                        }
                    }
                });
    }

    @Override
    public synchronized void delete(Trip obj) {
        if(obj.getListenerRegistration() != null)
            obj.getListenerRegistration().remove();
        FirebaseFirestore.getInstance().collection("trips").document(obj.getId()).delete();
    }
    @Override
    public synchronized void save(Trip obj) {
        new SaveTripTask().execute(obj);
    }
    @Override
    public synchronized void retrieve(String tripId, RetrieveListener<Trip> retrieveListener) {
        if(tripId == null)
            return;
        if(trips != null) {
            for(Trip t: trips) {
                if(t.getId().equals(tripId)) {
                    retrieveListener.onRetrieve(t);
                    return;
                }
            }
        }
        else {
            listeners.put(tripId, retrieveListener);
            new RetrieveTask().execute(tripId);
        }
    }


    private static class SaveTripTask extends AsyncTask<Trip, Void, Void> {
        @Override
        protected Void doInBackground(Trip... trips) {
            Trip t = trips[0];
            Map<String, Object> data = new HashMap<>();
            data.put("source", t.getSource());
            data.put("destination", t.getDestination());
            data.put("price", t.getPrice());
            data.put("time", t.getTime());
            data.put("vehicle", t.getVehicle().getVehicleId());
            data.put("driver", t.getDriverId());
            try {
                DocumentReference tripReference = FirebaseFirestore.getInstance()
                        .collection("trips").document(t.getId());
                Tasks.await(tripReference.set(data, SetOptions.merge()), 10, TimeUnit.SECONDS);
                Tasks.await(tripReference.update("tickets",
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

    private static class RetrieveTask extends AsyncTask<String, Void, Trip> {
        @Override
        protected Trip doInBackground(String... strings) {
            try {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentSnapshot d =
                        Tasks.await(db.collection("trips").document(strings[0]).get(),
                        10,TimeUnit.SECONDS);
                return fromSnapshot(d);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Trip trip) {
            super.onPostExecute(trip);
            if(trip == null)
                return;
            synchronized (TripManager.getInstance().listeners) {
                Map<String, RetrieveListener<Trip>> listeners = TripManager.getInstance().listeners;
                RetrieveListener<Trip> listener = listeners.get(trip.getId());
                if(listener != null) {
                    listener.onRetrieve(trip);
                    listeners.remove(trip.getId());
                }
            }
        }
    }

    private static class RetrieveAllTask extends AsyncTask<Void, Void, Collection<Trip>> {
        @Override
        protected Collection<Trip> doInBackground(Void... voids) {
            try {
                List<Trip> result = new ArrayList<>();
                QuerySnapshot query = Tasks.await(FirebaseFirestore.getInstance().collection("trips").get(),
                        20, TimeUnit.SECONDS);
                for(DocumentSnapshot d: query.getDocuments())
                    result.add(fromSnapshot(d));
                return result;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }

        /*@Override
        protected void onPostExecute(Collection<Trip> trips) {
            super.onPostExecute(trips);
            if(trips != null)
                TripManager.getInstance().trips.addAll(trips);
        }*/
    }

    private synchronized static Trip fromSnapshot(DocumentSnapshot d) {
        Trip.Builder b = new Trip.Builder();
        b.ofId(d.getId())
                .from((String)d.get("source"))
                .to((String)d.get("destination"))
                .ofPrice((long)d.get("price"))
                .atTime(((Timestamp)d.get("time")).toDate())
                .withDriver(((DocumentReference)d.get("driver")).getId());
        Set<String> ticketIds = new HashSet<>();
        for(DocumentReference ticketRef: (ArrayList<DocumentReference>)d.get("tickets"))
            ticketIds.add(ticketRef.getId());
        b.withTickets(ticketIds);
        //TODO vehicle
        return b.build();
    }
}
