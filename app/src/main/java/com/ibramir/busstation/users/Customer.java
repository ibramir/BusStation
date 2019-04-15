package com.ibramir.busstation.users;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.ibramir.busstation.RetrieveListener;
import com.ibramir.busstation.station.tickets.Ticket;
import com.ibramir.busstation.station.trips.Trip;
import com.ibramir.busstation.station.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class Customer extends User implements RetrieveListener<Ticket> {

    private List<Ticket> tickets;

    Customer(String uid) {
        this(uid, null);
    }
    Customer(String uid, String email) {
        super(uid, email);
        tickets = new ArrayList<>();
    }
    Customer(String uid, String email, String name) {
        super(uid, email, name);
    }
    public List<Ticket> getTickets() {
        return tickets;
    }

    void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public boolean reserveTicket(Trip trip1, Vehicle.SeatClass seatClass, @Nullable Trip trip2, @Nullable Vehicle.SeatClass seatClass2,
                                 int numOfSeats) {
        if(!trip1.getVehicle().availableSeats(numOfSeats, seatClass)
        || trip2 != null && !trip2.getVehicle().availableSeats(numOfSeats, seatClass)) {
            return false;
        }
        Ticket ticket = Ticket.reserveTicket(this, trip1, seatClass, trip2, seatClass2, numOfSeats);
        tickets.add(ticket);
        return true;
    }
    public void cancelReservation(String ticketId) {
        for(Ticket t: tickets) {
            if(t.equals(ticketId)) {
                cancelReservation(t);
                return;
            }
        }
    }
    public void cancelReservation(Ticket t) {
        t.revokeTicket();
        tickets.remove(t);
        UserManager.getInstance().save(this);
    }

    @Override
    public synchronized void onRetrieve(Ticket obj) {
        if(tickets.contains(obj))
            return;
        tickets.add(obj);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TicketListener listener = new TicketListener(obj);
        ListenerRegistration r = db.collection("trips").document(obj.getTicketId())
                .addSnapshotListener(listener);
        listener.registration = r;
    }

    private class TicketListener implements EventListener<DocumentSnapshot> {
        private Ticket ticket;
        private ListenerRegistration registration;
        private TicketListener(Ticket ticket) {
            this.ticket = ticket;
        }

        @Override
        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
            if(documentSnapshot == null || !documentSnapshot.exists()) {
                tickets.remove(ticket);
                ticket = null;
                registration.remove();
                registration = null;
            }
        }
    }
}
