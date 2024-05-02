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

public class CustomerNotificationPanel extends AppCompatActivity {

    private LinearLayout notificationsLayout;
    private String customerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_notification_panel);

        customerEmail = getIntent().getStringExtra("customerEmail");
        notificationsLayout = findViewById(R.id.notificationsLayout);

        fetchNotifications();
    }

    private void fetchNotifications() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Notification")
                .whereEqualTo("CustomerEmail", customerEmail)
                .whereIn("Status", java.util.Arrays.asList("Pending", "Complete"))  // Checks if Status is either Pending or Complete
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String notificationText = document.getString("NotificationText");
                            String status = document.getString("Status");
                            String notificationType = document.getString("NotificationType");
                            String serviceId = document.getString("ServiceID");
                            String serviceDate = document.getString("ServiceDate");
                            String numberPlate = document.getString("NumberPlate");
                            String payment = document.getString("Payment");
                            String gatepassId = document.getString("GatepassID");
                            // Display the notification
                            displayNotification(notificationText, status, notificationType, serviceId, serviceDate, numberPlate, payment, gatepassId);
                        }
                    } else {
                        Log.e("CustomerNotificationPanel", "Error getting documents: ", task.getException());
                        Toast.makeText(this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayNotification(String text, String status, String notificationType, String serviceId, String serviceDate, String numberPlate, String payment, String gatepassId) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(20, 50, 20, 50);
        textView.setMinimumHeight(100);
        textView.setBackgroundResource(R.drawable.notification_background);
        textView.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20, 10, 20, 10);

        textView.setLayoutParams(layoutParams);

        textView.setOnClickListener(v -> {
            Intent intent;
            if ("payment".equals(notificationType) && "Pending".equals(status)) {
                intent = new Intent(CustomerNotificationPanel.this, MakePayment.class);
                intent.putExtra("NotificationText", text);
                intent.putExtra("ServiceID", serviceId);
                intent.putExtra("ServiceDate", serviceDate);
                intent.putExtra("NumberPlate", numberPlate);
                intent.putExtra("Payment", payment);
                intent.putExtra("CustomerEmail", customerEmail);
                startActivity(intent);
            } else if ("gatepass".equals(notificationType)) {
                intent = new Intent(CustomerNotificationPanel.this, ShowGatepass.class);
                intent.putExtra("GatepassID", gatepassId);
                intent.putExtra("ServiceDate", serviceDate);
                intent.putExtra("NumberPlate", numberPlate);
                startActivity(intent);
            }
        });

        notificationsLayout.addView(textView);
    }
}
