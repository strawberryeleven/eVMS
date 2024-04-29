package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class loginCustomer extends AppCompatActivity {

    EditText emailField, passwordField;
    Button loginButton;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_customer);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Link UI elements
        emailField = findViewById(R.id.editTextCustomerEmailAddress);
        passwordField = findViewById(R.id.editTextCustomerPassword);
        loginButton = findViewById(R.id.btn_customer_LoggedIn);

        loginButton.setOnClickListener(view -> attemptLogin());
    }

    private void attemptLogin() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Customers")
                .whereEqualTo("Email", email)
                .whereEqualTo("Password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Authentication successful
                        Toast.makeText(loginCustomer.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(loginCustomer.this, customerHomepage.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Authentication failed
                        Toast.makeText(loginCustomer.this, "Invalid Email/Password", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
