package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateGatepass extends AppCompatActivity {

    private LinearLayout notificationsLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gatepass);
        notificationsLayout = findViewById(R.id.notificationsLayout);

        fetchGatepassNotifications();
    }

    private void fetchGatepassNotifications() {
        db.collection("Notification")
                .whereEqualTo("Status", "Processing")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String customerEmail = document.getString("CustomerEmail");
                            String serviceId = document.getString("ServiceID");
                            String text = document.getString("NotificationText"); // This will be replaced
                            displayGatepassNotification(customerEmail, serviceId, document.getId());
                        }
                    } else {
                        Log.e("CreateGatepass", "Error getting documents: ", task.getException());
                        Toast.makeText(this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayGatepassNotification(String customerEmail, String serviceId, String documentId) {
        TextView textView = new TextView(this);
        String displayText = "Email: " + customerEmail + "\nService ID: " + serviceId;
        textView.setText(displayText);
        textView.setPadding(20, 50, 20, 50);
        textView.setMinimumHeight(100);
        textView.setBackgroundResource(R.drawable.notification_background);
        textView.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20, 10, 20, 10);

        textView.setLayoutParams(layoutParams);

        textView.setOnClickListener(v -> updateNotificationAsGatepass(documentId));

        notificationsLayout.addView(textView);
    }

    private void updateNotificationAsGatepass(String documentId) {
        String gatepassId = UUID.randomUUID().toString();
        Map<String, Object> updates = new HashMap<>();
        updates.put("NotificationType", "gatepass");
        updates.put("GatepassID", gatepassId);
        updates.put("NotificationText", "Gatepass Key: " + gatepassId);
        updates.put("Status", "Complete");

        db.collection("Notification").document(documentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Gatepass created with ID: " + gatepassId, Toast.LENGTH_SHORT).show();
                    refreshGatepassNotifications(); // Optionally refresh the list to reflect changes
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to create gatepass", Toast.LENGTH_SHORT).show());
    }

    private void refreshGatepassNotifications() {
        notificationsLayout.removeAllViews();
        fetchGatepassNotifications();
    }
}
