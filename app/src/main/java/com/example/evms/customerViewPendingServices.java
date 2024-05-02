package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class customerViewPendingServices extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PendingServiceAdapter pendingServiceAdapter;
    private ArrayList<PendingService> pendingServiceList = new ArrayList<>();

    private FirebaseFirestore db;
    private String customerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_view_pending_services);

        // Setup Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firestore and RecyclerView
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.pendingServicesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pendingServiceAdapter = new PendingServiceAdapter(pendingServiceList);
        recyclerView.setAdapter(pendingServiceAdapter);

        // Fetch customer email from intent
        Intent intent = getIntent();
        customerEmail = intent.getStringExtra("customerEmail");
        Toast.makeText(this, "Fetching records for: " + customerEmail, Toast.LENGTH_LONG).show();

        // Load Vehicles from Firestore
        loadPendingServices(customerEmail);
    }

    private void loadPendingServices(String email) {
        db.collection("PendingService").whereEqualTo("CustomerEmail", email)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String serviceId = document.getString("ServiceID");
                            db.collection("Service").whereEqualTo("ServiceID", serviceId).get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        if (!querySnapshot.isEmpty()) {
                                            DocumentSnapshot serviceDoc = querySnapshot.getDocuments().get(0);
                                            Service service = serviceDoc.toObject(Service.class);

                                            PendingService history = document.toObject(PendingService.class);
                                            history.setServiceName(service.getServiceName()); // Make sure this method exists and works correctly
                                            history.setServicePrice(service.getServicePrice()); // Make sure this method exists and works correctly

                                            pendingServiceList.add(history);
                                            pendingServiceAdapter.notifyDataSetChanged(); // Notify adapter
                                        } else {
                                            Log.d("Service Fetch", "No service found for ID: " + serviceId);
                                        }
                                    }).addOnFailureListener(e -> Log.e("Service Fetch", "Error fetching service details", e));
                        }
                    } else {
                        Toast.makeText(customerViewPendingServices.this, "Error loading Pending Services: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(customerViewPendingServices.this, "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }




}
