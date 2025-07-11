package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class employeeAddNewVehicle extends AppCompatActivity {

    private static final int SCAN_REQUEST_CODE = 1; // Define a request code for your scan activity

    private EditText customerEmail, vehicleModel, kmsDriven, vehicleColor;
    private TextView numberPlate;
    private Button scanNumberPlateButton, addVehicleButton;

    private FirebaseFirestore db;
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_add_new_vehicle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Linking the UI components with the XML
        customerEmail = findViewById(R.id.customerEmail);
        vehicleModel = findViewById(R.id.vehicleModel);
        kmsDriven = findViewById(R.id.kmsDriven);
        vehicleColor = findViewById(R.id.vehicleColor);
        numberPlate = findViewById(R.id.displayNumberPlate);
        scanNumberPlateButton = findViewById(R.id.scanNumberPlateButton);
        addVehicleButton = findViewById(R.id.addVehicleButton);

        // Setting up the button to start the scan activity
        scanNumberPlateButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, employeeScanNumberPlate.class);
            startActivityForResult(intent, SCAN_REQUEST_CODE);
        });

        // Setting up the button to add the vehicle to Firestore
        addVehicleButton.setOnClickListener(v -> {
            addVehicleToFirestore();
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

    // Handling the result returned from the scan activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                String scannedNumberPlate = data.getStringExtra("numberPlate");
                if (scannedNumberPlate != null && !scannedNumberPlate.isEmpty()) {
                    numberPlate.setText(scannedNumberPlate);
                    Toast.makeText(this, "Number plate scanned successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No number plate found", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Scanning was canceled or failed", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Method to add the vehicle data to Firestore
    // Method to add the vehicle data to Firestore
    private void addVehicleToFirestore() {
        String customerEmailText = customerEmail.getText().toString();
        String modelText = vehicleModel.getText().toString();
        String kmsDrivenText = kmsDriven.getText().toString();
        String colorText = vehicleColor.getText().toString();
        String numberPlateText = numberPlate.getText().toString();

        // Customer Email validation: should follow proper email syntax
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(customerEmailText).matches()) {
            Toast.makeText(getApplicationContext(), "Enter a valid email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vehicle Model validation: should not contain numbers
        if (!modelText.matches("[a-zA-Z ]+")) {
            Toast.makeText(getApplicationContext(), "Vehicle model must not include numbers.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kilometers Driven validation: should be a numeric value
        try {
            double kms = Double.parseDouble(kmsDrivenText);
            if (kms <= 0) {
                Toast.makeText(getApplicationContext(), "Kilometers driven cannot be negative.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "Invalid format for kilometers driven.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vehicle Color validation: should not contain numbers
        if (!colorText.matches("[a-zA-Z ]+")) {
            Toast.makeText(getApplicationContext(), "Vehicle color must not include numbers.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!customerEmailText.isEmpty() && !modelText.isEmpty() && !kmsDrivenText.isEmpty() && !colorText.isEmpty() && !numberPlateText.isEmpty()) {
            // Using HashMap to ensure proper field names
            Map<String, Object> vehicle = new HashMap<>();
            vehicle.put("NumberPlate", numberPlateText);
            vehicle.put("Model", modelText);
            vehicle.put("KmsDriven", kmsDrivenText);
            vehicle.put("Color", colorText);
            vehicle.put("CustomerEmail", customerEmailText);

            db.collection("Vehicles")
                    .add(vehicle)
                    .addOnSuccessListener(documentReference -> Toast.makeText(this, "Vehicle added successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to add vehicle", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }


}