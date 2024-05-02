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
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String notificationText = document.getString("Notification");
                            displayNotification(notificationText);
                        }
                    } else {
                        // Log the error or show a message
                        Log.e("CustomerNotificationPanel", "Error getting documents: ", task.getException());
                        Toast.makeText(this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayNotification(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(20, 50, 20, 50);
        textView.setMinimumHeight(100);
        textView.setBackgroundResource(R.drawable.notification_background);
        textView.setGravity(android.view.Gravity.CENTER_VERTICAL);

        // Setting layout parameters with margins
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20, 10, 20, 10);  // Left, top, right, bottom margins

        textView.setLayoutParams(layoutParams);

        // Adding click listener to this textView
        textView.setOnClickListener(v -> {
            // Intent to start MakePayment activity
            Intent intent = new Intent(CustomerNotificationPanel.this, MakePayment.class);
            intent.putExtra("NotificationText", text);
            startActivity(intent);
        });

        notificationsLayout.addView(textView); // Add to the layout
    }


}
