package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

public class loginEmployee extends AppCompatActivity {

    private EditText textEmployeeId;
    private EditText textEmployeePassword;
    private CheckBox showPasswordCheckBox;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button backButton;
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

        textEmployeeId = findViewById(R.id.text_employeeID);
        textEmployeePassword = findViewById(R.id.text_employeePassword);
        showPasswordCheckBox = findViewById(R.id.checkBox_showPassword);
        Button btnEmployeeLogin = findViewById(R.id.btn_employeeLoggedIn);

        btnEmployeeLogin.setOnClickListener(v -> attemptEmployeeLogin());

        // Set up the checkbox to toggle password visibility
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                textEmployeePassword.setInputType(145); // InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                textEmployeePassword.setInputType(129); // InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            textEmployeePassword.setSelection(textEmployeePassword.getText().length());
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

    private void attemptEmployeeLogin() {
        String employeeId = textEmployeeId.getText().toString().trim();
        String employeePassword = textEmployeePassword.getText().toString().trim();

        if (employeeId.isEmpty() || employeePassword.isEmpty()) {
            Toast.makeText(this, "Employee ID and password cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Employees")
                .whereEqualTo("EmployeeID", employeeId)
                .whereEqualTo("password", employeePassword)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        Intent intent = new Intent(loginEmployee.this, EmployeeHomepage.class);
                        Objects.requireNonNull(document.getData()).forEach((key, value) -> {
                            intent.putExtra(key, value.toString());  // Assumes all values are suitable for passing as Strings
                        });
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Invalid EmployeeID or Password.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(this, "Error logging in. Please try again.", Toast.LENGTH_SHORT).show());
    }
}