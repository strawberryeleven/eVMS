package com.example.evms;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class customerSearchService extends AppCompatActivity {

    private EditText searchInput;
    private Button searchButton;
    private RecyclerView servicesRecyclerView;
    private LinearLayout servicesHeader;
    private TextView resultStatement;
    private FirebaseFirestore db;

    private ServiceAdapter serviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search_service);

        // Initialize the views
        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);
        servicesRecyclerView = findViewById(R.id.servicesRecyclerView);
        servicesHeader = findViewById(R.id.servicesHeader);
        resultStatement = findViewById(R.id.resultStatement);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Setup the RecyclerView
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        serviceAdapter = new ServiceAdapter(this, new ArrayList<>(), v -> {
            // You can handle click events on items here
            // This lambda expression is essentially an OnClickListener, where 'v' is the view that was clicked
            int pos = servicesRecyclerView.getChildAdapterPosition(v);
            if (pos != RecyclerView.NO_POSITION) {
                Service service = serviceAdapter.getItemAt(pos);
                serviceAdapter.showServiceDetailsDialog(service);
            }
        });
        servicesRecyclerView.setAdapter(serviceAdapter);

        // Setup search button behavior
        searchButton.setOnClickListener(v -> performSearch(searchInput.getText().toString()));
        resultStatement.setVisibility(View.GONE);
    }

    private void performSearch(String searchQuery) {
        db.collection("Service")
                .whereEqualTo("ServiceType", searchQuery)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        ArrayList<Service> services = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            services.add(document.toObject(Service.class));
                        }
                        if (!services.isEmpty()) {
                            serviceAdapter.updateData(services);
                            resultStatement.setText("Showing " + services.size() + " results for: " + searchQuery);
                        } else {
                            resultStatement.setText("No results found for: " + searchQuery);
                        }
                        resultStatement.setVisibility(View.VISIBLE);
                    } else {
                        resultStatement.setText("Search failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error occurred"));
                        resultStatement.setVisibility(View.VISIBLE);
                    }
                });
    }
}
