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
    private Button btnCompleteService;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String serviceId, employeeId, numberPlate, serviceDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_service);

        etEmployeeRemarks = findViewById(R.id.etEmployeeRemarks);
        etServiceRating = findViewById(R.id.etServiceRating);
        btnCompleteService = findViewById(R.id.btnCompleteService);

        // Retrieve passed data from intent
        serviceId = getIntent().getStringExtra("ServiceID");
        employeeId = getIntent().getStringExtra("EmployeeID");
        numberPlate = getIntent().getStringExtra("NumberPlate");
        serviceDate = getIntent().getStringExtra("MaintenanceDate");

        btnCompleteService.setOnClickListener(v -> completeService());
    }

    private void completeService() {
        String remarks = etEmployeeRemarks.getText().toString().trim();
        String rating = etServiceRating.getText().toString().trim();

        if (remarks.isEmpty() || rating.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        // Prepare the document to store in ServiceHistory
        Map<String, Object> serviceHistoryEntry = new HashMap<>();
        serviceHistoryEntry.put("ServiceID", serviceId);
        serviceHistoryEntry.put("EmployeeID", employeeId);
        serviceHistoryEntry.put("NumberPlate", numberPlate);
        serviceHistoryEntry.put("ServiceDate", serviceDate);
        serviceHistoryEntry.put("EmployeeRemarks", remarks);
        serviceHistoryEntry.put("ServiceRating", Double.parseDouble(rating)); // Convert string rating to double

        // Add to ServiceHistory and delete from PendingService
        db.collection("ServiceHistory").add(serviceHistoryEntry)
                .addOnSuccessListener(documentReference -> {
                    // Query to find and delete the matching document in PendingService
                    Query deleteQuery = db.collection("PendingService")
                            .whereEqualTo("ServiceID", serviceId)
                            .whereEqualTo("NumberPlate", numberPlate)
                            .whereEqualTo("MaintenanceDate", serviceDate);

                    deleteQuery.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            task.getResult().getDocuments().get(0).getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Service completed successfully!", Toast.LENGTH_SHORT).show();
                                        finish();  // Close this activity
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to remove service from pending services", Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(this, "No matching service found in pending services to delete", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving service history", Toast.LENGTH_SHORT).show());
    }
}
