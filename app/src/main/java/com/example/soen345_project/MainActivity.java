package com.example.soen345_project;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Realtime Database
        db = FirebaseDatabase.getInstance().getReference();

        // Reference to "users" node
        DatabaseReference usersRef = db.child("users");

        // User 1 test (John Smith)
        Map<String, Object> user1 = new HashMap<>();
        user1.put("first", "John");
        user1.put("last", "Smith");
        user1.put("born", 1999);

        String userId1 = usersRef.push().getKey();

        usersRef.child(userId1).setValue(user1)
                .addOnSuccessListener(aVoid ->
                        Log.d(TAG, "User 1 added with ID: " + userId1))
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error adding user 1", e));


        // User 2 test (Sarah Jones)
        Map<String, Object> user2 = new HashMap<>();
        user2.put("first", "Sarah");
        user2.put("last", "Jones");
        user2.put("born", 2000);

        String userId2 = usersRef.push().getKey();

        usersRef.child(userId2).setValue(user2)
                .addOnSuccessListener(aVoid ->
                        Log.d(TAG, "User 2 added with ID: " + userId2))
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error adding user 2", e));
    }
}