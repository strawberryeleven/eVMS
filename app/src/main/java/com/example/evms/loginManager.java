package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class loginManager extends AppCompatActivity {

    // Initialize Firebase Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText textManagerId;
    private EditText textManagerPassword;
    private Button btnManagerLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_manager);

        // Adjust insets for the layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve views by their IDs
        textManagerId = findViewById(R.id.text_managerID);
        textManagerPassword = findViewById(R.id.text_managerPassword);
        btnManagerLogin = findViewById(R.id.btn_managerLoggedIn);

        // Set on click listener for the login button
        btnManagerLogin.setOnClickListener(view -> attemptLogin());
    }

    private void attemptLogin() {
        // Retrieve manager ID and password from input fields
        String managerId = textManagerId.getText().toString();
        String managerPassword = textManagerPassword.getText().toString();

        // Check if the fields are empty
        if (managerId.isEmpty() || managerPassword.isEmpty()) {
            Toast.makeText(loginManager.this, "Manager ID and password cannot be empty.", Toast.LENGTH_LONG).show();
            return;
        }

        // Query Firestore to find a matching manager document
        db.collection("Manager")
                .whereEqualTo("ManagerID", managerId)
                .whereEqualTo("password", managerPassword) // Storing passwords in plaintext is not secure, consider using Firebase Authentication
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Assuming 'ManagerID' and 'password' fields are available in your Firestore documents
                                // Redirect to manager homepage
                                Intent intent = new Intent(loginManager.this, ManagerHomepage.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            // Invalid ManagerID or Password
                            Toast.makeText(loginManager.this, "Invalid ManagerID or Password.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(loginManager.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(loginManager.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
