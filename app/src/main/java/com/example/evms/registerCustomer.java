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

    private Button backButton;
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

        backButton = findViewById(R.id.backButton);
        // Set up the listener for the back button
        backButton.setOnClickListener(v->onBackPressed());
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

    private void attemptSignUp() {
        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();

        // Name validation: should not contain numbers
        if (!name.matches("[^0-9]+")) {
            Toast.makeText(getApplicationContext(), "Name must not include numbers.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "Enter a valid email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Password validation: should be at least 8 characters
        if (password.length() < 8) {
            Toast.makeText(getApplicationContext(), "Password must be at least 8 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Phone validation: should only contain numbers and be exactly 11 characters long
        if (!phone.matches("\\d{11}")) {
            Toast.makeText(getApplicationContext(), "Phone must be exactly 11 digits.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the email already exists
        db.collection("Customers")
                .whereEqualTo("Email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Email already exists, show error message
                        Toast.makeText(this, "Email is already in use!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Email is unique, proceed with sign up
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Confirm Sign Up");
                        builder.setMessage("Are you sure you want to sign up?");
                        builder.setPositiveButton("Yes", (dialogInterface, i) -> createCustomerAccount(name, email, password, phone));
                        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                        builder.show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
