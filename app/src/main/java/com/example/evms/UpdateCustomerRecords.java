package com.example.evms;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
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
            default:
                return "";
        }
    }
}

public class UpdateCustomerRecords extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner filterSpinner;
    private ArrayAdapter<String> customerListAdapter;
    private List<SearchableCustomer> fullCustomerList = new ArrayList<>();
    private List<String> customerDisplayList = new ArrayList<>();

    private EditText etSearchField;
    private String currentSearchField = "Name"; // Default search field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_customer_records);

        etSearchField = findViewById(R.id.searchField);
        filterSpinner = findViewById(R.id.filterSpinner);
        ListView customerListView = findViewById(R.id.customerListView);

        customerListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, customerDisplayList);
        customerListView.setAdapter(customerListAdapter);

        setUpFilterSpinner();
        fetchAllCustomers();

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
        for (SearchableCustomer customer : fullCustomerList) {
            if (customer.matchesSearch(searchText, currentSearchField)) {
                customerDisplayList.add(customer.getField(currentSearchField));
            }
        }
        customerListAdapter.notifyDataSetChanged();
    }
}
