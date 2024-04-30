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

public class adminHomepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Ensure you have imported EdgeToEdge correctly
        setContentView(R.layout.activity_admin_homepage);

        // Set padding based on system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the "Add Services" button by its ID
        Button addServicesButton = findViewById(R.id.buttonAddServices);
        Button appointManagerButton = findViewById(R.id.button2);

        addServicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the AddServicesActivity
                Intent intent = new Intent(adminHomepage.this, adminAddService.class);

                // Start the AddServicesActivity
                startActivity(intent);
            }
        });

        appointManagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the AddServicesActivity
                Intent intent = new Intent(adminHomepage.this, adminAppointManager.class);

                // Start the AddServicesActivity
                startActivity(intent);
            }
        });
    }
}
