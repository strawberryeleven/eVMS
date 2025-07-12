package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class launchPage extends AppCompatActivity {

    Button btnAdmin, btnManager, btnEmployee, btnCustomer;
    TextView txtRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_page);

        // Initialize buttons and text view
        btnAdmin = findViewById(R.id.btn_admin);
        btnManager = findViewById(R.id.btn_manager);
        btnEmployee = findViewById(R.id.btn_employee);
        btnCustomer = findViewById(R.id.btn_customer);
        txtRegister = findViewById(R.id.btn_reg_customer);

        // Set click listeners for each button
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Admin login page
                Intent intent = new Intent(launchPage.this, loginAdmin.class);
                startActivity(intent);
            }
        });

        btnManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Manager login page
                Intent intent = new Intent(launchPage.this, loginManager.class);
                startActivity(intent);
            }
        });

        btnEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Employee login page
                Intent intent = new Intent(launchPage.this, loginEmployee.class);
                startActivity(intent);
            }
        });

        btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Customer login page
                Intent intent = new Intent(launchPage.this, loginCustomer.class);
                startActivity(intent);
            }
        });

        // Set click listener for the text view
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Customer registration page
                Intent intent = new Intent(launchPage.this, registerCustomer.class);
                startActivity(intent);
            }
        });
    }
}