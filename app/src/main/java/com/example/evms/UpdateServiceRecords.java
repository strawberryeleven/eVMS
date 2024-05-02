package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UpdateServiceRecords extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayAdapter<String> adapter;
    private List<String> servicesList = new ArrayList<>();
    private List<QueryDocumentSnapshot> documents = new ArrayList<>();
    private String employeeId;  // Variable to store EmployeeID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_service_records);
        ListView listViewPendingServices = findViewById(R.id.listViewPendingServices);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, servicesList);
        listViewPendingServices.setAdapter(adapter);

        // Retrieve EmployeeID from the intent extras
        employeeId = getIntent().getStringExtra("EmployeeID");

        fetchPendingServices();

        listViewPendingServices.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(UpdateServiceRecords.this, CompleteService.class);
            QueryDocumentSnapshot selectedService = documents.get(position);
            intent.putExtra("ServiceID", selectedService.getString("ServiceID"));
            intent.putExtra("EmployeeID", employeeId);
            intent.putExtra("NumberPlate", selectedService.getString("NumberPlate"));
            intent.putExtra("MaintenanceDate", selectedService.getString("MaintenanceDate"));
            String customerEmail = selectedService.getString("CustomerEmail");
            intent.putExtra("CustomerEmail", customerEmail);
            startActivity(intent);
        });
    }

    private void fetchPendingServices() {
        db.collection("PendingService").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                documents.clear(); // Clear previous data
                servicesList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String serviceId = document.getString("ServiceID");
                    String customerEmail = document.getString("CustomerEmail");
                    String maintenanceDate = document.getString("MaintenanceDate");
                    String numberPlate = document.getString("NumberPlate");

                    if (serviceId != null && !serviceId.isEmpty() &&
                            customerEmail != null && !customerEmail.isEmpty() &&
                            maintenanceDate != null && !maintenanceDate.isEmpty() &&
                            numberPlate != null && !numberPlate.isEmpty()) {

                        documents.add(document);
                        String serviceInfo = "Service ID: " + serviceId +
                                "\nCustomer Email: " + customerEmail +
                                "\nMaintenance Date: " + maintenanceDate +
                                "\nNumber Plate: " + numberPlate;
                        servicesList.add(serviceInfo);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                System.out.println("Error getting documents: " + task.getException());
            }
        });
    }
}