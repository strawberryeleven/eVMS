package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class EmployeeHomepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_homepage);

        Button btnUpdateCustomerRecords = findViewById(R.id.btnUpdateCustomerRecords);
        Button btnUpdateServiceRecords = findViewById(R.id.btnUpdateServiceRecords);
        Button btnGenerateGatePass = findViewById(R.id.btnGenerateGatePass);

        // Pass all intent extras received to other activities
        Bundle extras = getIntent().getExtras();

        btnUpdateCustomerRecords.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeHomepage.this, UpdateCustomerRecords.class);
            if (extras != null) {
                intent.putExtras(extras);  // Pass all received extras to UpdateCustomerRecords
            }
            startActivity(intent);
        });

        btnUpdateServiceRecords.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeHomepage.this, UpdateServiceRecords.class);
            if (extras != null) {
                intent.putExtras(extras);  // Pass all received extras to UpdateServiceRecords
            }
            startActivity(intent);
        });

        btnGenerateGatePass.setOnClickListener(v -> {
            // TODO: Implement the logic to generate a gate pass
        });
    }
}
