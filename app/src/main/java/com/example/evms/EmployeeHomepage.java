package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EmployeeHomepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_homepage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnUpdateCustomerRecords = findViewById(R.id.btnUpdateCustomerRecords);
        Button btnUpdateServiceRecords = findViewById(R.id.btnUpdateServiceRecords);
        Button btnGenerateGatePass = findViewById(R.id.btnGenerateGatePass);


        // Assuming you have a button with id "scanButton" in your activity_employee_homepage layout
        findViewById(R.id.scanButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the employeeScanNumberPlate activity
                startActivity(new Intent(EmployeeHomepage.this, employeeScanNumberPlate.class));
            }
        });

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
