package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManagerHomepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_homepage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button addButton = findViewById(R.id.btn_AddingEmployee);
        Button removeEmployeeButton = findViewById(R.id.buttonRemoveEmployee);
        Button reportsButton = findViewById(R.id.buttonReports); // Find the Reports button

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerHomepage.this, managerAddEmployee.class);
            startActivity(intent);
        });

        removeEmployeeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerHomepage.this, managerRemoveEmployee.class);
            startActivity(intent);
        });

        reportsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerHomepage.this, managerReports.class); // Start the managerReports activity
            startActivity(intent);
        });
    }
}