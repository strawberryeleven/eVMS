package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class loginCustomer extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginButton;
    private CheckBox showPasswordCheckBox;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_customer);

        db = FirebaseFirestore.getInstance();

        emailField = findViewById(R.id.editTextCustomerEmailAddress);
        passwordField = findViewById(R.id.editTextCustomerPassword);
        loginButton = findViewById(R.id.btn_customer_LoggedIn);
        showPasswordCheckBox = findViewById(R.id.checkBox_showPasswordCustomer);

        loginButton.setOnClickListener(view -> attemptLogin());

        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordField.setInputType(145); // InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                passwordField.setInputType(129); // InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            passwordField.setSelection(passwordField.getText().length());
        });
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
                        Toast.makeText(loginCustomer.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(loginCustomer.this, customerHomepage.class);
                        intent.putExtra("customerEmail", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(loginCustomer.this, "Invalid Email/Password", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}