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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.ibramir.busstation.FirestoreActions;
import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.tickets.Ticket;
import com.ibramir.busstation.station.tickets.TicketManager;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.trips.TripManager;

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

public class UserManager implements FirestoreActions<User> {
    public interface OnRetrieveDriversListener {
        void onRetrieveDrivers(Collection<Driver> drivers);
    }

    private static final UserManager ourInstance = new UserManager();

    public static UserManager getInstance() {
        return ourInstance;
    }
    private UserManager() {
    }

    private Map<String,RetrieveListener<User>> retrieveListeners = new HashMap<>();

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

        private List<DocumentReference> getCustomerData(Customer customer) {
            List<Ticket> cTickets = customer.getTickets();
            List<DocumentReference> ticketRefs = new ArrayList<>(cTickets.size());
            CollectionReference ref = FirebaseFirestore.getInstance().collection("tickets");
            for(Ticket t: cTickets)
                ticketRefs.add(ref.document(t.getTicketId()));
            return ticketRefs;
        }

        private List<DocumentReference> getDriverData(Driver driver) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            List<DocumentReference> tripRefs = new ArrayList<>();
            CollectionReference ref = db.collection("trips");
            for(Trip t: driver.getAssignedTrips())
                tripRefs.add(ref.document(t.getId()));
            return tripRefs;
        }
    }

    @Override
    public void retrieve(String id, RetrieveListener<User> retrieveListener) {
        retrieveListeners.put(id, retrieveListener);
        new RetrieveTask().execute(id);
    }
    private static class RetrieveTask extends AsyncTask<String,Void,User> {
        @Override
        protected User doInBackground(String... strings) {
            try {
                DocumentSnapshot d = Tasks.await(
                        FirebaseFirestore.getInstance().collection("users").document(strings[0]).get(),
                        10, TimeUnit.SECONDS);
                if(!d.exists())
                    return null;
                User ret = null;
                switch (d.getString("type")) {
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
        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            RetrieveListener<User> userRetrieveListener = getInstance().retrieveListeners.get(user.getUid());
            if(userRetrieveListener != null)
                userRetrieveListener.onRetrieve(user);
        }
    }
    private static Customer getCustomer(DocumentSnapshot d) {
        Customer ret = new Customer(d.getString("uid"), d.getString("email"));
        ret.setName(d.getString("name"));
        for(DocumentReference tRef: (ArrayList<DocumentReference>)d.get("tickets")) {
            TicketManager.getInstance().retrieve(tRef.getId(),ret);
        }
        return ret;
    }
    private static Manager getManager(DocumentSnapshot d) {
        Manager ret = new Manager(d.getString("uid"), d.getString("email"));
        ret.setName(d.getString("name"));
        return ret;
    }
    private static Driver getDriver(DocumentSnapshot d) {
        Driver ret = new Driver(d.getString("uid"), d.getString("email"));
        ret.setName(d.getString("name"));
        for(DocumentReference tRef: (ArrayList<DocumentReference>)d.get("trips"))
            TripManager.getInstance().retrieve(tRef.getId(), ret);
        return ret;
    }
    private static Driver getDriver(DocumentSnapshot d, boolean getAssignedTrips) {
        if(getAssignedTrips)
            return  getDriver(d);
        Driver ret = new Driver(d.getString("uid"), d.getString("email"));
        ret.setName(d.getString("name"));
        return ret;
    }

    public void retrieveDrivers(OnRetrieveDriversListener listener) {
        new RetrieveDriversTask(listener).execute();
    }

    private static class RetrieveDriversTask extends AsyncTask<Void, Void, Collection<Driver>> {
        private OnRetrieveDriversListener listener;
        private RetrieveDriversTask(OnRetrieveDriversListener listener) {
            this.listener = listener;
        }

        @Override
        protected Collection<Driver> doInBackground(Void... voids) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Query query = db.collection("users").whereEqualTo("type", Driver.class.getSimpleName());
            try {
                Set<Driver> drivers = new HashSet<>();
                QuerySnapshot driversSnapshot = Tasks.await(query.get(), 15, TimeUnit.SECONDS);
                for(DocumentSnapshot d: driversSnapshot) {
                    drivers.add(getDriver(d, false));
                }
                return drivers;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Collection<Driver> drivers) {
            listener.onRetrieveDrivers(drivers);
        }
    }
}
