//package com.example.evms;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//
//import java.util.ArrayList;
//
//public class customerSearchService extends AppCompatActivity {
//
//    private EditText searchInput;
//    private Button searchButton;
//    private RecyclerView servicesRecyclerView;
//    private LinearLayout servicesHeader;
//    private TextView resultStatement;
//    private FirebaseFirestore db;
//    private String CustomerEmail;
//    private ServiceAdapter serviceAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_customer_search_service);
//
//        // Initialize the views
//        searchInput = findViewById(R.id.searchInput);
//        searchButton = findViewById(R.id.searchButton);
//        servicesRecyclerView = findViewById(R.id.servicesRecyclerView);
//        servicesHeader = findViewById(R.id.servicesHeader);
//        resultStatement = findViewById(R.id.resultStatement);
//
//        // Initialize Firebase Firestore
//        db = FirebaseFirestore.getInstance();
//
//        Intent intent = getIntent();
//        CustomerEmail = intent.getStringExtra("customerEmail");
//
//
//
//        // Setup the RecyclerView
//        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        serviceAdapter = new ServiceAdapter(this, new ArrayList<>(), v -> {
//            // You can handle click events on items here
//            // This lambda expression is essentially an OnClickListener, where 'v' is the view that was clicked
//            int pos = servicesRecyclerView.getChildAdapterPosition(v);
//            if (pos != RecyclerView.NO_POSITION) {
//                Service service = serviceAdapter.getItemAt(pos);
//                serviceAdapter.showServiceDetailsDialog(service);
//            }
//        }, CustomerEmail); // Pass the userEmail here);
//
//
//        servicesRecyclerView.setAdapter(serviceAdapter);
//
//        // Setup search button behavior
//        searchButton.setOnClickListener(v -> performSearch(searchInput.getText().toString()));
//        resultStatement.setVisibility(View.GONE);
//    }
//
//    private void performSearch(String searchQuery) {
//        db.collection("Service")
//                .whereEqualTo("ServiceType", searchQuery)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        ArrayList<Service> services = new ArrayList<>();
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            services.add(document.toObject(Service.class));
//                        }
//                        if (!services.isEmpty()) {
//                            serviceAdapter.updateData(services);
//                            resultStatement.setText("Showing " + services.size() + " results for: " + searchQuery);
//                        } else {
//                            resultStatement.setText("No results found for: " + searchQuery);
//                        }
//                        resultStatement.setVisibility(View.VISIBLE);
//                    } else {
//                        resultStatement.setText("Search failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error occurred"));
//                        resultStatement.setVisibility(View.VISIBLE);
//                    }
//                });
//    }
//}

