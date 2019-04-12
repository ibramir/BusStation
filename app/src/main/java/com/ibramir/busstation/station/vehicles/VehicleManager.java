package com.ibramir.busstation.station.vehicles;

import android.os.AsyncTask;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.ibramir.busstation.FirestoreActions;
import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.trips.TripManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class VehicleManager implements FirestoreActions<Vehicle> {
    private static final VehicleManager ourInstance = new VehicleManager();
    public static VehicleManager getInstance() {
        return ourInstance;
    }
    private VehicleManager() {
    }

    private Map<String, RetrieveListener<Vehicle>> listeners = new HashMap<>();

    @Override
    public void delete(Vehicle obj) {
        FirebaseFirestore.getInstance().collection("vehicles").document(obj.getVehicleId()).delete();
    }

    @Override
    public void save(Vehicle obj) {
        new SaveVehicleTask().execute(obj);
    }
    private static class SaveVehicleTask extends AsyncTask<Vehicle,Void,Void> {
        @Override
        protected Void doInBackground(Vehicle... vehicles) {
            Vehicle v = vehicles[0];
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> data = new HashMap<>();
            data.put("availableSeats", v.getAvailableSeats());
            data.put("assignedTrip", db.collection("trips").document(v.getAssignedTrip().getId()));
            data.put("type", v.getClass().getSimpleName().toUpperCase());
            addTypeData(v, data);
            try {
                DocumentReference userRef = db.collection("vehicles").document(v.getVehicleId());
                Tasks.await(userRef.set(data, SetOptions.merge()), 15, TimeUnit.SECONDS);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }
        private void addTypeData(Vehicle v, Map<String,Object> data) {
            if(v instanceof Car)
                return;
            if(v instanceof MiniBus) {
                data.put("economySeats", ((MiniBus) v).getEconomySeats());
                data.put("comfortSeats", ((MiniBus) v).getComfortSeats());
            }
            else if(v instanceof Bus) {
                data.put("economySeats", ((Bus) v).getEconomySeats());
                data.put("comfortSeats", ((Bus) v).getComfortSeats());
                data.put("luxurySeats", ((Bus) v).getLuxurySeats());
            }
        }
    }

    @Override
    public void retrieve(String id, RetrieveListener<Vehicle> retrieveListener) {
        listeners.put(id, retrieveListener);
        new RetrieveTask().execute(id);
    }
    private static class RetrieveTask extends AsyncTask<String,Void,Vehicle> {
        @Override
        protected Vehicle doInBackground(String... strings) {
            try {
                DocumentSnapshot d = Tasks.await(FirebaseFirestore.getInstance()
                        .collection("vehicles").document(strings[0]).get(),
                        15, TimeUnit.SECONDS);
                if(!d.exists())
                    return null;
                Vehicle ret = Vehicle.fromDocument(d);
                TripManager.getInstance().retrieve(d.getString("assignedTrip"), ret);
                return ret;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Vehicle vehicle) {
            if(vehicle == null)
                return;
            Map<String, RetrieveListener<Vehicle>> listeners = VehicleManager.getInstance().listeners;
            RetrieveListener<Vehicle> listener = listeners.get(vehicle.getVehicleId());
            if(listener != null) {
                listener.onRetrieve(vehicle);
                listeners.remove(vehicle.getVehicleId());
            }
        }
    }
}
