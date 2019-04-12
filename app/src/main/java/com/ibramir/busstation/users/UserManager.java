package com.ibramir.busstation.users;

import android.os.AsyncTask;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.ibramir.busstation.FirestoreActions;
import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.Ticket;
import com.ibramir.busstation.station.Trip;
import com.ibramir.busstation.station.TripManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class UserManager implements FirestoreActions<User> {
    private static final UserManager ourInstance = new UserManager();

    static UserManager getInstance() {
        return ourInstance;
    }
    private UserManager() {
    }

    private Map<String, RetrieveListener<User>> listeners = new HashMap<>();

    @Override
    public void delete(User obj) {
        User.logout();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null && user.getUid().equals(obj.getUid()))
            user.delete();
        FirebaseFirestore.getInstance().collection("users").document(obj.getUid()).delete();
    }
    @Override
    public void save(User obj) {
        new SaveUserTask().execute(obj);
    }
    private static class SaveUserTask extends AsyncTask<User,Void,Void> {
        @Override
        protected Void doInBackground(User... users) {
            User u = users[0];
            Map<String, Object> data = new HashMap<>();
            data.put("uid", u.getUid());
            data.put("email", u.getEmail());
            data.put("name", u.getName());
            data.put("type", u.getClass().getSimpleName());
            try {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userRef = db.collection("users").document(u.getUid());
                Tasks.await(userRef.set(data, SetOptions.merge()), 15, TimeUnit.SECONDS);
                List<DocumentReference> typeSpecific;
                if(u instanceof Customer) {
                    typeSpecific = getCustomerData((Customer) u);
                    Tasks.await(userRef.update("tickets", FieldValue.arrayUnion(typeSpecific.toArray())),
                            15, TimeUnit.SECONDS);
                }
                else if(u instanceof Driver) {
                    typeSpecific = getDriverData((Driver) u);
                    Tasks.await(userRef.update("trips", FieldValue.arrayUnion(typeSpecific.toArray())),
                            15, TimeUnit.SECONDS);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }

        private List<DocumentReference> getCustomerData(Customer c) {
            Collection<Ticket> cTickets = c.getTickets();
            List<DocumentReference> ticketRefs = new ArrayList<>(cTickets.size());
            CollectionReference ref = FirebaseFirestore.getInstance().collection("tickets");
            for(Ticket t: cTickets)
                ticketRefs.add(ref.document(t.getTicketId()));
            return ticketRefs;
        }

        private List<DocumentReference> getDriverData(Driver u) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            List<DocumentReference> tripRefs = new ArrayList<>();
            CollectionReference ref = db.collection("trips");
            for(Trip t: u.getAssignedTrips())
                tripRefs.add(ref.document(t.getId()));
            return tripRefs;
        }
    }

    @Override
    public void retrieve(String id, RetrieveListener<User> retrieveListener) {
        listeners.put(id, retrieveListener);
        new RetrieveTask().execute(id);
    }
    private static class RetrieveTask extends AsyncTask<String,Void,User> {
        @Override
        protected User doInBackground(String... strings) {
            try {
                DocumentSnapshot d = Tasks.await(
                        FirebaseFirestore.getInstance().collection("users").document(strings[0]).get(),
                        10, TimeUnit.SECONDS);
                User ret = null;
                switch ((String)d.get("type")) {
                    case "Customer": ret = getCustomer(d); break;
                    case "Manager": ret = getManager(d); break;
                    case "Driver": ret = getDriver(d); break;
                }
                return ret;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }
        private Customer getCustomer(DocumentSnapshot d) {
            Customer ret = new Customer((String)d.get("uid"), (String)d.get("email"));
            ret.setName((String)d.get("name"));
            List<Ticket> tickets = new ArrayList<>();
            for(DocumentReference tRef: (ArrayList<DocumentReference>)d.get("tickets")) {
                //TODO retrieve tickets
            }
            ret.setTickets(tickets);
            return ret;
        }
        private Manager getManager(DocumentSnapshot d) {
            Manager ret = new Manager((String)d.get("uid"), (String)d.get("email"));
            ret.setName((String)d.get("name"));
            return ret;
        }
        private Driver getDriver(DocumentSnapshot d) {
            Driver ret = new Driver((String)d.get("uid"), (String)d.get("email"));
            ret.setName((String)d.get("name"));
            for(DocumentReference tRef: (ArrayList<DocumentReference>)d.get("trips"))
                TripManager.getInstance().retrieve(tRef.getId(), ret);
            return ret;
        }
        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            Map<String, RetrieveListener<User>> listeners = UserManager.getInstance().listeners;
            RetrieveListener<User> listener = listeners.get(user.getUid());
            if(listener == null)
                return;
            listener.onRetrieve(user);
            listeners.remove(user.getUid());
        }
    }
}