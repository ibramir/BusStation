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
            data.put("trip1", db.collection("trips").document(t.getTrip1().getId()));
            data.put("trip2", db.collection("trips").document(t.getTrip2().getId()));
            data.put("numOfSeats", t.getNumOfSeats());
            data.put("price", t.getPrice());
            data.put("seatClass", t.getSeatClass().toString());
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
                double price;
                Object o = d.get("price");
                if(o instanceof Long)
                    price = ((Long) o).doubleValue();
                else
                    price = (double) o;
                Ticket ret = new Ticket(d.getId(),(String)d.get("uid"),
                        (int)d.get("numOfSeats"), price,
                        Vehicle.SeatClass.valueOf((String)d.get("seatClass")));
                TripManager tripManager = TripManager.getInstance();
                tripManager.retrieve((String)d.get("trip1"), ret);
                tripManager.retrieve((String)d.get("trip2"), ret);
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