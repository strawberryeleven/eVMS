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

public class employeeAddNewVehicle extends AppCompatActivity {

    private static final int SCAN_REQUEST_CODE = 1; // Define a request code for your scan activity

    private EditText customerEmail, vehicleModel, kmsDriven, vehicleColor;
    private TextView numberPlate;
    private Button scanNumberPlateButton, addVehicleButton;

    private FirebaseFirestore db;

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
    private void addVehicleToFirestore() {
        String email = customerEmail.getText().toString();
        String model = vehicleModel.getText().toString();
        String kms = kmsDriven.getText().toString();
        String color = vehicleColor.getText().toString();
        String plate = numberPlate.getText().toString();

        if (!email.isEmpty() && !model.isEmpty() && !kms.isEmpty() && !color.isEmpty() && !plate.isEmpty()) {
            Vehicle vehicle = new Vehicle(plate, model, kms, color, email);  // Pass email to the constructor
            db.collection("Vehicles")
                    .add(vehicle)
                    .addOnSuccessListener(documentReference -> Toast.makeText(this, "Vehicle added successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to add vehicle", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

}
