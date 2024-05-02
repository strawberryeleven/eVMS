package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class customerViewVehicles extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VehicleAdapter vehicleAdapter;
    private ArrayList<Vehicle> vehicleList = new ArrayList<>();

    private FirebaseFirestore db;
    private String customerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_view_vehicles);

        // Setup Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firestore and RecyclerView
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.vehiclesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vehicleAdapter = new VehicleAdapter(vehicleList);
        recyclerView.setAdapter(vehicleAdapter);

        // Fetch customer email from intent
        Intent intent = getIntent();
        customerEmail = intent.getStringExtra("customerEmail");
        Toast.makeText(this, "Fetching records for: " + customerEmail, Toast.LENGTH_LONG).show();

        // Load Vehicles from Firestore
        loadVehicles(customerEmail);
    }

    private void loadVehicles(String email) {
        Query query = db.collection("Vehicles").whereEqualTo("CustomerEmail", email);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                vehicleList.clear(); // Clear existing data
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    Vehicle vehicle = documentSnapshot.toObject(Vehicle.class);
                    vehicleList.add(vehicle);
                }
                vehicleAdapter.notifyDataSetChanged(); // Notify adapter about data changes
            } else {
                Toast.makeText(customerViewVehicles.this, "Error loading vehicles: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(customerViewVehicles.this, "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