package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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
    private String CustomerEmail;
    private ServiceAdapter serviceAdapter;
    private ImageButton filterButton;
    private Button backButton;

    // New UI elements for filter dialog
    private EditText editTextLowerPrice;
    private EditText editTextUpperPrice;
    private EditText editTextLowerRating;
    private EditText editTextUpperRating;
    private RadioGroup radioGroupSearchBy;
    private RadioButton radioButtonType;
    private RadioButton radioButtonName;
    EditText editTextSearchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search_service);

        // Initialize the views
        backButton = findViewById((R.id.backButton));
        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);
        servicesRecyclerView = findViewById(R.id.servicesRecyclerView);
        servicesHeader = findViewById(R.id.servicesHeader);
        resultStatement = findViewById(R.id.resultStatement);
        filterButton = findViewById(R.id.btnViewFilters);
        editTextSearchQuery = findViewById(R.id.editTextSearchQuery);
        editTextLowerPrice = findViewById(R.id.editTextLowerPrice);
        editTextUpperPrice = findViewById(R.id.editTextUpperPrice);
        editTextLowerRating = findViewById(R.id.editTextLowerRating);
        editTextUpperRating = findViewById(R.id.editTextUpperRating);
        radioGroupSearchBy = findViewById(R.id.radioGroupSearchBy);
        radioButtonType = findViewById(R.id.radioButtonType);
        radioButtonName = findViewById(R.id.radioButtonName);


        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        CustomerEmail = intent.getStringExtra("customerEmail");

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
        }, CustomerEmail); // Pass the userEmail here);
        filterButton.setOnClickListener(v -> setFilterButton());

        servicesRecyclerView.setAdapter(serviceAdapter);

        // Setup search button behavior
        searchButton.setOnClickListener(v -> performSearch(searchInput.getText().toString()));
        backButton.setOnClickListener(v->onBackPressed());

        resultStatement.setVisibility(View.GONE);
    }
    @Override
    public void onBackPressed() {
        // Check if there are fragments in the back stack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // If there are fragments in the back stack, pop the fragment
            getSupportFragmentManager().popBackStack();
        } else {
            // If there are no fragments in the back stack, perform default back button behavior
            super.onBackPressed();
        }
    }

    class SearchParameters{
        public String Type_Name; // co
        public float ratingLower ;
        public float ratingUpper;
        public float priceLower;
        public float priceUpper;
        SearchParameters(){
            Type_Name = "ServiceType";
            ratingLower = 0;
            ratingUpper = 5;
            priceLower = 0;
            priceUpper = Float.MAX_VALUE;
        }
        public String getType_Name(){
            return Type_Name;
        }
    }


    public void setFilterButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Filters");

        // Inflate the layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_filter_options, null);
        builder.setView(dialogView);

        // Initialize new UI elements
        editTextSearchQuery = dialogView.findViewById(R.id.editTextSearchQuery);
        editTextLowerPrice = dialogView.findViewById(R.id.editTextLowerPrice);
        editTextUpperPrice = dialogView.findViewById(R.id.editTextUpperPrice);
        editTextLowerRating = dialogView.findViewById(R.id.editTextLowerRating);
        editTextUpperRating = dialogView.findViewById(R.id.editTextUpperRating);
        radioGroupSearchBy = dialogView.findViewById(R.id.radioGroupSearchBy);
        radioButtonType = dialogView.findViewById(R.id.radioButtonType);
        radioButtonName = dialogView.findViewById(R.id.radioButtonName);

        // Add action buttons
        builder.setPositiveButton("Apply", (dialog, which) -> {
            // Retrieve user selections and modify search query accordingly
            SearchParameters searchParameters = new SearchParameters();

            // Retrieve selected search type (type or name)
            int selectedRadioButtonId = radioGroupSearchBy.getCheckedRadioButtonId();
            if (selectedRadioButtonId == R.id.radioButtonType) {
                searchParameters.Type_Name = "ServiceType";
            } else if (selectedRadioButtonId == R.id.radioButtonName) {
                searchParameters.Type_Name = "ServiceName";
            }
            String searchQuery = editTextSearchQuery.getText().toString().trim();

// Retrieve price range values
            String lowerPriceText = editTextLowerPrice.getText().toString().trim();
            String upperPriceText = editTextUpperPrice.getText().toString().trim();
            float priceLower = lowerPriceText.isEmpty() ? 0.0f : Float.parseFloat(lowerPriceText);
            float priceUpper = upperPriceText.isEmpty() ? Float.MAX_VALUE : Float.parseFloat(upperPriceText);

// Retrieve rating range values
            String lowerRatingText = editTextLowerRating.getText().toString().trim();
            String upperRatingText = editTextUpperRating.getText().toString().trim();
            float ratingLower = lowerRatingText.isEmpty() ? 0.0f : Float.parseFloat(lowerRatingText);
            float ratingUpper = upperRatingText.isEmpty() ? 5.0f : Float.parseFloat(upperRatingText);

// Update search parameters
            searchParameters.priceLower = priceLower;
            searchParameters.priceUpper = priceUpper;
            searchParameters.ratingLower = ratingLower;
            searchParameters.ratingUpper = ratingUpper;

            // Perform search with modified query
            performAdvancedSearch(searchQuery, searchParameters);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // User clicked cancel, do nothing
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void performAdvancedSearch(String searchQuery , SearchParameters SearchParam) {


        db.collection("Service")
                .whereEqualTo(SearchParam.Type_Name, searchQuery)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        ArrayList<Service> services = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            services.add(document.toObject(Service.class));
                        }
                        services = removeOutOfParameters(services,SearchParam);

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
    public ArrayList<Service> removeOutOfParameters(ArrayList<Service> services, SearchParameters searchParam) {
        ArrayList<Service> filteredServices = new ArrayList<>();

        for (Service service : services) {

            // Convert price and rating from string to float for comparison
            float servicePrice = Float.parseFloat(service.getServicePrice());
            double rating = service.getServiceRating(); // Getting the rating as a double
            float serviceRating = (float) rating;

            // Check if the service price is within the specified range
            float lowerPrice = searchParam.priceLower;
            float upperPrice = searchParam.priceUpper;
            if ((lowerPrice != upperPrice) && (servicePrice < lowerPrice || servicePrice > upperPrice)) {
                continue; // Skip this service as it's outside the price range
            }

            // Check if the service rating is within the specified range
            float lowerRating = searchParam.ratingLower;
            float upperRating = searchParam.ratingUpper;
            if ((lowerRating != upperRating) && (serviceRating < lowerRating || serviceRating > upperRating)) {
                continue; // Skip this service as it's outside the rating range
            }

            // If the service meets all criteria, add it to the filtered list
            filteredServices.add(service);
        }

        return filteredServices;
    }
}