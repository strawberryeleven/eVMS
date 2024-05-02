package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class customerHomepage extends AppCompatActivity {

    private String customerEmail;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_homepage);

        db = FirebaseFirestore.getInstance();  // Initialize Firestore

        // Retrieve the email passed from loginCustomer
        Intent intent = getIntent();
        customerEmail = intent.getStringExtra("customerEmail");
        Toast.makeText(this, "Logged in as: " + customerEmail, Toast.LENGTH_LONG).show();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button searchButton = findViewById(R.id.button3);
        Button testButton = findViewById(R.id.btn_testEmail);
        Button historyButton = findViewById(R.id.btnViewHistory);
        ImageButton viewVehiclesButton = findViewById(R.id.btnViewVehicles); // ImageButton

        displayPendingService();

        setupButtonListeners(searchButton, testButton, historyButton, viewVehiclesButton);
    }

    private void setupButtonListeners(Button searchButton, Button testButton, Button historyButton, ImageButton viewVehiclesButton) {
        searchButton.setOnClickListener(v -> navigateTo(customerSearchService.class));
        testButton.setOnClickListener(v -> navigateTo(testEmailPage.class));
        historyButton.setOnClickListener(v -> navigateTo(customerViewHistory.class));
        viewVehiclesButton.setOnClickListener(v -> navigateTo(customerViewVehicles.class));
    }

    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(customerHomepage.this, cls);
        intent.putExtra("customerEmail", customerEmail);
        startActivity(intent);
        finish();
    }

    private void displayPendingService() {
        db.collection("PendingService")
                .whereEqualTo("CustomerEmail", customerEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String serviceId = document.getString("ServiceId");
                                if (serviceId == null) {
                                    Toast.makeText(this, "Service ID is null for some records", Toast.LENGTH_SHORT).show();
                                    continue; // Skip this iteration if serviceId is null
                                }
                                String maintenanceDate = document.getString("MaintenanceDate");
                                String numberPlate = document.getString("NumberPlate");

                                db.collection("Service").document(serviceId)
                                        .get()
                                        .addOnSuccessListener(serviceDocument -> {
                                            if (serviceDocument.exists()) {
                                                String serviceName = serviceDocument.getString("ServiceName");
                                                String servicePrice = serviceDocument.getString("ServicePrice");

                                                ((TextView) findViewById(R.id.tvServiceName)).setText(serviceName);
                                                ((TextView) findViewById(R.id.tvServicePrice)).setText(String.format("Price: %s", servicePrice));
                                                ((TextView) findViewById(R.id.tvMaintenanceDate)).setText(String.format("Date: %s", maintenanceDate));
                                                ((TextView) findViewById(R.id.tvNumberPlate)).setText(String.format("Plate: %s", numberPlate));
                                            }
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(customerHomepage.this, "Failed to fetch service details", Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            ((TextView) findViewById(R.id.tvServiceName)).setText("No pending services found");
                        }
                    } else {
                        Toast.makeText(customerHomepage.this, "Failed to load pending services", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(customerHomepage.this, "Error fetching services", Toast.LENGTH_LONG).show());
    }

}