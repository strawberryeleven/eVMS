package com.example.evms;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

class SearchableCustomer extends Customer {
    // Constructor that mirrors the superclass
    public SearchableCustomer(String CustomerID, String Name, String Email, String Password, String phoneNumber) {
        super(CustomerID, Name, Email, Password, phoneNumber);
    }

    // Additional method to support searching
    public boolean matchesSearch(String searchText) {
        searchText = searchText.toLowerCase();
        return getCustomerID().toLowerCase().contains(searchText) ||
                getName().toLowerCase().contains(searchText) ||
                getEmail().toLowerCase().contains(searchText) ||
                getPhoneNumber().toLowerCase().contains(searchText);
    }
}

public class UpdateCustomerRecords extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner filterSpinner;
    private ArrayAdapter<String> customerListAdapter;
    private final List<SearchableCustomer> fullCustomerList = new ArrayList<>();
    private final List<String> customerDisplayList = new ArrayList<>();

    private EditText etCustomerId, etEmail, etName, etPassword, etPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_customer_records);

        // Initialize components with their IDs
        // Changed to EditText as per layout
        EditText searchField = findViewById(R.id.searchField);
        filterSpinner = findViewById(R.id.filterSpinner);
        ListView customerListView = findViewById(R.id.customerListView);
        etCustomerId = findViewById(R.id.etCustomerId);
        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        Button confirmEditButton = findViewById(R.id.confirmEditButton);

        // Set up ListView and adapters
        customerListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, customerDisplayList);
        customerListView.setAdapter(customerListAdapter);

        setUpFilterSpinner();
        fetchAllCustomers();

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchCustomer(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        confirmEditButton.setOnClickListener(v -> saveCustomerChanges());
    }

    private void fetchAllCustomers() {
        db.collection("customers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fullCustomerList.clear();
                customerDisplayList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    SearchableCustomer customer = new SearchableCustomer(
                            document.getString("CustomerID"),
                            document.getString("Name"),
                            document.getString("Email"),
                            document.getString("Password"),
                            document.getString("phoneNumber")
                    );
                    fullCustomerList.add(customer);
                    customerDisplayList.add(customer.getName()); // Or other details as needed
                }
                customerListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setUpFilterSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.customer_fields, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);
    }

    private void searchCustomer(String searchText) {
        customerDisplayList.clear();
        for (SearchableCustomer customer : fullCustomerList) {
            if (customer.matchesSearch(searchText)) {
                customerDisplayList.add(customer.getName()); // Adjust based on display preferences
            }
        }
        customerListAdapter.notifyDataSetChanged();
    }

    private void saveCustomerChanges() {
        String customerId = etCustomerId.getText().toString();
        db.collection("customers").document(customerId)
                .update(
                        "CustomerID", etCustomerId.getText().toString(),
                        "Email", etEmail.getText().toString(),
                        "Name", etName.getText().toString(),
                        "Password", etPassword.getText().toString(),
                        "PhoneNumber", etPhoneNumber.getText().toString()
                )
                .addOnSuccessListener(aVoid -> {
                    // Confirmation of success, e.g., Toast
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }
}
