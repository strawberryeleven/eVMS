package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class customerHomepage extends AppCompatActivity {

    private String customerEmail;

    public String getValidEmail(){
        return customerEmail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_homepage);

        // Retrieve the email passed from loginCustomer
        Intent intent = getIntent();
        customerEmail = intent.getStringExtra("customerEmail");
        Toast.makeText(this, "Logged in as: " + customerEmail, Toast.LENGTH_LONG).show();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button searchButton = findViewById(R.id.button3);

        Button testButton = findViewById(R.id.btn_testEmail);

        displayPendingService();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start customerSearchService activity
                Intent intent = new Intent(customerHomepage.this, customerSearchService.class);
                intent.putExtra("customerEmail", customerEmail); // Pass email to customerHomepage
                startActivity(intent);
                finish();
            }
        });

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start customerSearchService activity
                Intent intent = new Intent(customerHomepage.this, testEmailPage.class);
                intent.putExtra("customerEmail", customerEmail); // Pass email to customerHomepage
                startActivity(intent);
                finish();
            }
        });


    }

    private void displayPendingService() {
        DatabaseReference pendingRef = FirebaseDatabase.getInstance().getReference("PendingService");
        Query latestServiceQuery = pendingRef.orderByChild("CustomerEmail").equalTo(customerEmail);

        latestServiceQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next(); // Assuming only one latest service
                    String ServiceId = snapshot.child("ServiceId").getValue(String.class);
                    String maintenanceDate = snapshot.child("MaintenanceDate").getValue(String.class);
                    String numberPlate = snapshot.child("NumberPlate").getValue(String.class);

                    DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference("Service").child(ServiceId);
                    serviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot serviceSnapshot) {
                            if (serviceSnapshot.exists()) {
                                String serviceName = serviceSnapshot.child("ServiceName").getValue(String.class);
                                String servicePrice = serviceSnapshot.child("ServicePrice").getValue(String.class);

                                ((TextView) findViewById(R.id.tvServiceName)).setText(serviceName);
                                ((TextView) findViewById(R.id.tvServicePrice)).setText(String.format("Price: %s", servicePrice));
                                ((TextView) findViewById(R.id.tvMaintenanceDate)).setText(String.format("Date: %s", maintenanceDate));
                                ((TextView) findViewById(R.id.tvNumberPlate)).setText(String.format("Plate: %s", numberPlate));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(customerHomepage.this, "Failed to fetch service details", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    ((TextView) findViewById(R.id.tvServiceName)).setText("No pending services found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(customerHomepage.this, "Failed to load pending services", Toast.LENGTH_SHORT).show();
            }
        });
    }


}