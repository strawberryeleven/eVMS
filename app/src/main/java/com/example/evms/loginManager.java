package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class loginManager extends AppCompatActivity {

    private EditText managerIdField, passwordField;
    private Button loginButton;
    private CheckBox showPasswordCheckBox;
    private FirebaseFirestore db;
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_manager);

        db = FirebaseFirestore.getInstance();

        managerIdField = findViewById(R.id.text_managerID);
        passwordField = findViewById(R.id.text_managerPassword);
        loginButton = findViewById(R.id.btn_managerLoggedIn);
        showPasswordCheckBox = findViewById(R.id.checkBox_showPassword);

        loginButton.setOnClickListener(view -> attemptLogin());

        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordField.setInputType(145); // InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                passwordField.setInputType(129); // InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            passwordField.setSelection(passwordField.getText().length());
        });

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

    private void attemptLogin() {
        String managerId = managerIdField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (managerId.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        db.collection("Manager")
                .whereEqualTo("ManagerID", managerId)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        Intent intent = new Intent(loginManager.this, ManagerHomepage.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(loginManager.this, "Invalid Manager ID or Password", Toast.LENGTH_LONG).show();
                    }
                });
    }
}