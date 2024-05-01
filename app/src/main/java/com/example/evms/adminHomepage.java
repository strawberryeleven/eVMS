package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class adminHomepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_homepage);

        Button addServicesButton = findViewById(R.id.buttonAddServices);
        Button appointManagerButton = findViewById(R.id.button2);
        Button removeManagerButton = findViewById(R.id.buttonRemoveManager);
        Button logoutButton = findViewById(R.id.buttonLogout);

        addServicesButton.setOnClickListener(v -> startActivity(new Intent(adminHomepage.this, adminAddService.class)));
        appointManagerButton.setOnClickListener(v -> startActivity(new Intent(adminHomepage.this, adminAppointManager.class)));
        removeManagerButton.setOnClickListener(v -> startActivity(new Intent(adminHomepage.this, adminRemoveManager.class)));
        logoutButton.setOnClickListener(v -> finish());  // Assuming you simply want to close the activity on logout
    }
}
