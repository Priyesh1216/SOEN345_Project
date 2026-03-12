package com.example.soen345_project.data;

import com.example.soen345_project.domain.models.Event;
import com.example.soen345_project.domain.models.Reservation;
import com.example.soen345_project.domain.models.User;
import com.example.soen345_project.domain.services.EventService;
import com.example.soen345_project.domain.services.ReservationService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FirebaseRepository {

    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    // --- User ---

    public void getUser(String userId, GetUserCallback callback) {
        db.child("users").child(userId).get()
            .addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    user.setId(snapshot.getKey());
                    callback.onSuccess(user);
                } else {
                    callback.onFailure(new Exception("User not found"));
                }
            })
            .addOnFailureListener(e -> callback.onFailure(e));
    }

    public void saveUser(User user, SimpleCallback callback) {
        String key = user.getId() != null ? user.getId() : db.child("users").push().getKey();
        user.setId(key);

        db.child("users").child(key).setValue(user)
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(e -> callback.onFailure(e));
    }


    // --- Events ---
    public void getEvent(String eventId, GetEventCallback callback) {
        db.child("events").child(eventId).get()
            .addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    Event event = snapshot.getValue(Event.class);
                    event.setId(snapshot.getKey());
                    callback.onSuccess(event);
                } else {
                    callback.onFailure(new Exception("Event not found"));
                }
            })
            .addOnFailureListener(e -> callback.onFailure(e));
    }

    public void saveEvent(Event event, EventService.EventCallback callback) {
        String key = event.getId() != null ? event.getId() : db.child("events").push().getKey();
        event.setId(key);

        db.child("events").child(key).setValue(event)
                .addOnSuccessListener(aVoid -> callback.onSuccess(event))
                .addOnFailureListener(e -> callback.onFailure(e));
    }

    public void getFilteredEvents(Map<String, String> filters, EventService.EventListCallback callback) {
        db.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Event> events = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Event event = child.getValue(Event.class);
                    if (event == null) continue;
                    event.setId(child.getKey());

                    // skip cancelled events
                    if (!event.isActive()) continue;

                    if (filters != null) {
                        // filter by category
                        if (filters.containsKey("category")) {
                            String filterCategory = filters.get("category").toLowerCase();
                            if (!event.getCategory().toLowerCase().contains(filterCategory)) continue;
                        }

                        // filter by location
                        if (filters.containsKey("location")) {
                            String filterLocation = filters.get("location").toLowerCase();
                            if (!event.getLocation().toLowerCase().contains(filterLocation)) continue;
                        }

                        // filter by date range - expects "dateFrom" and/or "dateTo" as "yyyy-MM-dd"
                        if (filters.containsKey("dateFrom")) {
                            try {
                                Date dateFrom = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        .parse(filters.get("dateFrom"));
                                if (event.getDate().before(dateFrom)) continue;
                            } catch (ParseException e) {
                                callback.onFailure(new Exception("Invalid dateFrom format, use yyyy-MM-dd"));
                                return;
                            }
                        }

                        if (filters.containsKey("dateTo")) {
                            try {
                                Date dateTo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        .parse(filters.get("dateTo"));
                                if (event.getDate().after(dateTo)) continue;
                            } catch (ParseException e) {
                                callback.onFailure(new Exception("Invalid dateTo format, use yyyy-MM-dd"));
                                return;
                            }
                        }
                    }

                    events.add(event);
                }

                callback.onSuccess(events);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    // --- Reservations ---
    public void getReservation(String reservationId, GetReservationCallback callback) {
        db.child("reservations").child(reservationId).get()
            .addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    Reservation reservation = snapshot.getValue(Reservation.class);
                    reservation.setId(snapshot.getKey());
                    callback.onSuccess(reservation);
                } else {
                    callback.onFailure(new Exception("Reservation not found"));
                }
            })
            .addOnFailureListener(e -> callback.onFailure(e));
    }

    public void getReservationsByUser(String userId, ReservationService.ReservationListCallback callback) {
        db.child("reservations").orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Reservation> reservations = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Reservation reservation = child.getValue(Reservation.class);
                        if (reservation != null) {
                            reservation.setId(child.getKey());
                            reservations.add(reservation);
                        }
                    }
                    callback.onSuccess(reservations);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    callback.onFailure(error.toException());
                }
            });
    }

    public void createReservation(Reservation reservation, CreateReservationCallback callback) {
        String key = db.child("reservations").push().getKey();
        reservation.setId(key);

        db.child("reservations").child(key).setValue(reservation)
            .addOnSuccessListener(aVoid -> callback.onSuccess(key))
            .addOnFailureListener(e -> callback.onFailure(e));
    }

    public void transaction(String eventId, int quantity, TransactionCallback callback) {
        db.child("events").child(eventId).child("openSeats")
            .runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData currentData) {
                    Integer currentSeats = currentData.getValue(Integer.class);

                    if (currentSeats == null) {
                        return Transaction.abort();
                    }

                    int newSeats = currentSeats + quantity; // quantity is negative when cancelling

                    if (newSeats < 0) {
                        return Transaction.abort(); // not enough seats
                    }

                    currentData.setValue(newSeats);
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                    callback.onResult(committed);
                }
            });
    }

    // --- Callbacks ---
    public interface GetUserCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }
    public interface GetEventCallback {
        void onSuccess(Event event);
        void onFailure(Exception e);
    }
    public interface GetReservationCallback {
        void onSuccess(Reservation reservation);
        void onFailure(Exception e);
    }
    public interface CreateReservationCallback {
        void onSuccess(String reservationId);
        void onFailure(Exception e);
    }
    public interface TransactionCallback {
        void onResult(boolean success);
    }
    public interface SimpleCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}