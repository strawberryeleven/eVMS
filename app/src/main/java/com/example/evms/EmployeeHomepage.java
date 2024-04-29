package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.evms.R;

public class EmployeeHomepage extends AppCompatActivity {

    private Button btnUpdateCustomerRecords;
    private Button btnUpdateServiceRecords;
    private Button btnGenerateGatePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_homepage);

        btnUpdateCustomerRecords = findViewById(R.id.btnUpdateCustomerRecords);
        btnUpdateServiceRecords = findViewById(R.id.btnUpdateServiceRecords);
        btnGenerateGatePass = findViewById(R.id.btnGenerateGatePass);

        // Set up the click listener for Update Customer Records button
        btnUpdateCustomerRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployeeHomepage.this, UpdateCustomerRecords.class);
                startActivity(intent);
            }
        });


        // Set up the click listener for Update Service Records button
        btnUpdateServiceRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Start the Update Service Records Activity or Fragment
            }
        });

        // Set up the click listener for Generate Gate Pass button
        btnGenerateGatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement the logic to generate a gate pass
            }
        });
    }
}
