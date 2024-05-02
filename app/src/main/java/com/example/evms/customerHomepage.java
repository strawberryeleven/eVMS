package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class customerHomepage extends AppCompatActivity {

    private String customerEmail;

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
        TextView notificationCount = findViewById(R.id.notificationCount);

        ImageView notificationIcon = findViewById(R.id.notificationIcon);
        notificationIcon.setOnClickListener(v -> {
            Intent notificationIntent = new Intent(customerHomepage.this, CustomerNotificationPanel.class);  // Renamed the variable here
            notificationIntent.putExtra("customerEmail", customerEmail);  // Pass the customerEmail to the NotificationPanel activity
            startActivity(notificationIntent);
        });
        searchButton.setOnClickListener(v -> {
            startActivity(new Intent(customerHomepage.this, customerSearchService.class));
        });

        testButton.setOnClickListener(v -> {
            Intent emailIntent = new Intent(customerHomepage.this, testEmailPage.class);
            emailIntent.putExtra("customerEmail", customerEmail);
            startActivity(emailIntent);
            finish();
        });

        fetchNotificationCount();
    }

    private void fetchNotificationCount() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Notification") // Ensure the collection name is correct
                .whereEqualTo("CustomerEmail", customerEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        final int count = task.getResult().size(); // Count the documents which match the query
                        runOnUiThread(() -> {
                            updateNotificationBadge(count); // Update the notification badge
                            Toast.makeText(customerHomepage.this, "You have " + count + " notifications", Toast.LENGTH_LONG).show(); // Show the count in a toast
                        });
                    } else {
                        Log.d("FetchNotifError", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void updateNotificationBadge(int count) {
        TextView notificationCount = findViewById(R.id.notificationCount);
        if (count > 0) {
            notificationCount.setText(String.valueOf(count));
            notificationCount.setVisibility(View.VISIBLE);
        } else {
            notificationCount.setVisibility(View.GONE);
        }
    }

}
