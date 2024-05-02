package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class adminReports extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_reports);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // OnClickListener for Service Report button
        findViewById(R.id.button4).setOnClickListener(v -> startActivity(new Intent(adminReports.this, adminServiceReport.class)));

        // OnClickListener for Employee Report button
        findViewById(R.id.button5).setOnClickListener(v -> startActivity(new Intent(adminReports.this, adminEmployeeReport.class)));

        // OnClickListener for Sales Report button
        findViewById(R.id.button6).setOnClickListener(v -> startActivity(new Intent(adminReports.this, adminSalesReport.class)));

        // OnClickListener for Manager Report button
        findViewById(R.id.button7).setOnClickListener(v -> startActivity(new Intent(adminReports.this, adminManagerReport.class)));
    }
}