package com.example.evms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class ShowGatepass extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        StringBuilder message = new StringBuilder();
        message.append("Customer Email: ").append(document.getString("CustomerEmail")).append("\n")
                .append("Service ID: ").append(document.getString("ServiceID")).append("\n")
                .append("Number Plate: ").append(document.getString("NumberPlate")).append("\n")
                .append("Service Date: ").append(document.getString("ServiceDate")).append("\n")
                .append("Gatepass ID: ").append(document.getString("GatepassID")).append("\n")
                .append("").append(document.getString("NotificationText"));

        builder.setMessage(message.toString());
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNotification(document.getId());
            }
        });
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
