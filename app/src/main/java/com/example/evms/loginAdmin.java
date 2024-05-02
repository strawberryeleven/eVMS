package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.text.InputType;

public class loginAdmin extends AppCompatActivity {

    private EditText adminIdEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private CheckBox showPasswordCheckBox;
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_admin);
        setupInsets();

        adminIdEditText = findViewById(R.id.text_adminID);
        passwordEditText = findViewById(R.id.text_adminPassword);
        loginButton = findViewById(R.id.btn_adminLoggedIn);
        showPasswordCheckBox = findViewById(R.id.checkBox_showPassword);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogin();
            }
        });

        showPasswordCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showPasswordCheckBox.isChecked()) {
                    // To show the password
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordEditText.setSelection(passwordEditText.getText().length());
                } else {
                    // To hide the password
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordEditText.setSelection(passwordEditText.getText().length());
                }
            }
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

    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void performLogin() {
        String adminId = adminIdEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (adminId.equals("admin1") && password.equals("wyne123")) {
            Toast.makeText(loginAdmin.this, "Login Successful", Toast.LENGTH_SHORT).show();
            // Intent to navigate to admin home page
            Intent intent = new Intent(loginAdmin.this, adminHomepage.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(loginAdmin.this, "Invalid ID or Password", Toast.LENGTH_LONG).show();
        }
    }
}