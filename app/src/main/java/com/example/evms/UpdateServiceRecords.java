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

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView listViewPendingServices;
    private ArrayAdapter<String> adapter;
    private List<String> servicesList = new ArrayList<>();
    private List<QueryDocumentSnapshot> documents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_service_records);
        listViewPendingServices = findViewById(R.id.listViewPendingServices);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, servicesList);
        listViewPendingServices.setAdapter(adapter);

        fetchPendingServices();

        listViewPendingServices.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(UpdateServiceRecords.this, CompleteService.class);
            QueryDocumentSnapshot selectedService = documents.get(position);
            intent.putExtra("ServiceID", selectedService.getString("ServiceID"));
            intent.putExtra("EmployeeID", "E001"); // Assuming EmployeeID is known or static in this context
            intent.putExtra("NumberPlate", selectedService.getString("NumberPlate"));
            intent.putExtra("MaintenanceDate", selectedService.getString("MaintenanceDate"));
            startActivity(intent);
        });
    }

    private void fetchPendingServices() {
        db.collection("PendingService").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                documents.clear(); // Clear previous data
                servicesList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    documents.add(document); // Save the document snapshot for later use
                    String serviceInfo = "Service ID: " + document.getString("ServiceID") +
                            "\nCustomer Email: " + document.getString("CustomerEmail") +
                            "\nMaintenance Date: " + document.getString("MaintenanceDate") +
                            "\nNumber Plate: " + document.getString("NumberPlate");
                    servicesList.add(serviceInfo);
                }
                adapter.notifyDataSetChanged();
            } else {
                System.out.println("Error getting documents: " + task.getException());
            }
        });
    }
}
