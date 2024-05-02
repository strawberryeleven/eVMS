package com.example.evms;

import android.content.Intent;
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

    private static final int SCAN_REQUEST_CODE = 1; // Define a request code for your scan activity
    private EditText etEmployeeRemarks, etServiceRating;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button btnCompleteService;
    private String serviceId, employeeId, numberPlate, serviceDate, customerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_service);

        etEmployeeRemarks = findViewById(R.id.etEmployeeRemarks);
        btnCompleteService = findViewById(R.id.btnCompleteService);
        Button btnVerifyVehicle = findViewById(R.id.btnVerifyVehicle);

        // Retrieve passed data from intent
        serviceId = getIntent().getStringExtra("ServiceID");
        employeeId = getIntent().getStringExtra("EmployeeID");
        numberPlate = getIntent().getStringExtra("NumberPlate");
        serviceDate = getIntent().getStringExtra("MaintenanceDate");
        customerEmail = getIntent().getStringExtra("CustomerEmail");

        btnVerifyVehicle.setOnClickListener(v -> {
            Intent intent = new Intent(this, employeeScanNumberPlate.class);
            startActivityForResult(intent, SCAN_REQUEST_CODE);
        });

        btnCompleteService.setOnClickListener(v -> completeService());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String scannedNumberPlate = data.getStringExtra("numberPlate");
            verifyServiceRecord(scannedNumberPlate);
        }
    }

    private void verifyServiceRecord(String scannedNumberPlate) {
        if (!scannedNumberPlate.equals(numberPlate)) {
            Toast.makeText(this, "InvalidServiceRecord: Number plate does not match", Toast.LENGTH_LONG).show();
            btnCompleteService.setEnabled(false);
            return;
        }

        // Query Firestore to validate CustomerEmail and NumberPlate
        db.collection("Vehicles")
                .whereEqualTo("CustomerEmail", customerEmail)
                .whereEqualTo("NumberPlate", numberPlate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        btnCompleteService.setEnabled(true);
                    } else {
                        Toast.makeText(this, "InvalidServiceRecord: No matching vehicle found", Toast.LENGTH_LONG).show();
                        btnCompleteService.setEnabled(false);
                    }
                });
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
        serviceHistoryEntry.put("ServiceRating", "0");

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
        String notificationText = "Make Payment: " + servicePrice; // Constructs the notification message
        Map<String, Object> notification = new HashMap<>();
        notification.put("CustomerEmail", customerEmail);
        notification.put("NotificationText", notificationText); // Assuming field name in Firestore is "NotificationText"
        notification.put("NotificationType", "payment"); // Specifies the type of notification
        notification.put("Payment", servicePrice); // Specifies the payment amount
        notification.put("Status", "Pending"); // Sets the status of the notification
        notification.put("GatepassID", ""); // Leaves GatepassID empty as specified
        notification.put("ServiceID", serviceId); // Adds the service ID
        notification.put("ServiceDate", serviceDate); // Adds the service date
        notification.put("NumberPlate", numberPlate); // Adds the number plate

        // Adding the notification to the Firestore collection
        db.collection("Notification").add(notification)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Service completed successfully!", Toast.LENGTH_SHORT).show();
                    finish();  // Close this activity after successful creation of the notification
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to create notification", Toast.LENGTH_SHORT).show());
    }
}