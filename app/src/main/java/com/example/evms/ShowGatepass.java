package com.example.evms;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ShowGatepass extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String gatepassId, serviceDate, numberPlate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve data from intent
        Intent intent = getIntent();
        gatepassId = intent.getStringExtra("GatepassID");
        serviceDate = intent.getStringExtra("ServiceDate");
        numberPlate = intent.getStringExtra("NumberPlate");

        fetchGatepassDetails();
    }

    private void fetchGatepassDetails() {
        db.collection("Notification")
                .whereEqualTo("GatepassID", gatepassId)
                .whereEqualTo("ServiceDate", serviceDate)
                .whereEqualTo("NumberPlate", numberPlate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        showGatepassDialog(document);
                    } else {
                        Toast.makeText(this, "Gatepass details not found.", Toast.LENGTH_SHORT).show();
                        finish(); // Finish activity if no details are found.
                    }
                });
    }

    private void showGatepassDialog(DocumentSnapshot document) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Gatepass");
        String message = "Customer Email: " + document.getString("CustomerEmail") + "\n" +
                "Service ID: " + document.getString("ServiceID") + "\n" +
                "Number Plate: " + document.getString("NumberPlate") + "\n" +
                "Service Date: " + document.getString("ServiceDate") + "\n" +
                "Gatepass key: " + document.getString("GatepassID") + "\n";

        builder.setMessage(message);
        builder.setPositiveButton("Delete", (dialog, which) -> deleteNotification(document.getId()));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deleteNotification(String documentId) {
        db.collection("Notification").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Gatepass deleted successfully.", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after successful deletion
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete gatepass.", Toast.LENGTH_SHORT).show());
    }
}
