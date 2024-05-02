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

import java.util.List;
import java.util.ArrayList;

public class customerHomepage extends AppCompatActivity {

    private String customerEmail;
    private FirebaseFirestore db;

    TextView serviceNameTextView ;
    TextView servicePriceTextView ;
    TextView maintenanceDateTextView ;
    TextView numberPlateTextView;

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

        ImageView pendingServiceIcon = findViewById(R.id.btnViewPendingServices);
        pendingServiceIcon.setOnClickListener(v -> {
            Intent pendingServiceIntent = new Intent(customerHomepage.this, customerViewPendingServices.class);
            pendingServiceIntent.putExtra("customerEmail", customerEmail);
            startActivity(pendingServiceIntent);
        });

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

        setupButtonListeners(searchButton, testButton, historyButton, viewVehiclesButton);

        serviceNameTextView = findViewById(R.id.tvServiceName);
        servicePriceTextView = findViewById(R.id.tvServicePrice);
        maintenanceDateTextView = findViewById(R.id.tvMaintenanceDate);
        numberPlateTextView = findViewById(R.id.tvNumberPlate);

        serviceNameTextView.setVisibility(View.GONE);
        servicePriceTextView.setVisibility(View.GONE);
        maintenanceDateTextView.setVisibility(View.GONE);
        numberPlateTextView.setVisibility(View.GONE);

        fetchNotificationCount();
        displayPendingService();
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

    public void getAllPendingService(OnPendingServiceListListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PendingService")
                .whereEqualTo("CustomerEmail", customerEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<PendingService> pendingServiceList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        PendingService pendingService = document.toObject(PendingService.class);
                        pendingServiceList.add(pendingService);
                    }
                    // Pass the list to the listener
                    listener.onPendingServiceList(pendingServiceList);
                })
                .addOnFailureListener(e -> {
                    // Handle the failure appropriately
                    listener.onFailure(e.getMessage());
                });
    }

    // Define an interface for the listener
    public interface OnPendingServiceListListener {
        void onPendingServiceList(List<PendingService> pendingServiceList);
        void onFailure(String errorMessage);
    }
    private void displayPendingService() {
        getAllPendingService(new OnPendingServiceListListener() {
            @Override
            public void onPendingServiceList(List<PendingService> pendingServiceList) {
                // Store the list of pending services in a variable
                List<PendingService> pendingServices = pendingServiceList;
                PendingService pend = PendingService.filterReturnUpcoming(pendingServices);

                // Check if the upcoming pending service is found
                if (pend != null) {
                    Log.d("Pend service id = ", pend.getServiceID());
                    pend.getServiceDetails(FirebaseFirestore.getInstance(), new PendingService.OnServiceDetailsListener() {
                        @Override
                        public void onServiceDetails(String serviceName, String servicePrice) {
                            updatePendingServiceUI(serviceName,servicePrice,pend.getMaintenanceDate(),pend.getNumberPlate());
                            Toast.makeText(customerHomepage.this, "Service details fetched successfully", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.e("DisplayPendingService", "Failed to fetch service details: " + errorMessage);
                        }
                    });
                } else {
                    // Handle case where no upcoming pending service is found
                    Log.d("DisplayPendingService", "No upcoming pending service found");
                    // You can display a message or take other actions as needed
                }
                // Now you can use the pendingServices list as needed, such as displaying it in the UI
                // For example, you can update TextViews, RecyclerViews, etc.
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle failure appropriately
                Log.e("DisplayPendingService", "Failed to fetch pending services: " + errorMessage);
                // You can display an error message or take other actions as needed
            }
        });
    }
    private void updatePendingServiceUI(String serviceName, String servicePrice, String maintenanceDate, String numberPlate) {
        // Update UI with pending service details
        ((TextView) findViewById(R.id.tvServiceName)).setText(serviceName);
        ((TextView) findViewById(R.id.tvServicePrice)).setText(String.format("Price: %s", servicePrice));
        ((TextView) findViewById(R.id.tvMaintenanceDate)).setText(String.format("Date: %s", maintenanceDate));
        ((TextView) findViewById(R.id.tvNumberPlate)).setText(String.format("Plate: %s", numberPlate));
        serviceNameTextView.setVisibility(View.VISIBLE);
        servicePriceTextView.setVisibility(View.VISIBLE);
        maintenanceDateTextView.setVisibility(View.VISIBLE);
        numberPlateTextView.setVisibility(View.VISIBLE);
    }

}