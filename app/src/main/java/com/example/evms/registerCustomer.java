package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

public class registerCustomer extends AppCompatActivity {

    EditText nameField, emailField, passwordField, phoneField;
    Button signUpButton;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Link UI elements
        nameField = findViewById(R.id.editTextCustomerName);
        emailField = findViewById(R.id.editTextCustomerEmail);
        passwordField = findViewById(R.id.editTextCustomerPassword);
        phoneField = findViewById(R.id.editTextCustomerPhone);
        signUpButton = findViewById(R.id.button);

        signUpButton.setOnClickListener(view -> attemptSignUp());
    }

    private void attemptSignUp() {
        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Sign Up");
        builder.setMessage("Are you sure you want to sign up?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> createCustomerAccount(name, email, password, phone));
        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }

    private void createCustomerAccount(String name, String email, String password, String phone) {
        db.collection("Customers")
                .orderBy("CustomerID", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String newID;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String lastID = queryDocumentSnapshots.getDocuments().get(0).getString("CustomerID");
                        // Extract the numerical part of the ID and increment it
                        int idNumber = Integer.parseInt(lastID.substring(1)) + 1;
                        // Format the new ID with leading zeros to maintain the CXXX format
                        newID = String.format("C%03d", idNumber);
                    } else {
                        // This is the case where there are no customers in the database yet
                        newID = "C001";
                    }

                    // Create a hashmap with the correct case for the keys
                    HashMap<String, Object> customerData = new HashMap<>();
                    customerData.put("CustomerID", newID);
                    customerData.put("Name", name);
                    customerData.put("Email", email);
                    customerData.put("Password", password);
                    customerData.put("phoneNumber", phone);

                    db.collection("Customers").document(newID)
                            .set(customerData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(registerCustomer.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                // Redirect to Customer Homepage
                                Intent intent = new Intent(registerCustomer.this, customerHomepage.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(registerCustomer.this, "Failed to create account: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(registerCustomer.this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


}
