package com.ibramir.busstation.station.tickets;

import android.os.AsyncTask;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ibramir.busstation.FirestoreActions;
import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.trips.TripManager;
import com.ibramir.busstation.station.vehicles.Vehicle;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TicketManager implements FirestoreActions<Ticket> {
    private static final TicketManager ourInstance = new TicketManager();
    public static TicketManager getInstance() {
        return ourInstance;
    }
    private TicketManager() {
    }

    private Map<String, RetrieveListener<Ticket>> listeners = new HashMap<>();

    @Override
    public void delete(Ticket obj) {
        FirebaseFirestore.getInstance().collection("tickets").document(obj.getTicketId()).delete();
    }

    @Override
    public void save(Ticket obj) {
        new SaveTicketTask().execute(obj);
    }
    private static class SaveTicketTask extends AsyncTask<Ticket, Void, Void> {
        @Override
        protected Void doInBackground(Ticket... tickets) {
            Ticket t = tickets[0];
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> data = new HashMap<>();
            data.put("uid", t.getUid());
            data.put("trip1", db.collection("trips").document(t.getTrip().getId()));
            data.put("seatClass", t.getSeatClass().toString());
            if(t.getTrip2() != null) { ;
                data.put("trip2", db.collection("trips").document(t.getTrip2().getId()));
                data.put("seatClass2", t.getSeatClass2().toString());
            }
            data.put("numOfSeats", t.getNumOfSeats());
            data.put("price", t.getPrice());
            try {
                Tasks.await(db.collection("tickets").document(t.getTicketId()).set(data),
                        15, TimeUnit.SECONDS);
            } catch(InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch(TimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void retrieve(String id, RetrieveListener<Ticket> retrieveListener) {
        listeners.put(id, retrieveListener);
        new RetrieveTicketTask().execute(id);
    }
    private static class RetrieveTicketTask extends AsyncTask<String, Void, Ticket> {
        @Override
        protected Ticket doInBackground(String... strings) {
            try {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentSnapshot d = Tasks.await(db.collection("tickets").document(strings[0]).get(),
                        15, TimeUnit.SECONDS);
                Ticket ret = new Ticket(d.getId(),d.getString("uid"),
                        d.getLong("numOfSeats").intValue(), d.getDouble("price"),
                        Vehicle.SeatClass.valueOf(d.getString("seatClass")));
                TripManager tripManager = TripManager.getInstance();
                String trip1Id = d.getString("trip1");
                ret.setTrip1Id(trip1Id);
                tripManager.retrieve(trip1Id, ret);
                String trip2Id = d.getString("trip2");
                ret.setTrip2Id(trip2Id);
                tripManager.retrieve(trip2Id, ret);
                return ret;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Ticket ticket) {
            super.onPostExecute(ticket);
            Map<String, RetrieveListener<Ticket>> listeners = TicketManager.getInstance().listeners;
            RetrieveListener<Ticket> listener = listeners.get(ticket.getTicketId());
            if(listener != null) {
                listener.onRetrieve(ticket);
                listeners.remove(ticket.getTicketId());
            }
        }
    }
}
