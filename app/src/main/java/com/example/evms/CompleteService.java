package com.example.evms;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class CompleteService extends AppCompatActivity {

    private EditText etEmployeeRemarks, etServiceRating;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String serviceId, employeeId, numberPlate, serviceDate, customerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_service);

        etEmployeeRemarks = findViewById(R.id.etEmployeeRemarks);
        Button btnCompleteService = findViewById(R.id.btnCompleteService);

        // Retrieve passed data from intent
        serviceId = getIntent().getStringExtra("ServiceID");
        employeeId = getIntent().getStringExtra("EmployeeID");
        numberPlate = getIntent().getStringExtra("NumberPlate");
        serviceDate = getIntent().getStringExtra("MaintenanceDate");
        customerEmail = getIntent().getStringExtra("CustomerEmail");

        btnCompleteService.setOnClickListener(v -> completeService());
    }

    private void completeService() {
        String remarks = etEmployeeRemarks.getText().toString().trim();


        if (remarks.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> serviceHistoryEntry = new HashMap<>();
        serviceHistoryEntry.put("CustomerEmail", customerEmail);
        serviceHistoryEntry.put("ServiceID", serviceId);
        serviceHistoryEntry.put("EmployeeID", employeeId);
        serviceHistoryEntry.put("NumberPlate", numberPlate);
        serviceHistoryEntry.put("ServiceDate", serviceDate);
        serviceHistoryEntry.put("EmployeeRemarks", remarks);
        serviceHistoryEntry.put("FeedbackStatus", "Pending");
        serviceHistoryEntry.put("ServiceRating", Double.parseDouble("0")); // Convert string rating to double

        db.collection("ServiceHistory").add(serviceHistoryEntry)
                .addOnSuccessListener(documentReference -> deletePendingService())
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving service history", Toast.LENGTH_SHORT).show());
    }

    private void deletePendingService() {
        Query deleteQuery = db.collection("PendingService")
                .whereEqualTo("ServiceID", serviceId)
                .whereEqualTo("NumberPlate", numberPlate)
                .whereEqualTo("MaintenanceDate", serviceDate);

        deleteQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                task.getResult().getDocuments().get(0).getReference().delete()
                        .addOnSuccessListener(aVoid -> retrieveServicePriceAndCreateNotification())
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete from PendingService", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "No matching service found in PendingService to delete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveServicePriceAndCreateNotification() {
        db.collection("Service").whereEqualTo("ServiceID", serviceId)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String servicePrice = task.getResult().getDocuments().get(0).getString("ServicePrice");
                        createNotification(servicePrice);
                    } else {
                        Toast.makeText(this, "Service details not found.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createNotification(String servicePrice) {
        String notificationText = "Make Payment: " + servicePrice;
        Map<String, Object> notification = new HashMap<>();
        notification.put("CustomerEmail", customerEmail);
        notification.put("Notification", notificationText);

        db.collection("Notification").add(notification)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Service completed successfully!", Toast.LENGTH_SHORT).show();
                    finish();  // Close this activity
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to create notification", Toast.LENGTH_SHORT).show());
    }

}