package com.example.evms;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SearchableCustomer extends Customer {
    public SearchableCustomer(String CustomerID, String Name, String Email, String Password, String phoneNumber) {
        super(CustomerID, Name, Email, Password, phoneNumber);
    }

    public boolean matchesSearch(String searchText, String field) {
        searchText = searchText.toLowerCase();
        switch (field) {
            case "CustomerID":
                return getCustomerID().toLowerCase().contains(searchText);
            case "Name":
                return getName().toLowerCase().contains(searchText);
            case "Email":
                return getEmail().toLowerCase().contains(searchText);
            case "PhoneNumber":
                return getPhoneNumber().toLowerCase().contains(searchText);
            case "Password":  // Add case for Password
                return getPassword().toLowerCase().contains(searchText);
            default:
                return false;
        }
    }


    public String getField(String field) {
        switch (field) {
            case "CustomerID":
                return getCustomerID();
            case "Name":
                return getName();
            case "Email":
                return getEmail();
            case "PhoneNumber":
                return getPhoneNumber();
            case "Password":  // Add case for Password
                return getPassword();
            default:
                return "";
        }
    }

}

public class UpdateCustomerRecords extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner filterSpinner;
    private ArrayAdapter<String> customerListAdapter;
    private List<SearchableCustomer> fullCustomerList = new ArrayList<>();
    private List<String> customerDisplayList = new ArrayList<>();

    private List<Integer> filteredCustomerIndices = new ArrayList<>(); // List to store indices of filtered customers


    private EditText etSearchField;
    private String currentSearchField = "Name"; // Default search field

    private EditText etEmail, etName, etCustomerId, etPassword, etPhoneNumber;

    private Button confirmEditButton;
    private SearchableCustomer currentSelectedCustomer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_customer_records);

        etSearchField = findViewById(R.id.searchField);
        filterSpinner = findViewById(R.id.filterSpinner);
        ListView customerListView = findViewById(R.id.customerListView);
        LinearLayout customerInfoLayout = findViewById(R.id.customerInfoLayout);
        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etCustomerId = findViewById(R.id.etCustomerId);
        etPassword = findViewById(R.id.etPassword);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        confirmEditButton = findViewById(R.id.confirmEditButton);
        customerListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, customerDisplayList);
        customerListView.setAdapter(customerListAdapter);
        fetchAllCustomers();
        setUpFilterSpinner();



        confirmEditButton.setOnClickListener(v -> {
            if (validateInputs()) {
                updateCustomerInDatabase();
            } else {
                Toast.makeText(UpdateCustomerRecords.this, "Please check the inputs. Make sure all fields are filled and valid.", Toast.LENGTH_LONG).show();
            }
        });

        TextWatcher validationWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmEditButton.setEnabled(validateInputs());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etEmail.addTextChangedListener(validationWatcher);
        etName.addTextChangedListener(validationWatcher);
        etCustomerId.addTextChangedListener(validationWatcher);
        etPassword.addTextChangedListener(validationWatcher);
        etPhoneNumber.addTextChangedListener(validationWatcher);
        etSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchCustomer(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSearchField = parent.getItemAtPosition(position).toString();
                searchCustomer(etSearchField.getText().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        customerListView.setOnItemClickListener((parent, view, position, id) -> {
            int originalIndex = filteredCustomerIndices.get(position);  // Get the original index from the filtered list
            currentSelectedCustomer = fullCustomerList.get(originalIndex);  // Use the original index to fetch the customer
            etEmail.setText(currentSelectedCustomer.getEmail());
            etName.setText(currentSelectedCustomer.getName());
            etCustomerId.setText(currentSelectedCustomer.getCustomerID());
            etPassword.setText(currentSelectedCustomer.getPassword());
            etPhoneNumber.setText(currentSelectedCustomer.getPhoneNumber());
        });

        confirmEditButton.setOnClickListener(v -> {
            if (validateInputs()) {
                updateCustomerInDatabase();
            } else {
                Toast.makeText(UpdateCustomerRecords.this, "Please check the inputs. Make sure all fields are filled and valid.", Toast.LENGTH_LONG).show();
            }
        });
    }



    private boolean validateInputs() {
        if (etEmail.getText().toString().isEmpty() || etName.getText().toString().isEmpty() ||
                etCustomerId.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty() ||
                etPhoneNumber.getText().toString().isEmpty()) {
            return false;
        }
        if (!etEmail.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            Toast.makeText(UpdateCustomerRecords.this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!etPhoneNumber.getText().toString().matches("^[+]?[0-9]{10,13}$")) {
            Toast.makeText(UpdateCustomerRecords.this, "Invalid phone number format", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateCustomerInDatabase() {
        if (currentSelectedCustomer != null) {
            String customerID = currentSelectedCustomer.getCustomerID();
            // Create a map to store the updated fields
            Map<String, Object> updatedFields = new HashMap<>();
            updatedFields.put("Email", etEmail.getText().toString());
            updatedFields.put("Name", etName.getText().toString());
            updatedFields.put("Password", etPassword.getText().toString());
            updatedFields.put("phoneNumber", etPhoneNumber.getText().toString());

            // Update the database
            db.collection("Customers").document(customerID)
                    .update(updatedFields)
                    .addOnSuccessListener(aVoid -> Toast.makeText(UpdateCustomerRecords.this, "Customer updated successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(UpdateCustomerRecords.this, "Error updating customer: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
    private void fetchAllCustomers() {
        db.collection("Customers").get().addOnCompleteListener(task -> {
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
                }
                searchCustomer(etSearchField.getText().toString());
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
        filteredCustomerIndices.clear();  // Clear the indices list
        for (int i = 0; i < fullCustomerList.size(); i++) {
            SearchableCustomer customer = fullCustomerList.get(i);
            if (customer.matchesSearch(searchText, currentSearchField)) {
                customerDisplayList.add(customer.getField(currentSearchField));
                filteredCustomerIndices.add(i);  // Store the index of the customer in the full list
            }
        }
        customerListAdapter.notifyDataSetChanged();
    }
}
