package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class customerViewHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ServiceHistoryAdapter serviceHistoryAdapter;
    private ArrayList<ServiceHistory> serviceHistoryList = new ArrayList<>();

    private FirebaseFirestore db;
    private String customerEmail;
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_view_history);

        // Setup Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firestore and RecyclerView
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.serviceHistoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        serviceHistoryAdapter = new ServiceHistoryAdapter(serviceHistoryList);
        recyclerView.setAdapter(serviceHistoryAdapter);

        // Fetch customer email from intent
        Intent intent = getIntent();
        customerEmail = intent.getStringExtra("customerEmail");
        Toast.makeText(this, "Fetching records for: " + customerEmail, Toast.LENGTH_LONG).show();

        // Load Vehicles from Firestore
        loadServiceHistory(customerEmail);

        backButton = findViewById(R.id.backButton);
        // Set up the listener for the back button
        backButton.setOnClickListener(v->onBackPressed());
    }
    @Override
    public void onBackPressed() {
        // Check if there are fragments in the back stack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // If there are fragments in the back stack, pop the fragment
            getSupportFragmentManager().popBackStack();
        } else {
            // If there are no fragments in the back stack, perform default back button behavior
            super.onBackPressed();
        }
    }

    private void loadServiceHistory(String email) {
        db.collection("ServiceHistory").whereEqualTo("CustomerEmail", email)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String serviceId = document.getString("ServiceID");
                            db.collection("Service").whereEqualTo("ServiceID", serviceId).get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        if (!querySnapshot.isEmpty()) {
                                            DocumentSnapshot serviceDoc = querySnapshot.getDocuments().get(0);
                                            Service service = serviceDoc.toObject(Service.class);

                                            ServiceHistory history = document.toObject(ServiceHistory.class);
                                            history.setServiceName(service.getServiceName()); // Make sure this method exists and works correctly
                                            history.setServicePrice(service.getServicePrice()); // Make sure this method exists and works correctly

                                            serviceHistoryList.add(history);
                                            serviceHistoryAdapter.notifyDataSetChanged(); // Notify adapter
                                        } else {
                                            Log.d("Service Fetch", "No service found for ID: " + serviceId);
                                        }
                                    }).addOnFailureListener(e -> Log.e("Service Fetch", "Error fetching service details", e));
                        }
                    } else {
                        Toast.makeText(customerViewHistory.this, "Error loading Service History: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(customerViewHistory.this, "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }




}
