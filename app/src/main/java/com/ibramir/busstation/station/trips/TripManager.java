package com.ibramir.busstation.station.trips;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.ibramir.busstation.FirestoreActions;
import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.vehicles.VehicleManager;

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
    private Collection<OnTripsChangedListener> changeListeners = new HashSet<>();

    final private Map<String, RetrieveListener<Trip>> listeners = new HashMap<>();
    private List<Trip> trips;

    public List<Trip> getTrips() {
        if(trips == null) {
            fetchTrips();
        }
        return trips;
    }
    public Trip findTripById(String id) {
        int index = trips.indexOf(Trip.ofId(id));
        if(index == -1)
            return null;
        return getTrips().get(index);
    }
    public synchronized void fetchTrips() {
        if(trips == null) {
            trips = new ArrayList<>();
            new RetrieveAllTask().execute();
        }
    }
    public synchronized void fetchTrips(OnTripsChangedListener listener) {
        addTripsChangedListener(listener);
        if(trips == null) {
            trips = new ArrayList<>();
            new RetrieveAllTask().execute();
        }
        else
            listener.onTripsChanged();
    }

    @Override
    public synchronized void delete(Trip obj) {
        deleteTickets(obj.getId());
        FirebaseFirestore.getInstance().collection("trips").document(obj.getId()).delete();
    }
    private void deleteTickets(String tripId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();
        CollectionReference ticketsRef = db.collection("tickets");
        for(String ticketId: findTripById(tripId).getTicketIds()) {
            batch.delete(ticketsRef.document(ticketId));
        }
        batch.commit();
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
            retrieveListener.onRetrieve(findTripById(tripId));
        }
        else {
            listeners.put(tripId, retrieveListener);
            new RetrieveTripTask().execute(tripId);
        }
    }


    private static class SaveTripTask extends AsyncTask<Trip, Void, Void> {
        @Override
        protected Void doInBackground(Trip... trips) {
            Trip t = trips[0];
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> data = new HashMap<>();
            data.put("source", t.getSource());
            data.put("destination", t.getDestination());
            data.put("price", t.getPrice());
            data.put("time", t.getTime());
            DocumentReference vehicleRef = db.collection("vehicles").document(t.getVehicle().getVehicleId());
            data.put("vehicle", vehicleRef);
            DocumentReference driverRef = db.collection("users").document(t.getDriverId());
            data.put("driver", driverRef);
            List<DocumentReference> ticketRefs = new ArrayList<>();
            CollectionReference ticketsRef = db.collection("tickets");
            for(String ticketId: t.getTicketIds()) {
                ticketRefs.add(ticketsRef.document(ticketId));
            }
            try {
                DocumentReference tripReference = db.collection("trips").document(t.getId());
                Tasks.await(tripReference.set(data, SetOptions.merge()), 10, TimeUnit.SECONDS);
                if(ticketRefs.size() > 0)
                    Tasks.await(tripReference.update("tickets",
                        FieldValue.arrayUnion(ticketRefs.toArray())),
                        10,TimeUnit.SECONDS);
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

    private static class RetrieveTripTask extends AsyncTask<String, Void, Trip> {
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

        @Override
        protected void onPostExecute(Collection<Trip> result) {
            final List<Trip> trips = getInstance().trips;
            trips.addAll(result);
            FirebaseFirestore.getInstance().collection("trips")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if(queryDocumentSnapshots == null)
                                return;
                            synchronized (trips) {
                                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                        int i;
                                        if((i=trips.indexOf(Trip.ofId(documentChange.getDocument().getId()))) != -1)
                                            trips.get(i).updateData(documentChange.getDocument().getData());
                                        else
                                            trips.add(fromSnapshot(documentChange.getDocument()));
                                    }
                                    else {
                                        String tripId = documentChange.getDocument().getId();
                                        if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                                            getInstance().deleteTickets(tripId);
                                            trips.remove(Trip.ofId(tripId));
                                        }
                                        else if (documentChange.getType() == DocumentChange.Type.MODIFIED)
                                            trips.get(trips.indexOf(Trip.ofId(tripId)))
                                                    .updateData(documentChange.getDocument().getData());
                                    }
                                }
                            }
                            TripManager.getInstance().notifyListeners();
                        }
                    });
            TripManager.getInstance().notifyListeners();
        }
    }

    public synchronized void addTripsChangedListener(OnTripsChangedListener listener) {
        if(listener != null)
            changeListeners.add(listener);
    }
    public synchronized void removeTripsChangedListener(OnTripsChangedListener listener) {
        changeListeners.remove(listener);
    }
    synchronized void notifyListeners() {
        for(OnTripsChangedListener listener: changeListeners)
            listener.onTripsChanged();
    }

    private synchronized static Trip fromSnapshot(DocumentSnapshot d) {
        Trip.Builder b = new Trip.Builder();
        b.ofId(d.getId())
                .from(d.getString("source"))
                .to(d.getString("destination"))
                .ofPrice(d.getDouble("price"))
                .atTime(d.getTimestamp("time").toDate())
                .withDriver((d.getDocumentReference("driver")).getId());
        Set<String> ticketIds = new HashSet<>();
        List<DocumentReference> tickets = (List<DocumentReference>) d.get("tickets");
        if(tickets != null)
            for(DocumentReference ticketRef: tickets) {
                ticketIds.add(ticketRef.getId());
            }
        b.withTickets(ticketIds);
        Trip ret = b.build();
        VehicleManager.getInstance().retrieve((d.getDocumentReference("vehicle")).getId(),ret);
        return ret;
    }
}
