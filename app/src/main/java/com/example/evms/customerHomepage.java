package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

        ImageView notificationIcon = findViewById(R.id.notificationIcon);
        notificationIcon.setOnClickListener(v -> {
            Intent notificationIntent = new Intent(customerHomepage.this, CustomerNotificationPanel.class);  // Renamed the variable here
            notificationIntent.putExtra("customerEmail", customerEmail);  // Pass the customerEmail to the NotificationPanel activity
            startActivity(notificationIntent);
        });

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

        fetchNotificationCount();
    }

    private void fetchNotificationCount() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Notification") // Ensure the collection name is correct
                .whereEqualTo("CustomerEmail", customerEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        final int count = task.getResult().size(); // Count the documents which match the query
                        runOnUiThread(() -> {
                            updateNotificationBadge(count); // Update the notification badge
                            Toast.makeText(customerHomepage.this, "You have " + count + " notifications", Toast.LENGTH_LONG).show(); // Show the count in a toast
                        });
                    } else {
                        Log.d("FetchNotifError", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void updateNotificationBadge(int count) {
        TextView notificationCount = findViewById(R.id.notificationCount);
        if (count > 0) {
            notificationCount.setText(String.valueOf(count));
            notificationCount.setVisibility(View.VISIBLE);
        } else {
            notificationCount.setVisibility(View.GONE);
        }
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