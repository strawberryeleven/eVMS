package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class loginEmployee extends AppCompatActivity {

    private EditText textEmployeeId;
    private EditText textEmployeePassword;
    private Button btnEmployeeLogin;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_employee);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize components
        textEmployeeId = findViewById(R.id.text_employeeID);
        textEmployeePassword = findViewById(R.id.text_employeePassword);
        btnEmployeeLogin = findViewById(R.id.btn_employeeLoggedIn);

        // Set up click listener for the login button
        btnEmployeeLogin.setOnClickListener(v -> attemptEmployeeLogin());
    }

    private void attemptEmployeeLogin() {
        // Get input text
        String employeeId = textEmployeeId.getText().toString().trim();
        String employeePassword = textEmployeePassword.getText().toString().trim();

        // Simple validation
        if (employeeId.isEmpty() || employeePassword.isEmpty()) {
            Toast.makeText(this, "Employee ID and password cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query the Employees collection
        db.collection("Employees")
                .whereEqualTo("EmployeeID", employeeId)
                .whereEqualTo("password", employeePassword) // Note: Storing passwords in plain text is insecure
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (!task.getResult().isEmpty()) {
                            // Login successful, redirect to the Employee Homepage
                            Intent intent = new Intent(loginEmployee.this, EmployeeHomepage.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Login failed, invalid credentials
                            Toast.makeText(this, "Invalid EmployeeID or Password.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Error with Firestore operation
                        Toast.makeText(this, "Error logging in. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
