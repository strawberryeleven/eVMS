package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class CustomerServiceFeedback extends AppCompatActivity {

    private EditText etCustomerFeedback, etServiceRating;
    private Button btnSubmitFeedback;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String serviceId, serviceDate, numberPlate, customerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service_feedback);

        etCustomerFeedback = findViewById(R.id.etCustomerFeedback);
        etServiceRating = findViewById(R.id.etServiceRating);
        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback);

        // Retrieve data from intent
        Intent intent = getIntent();
        serviceId = intent.getStringExtra("ServiceID");
        serviceDate = intent.getStringExtra("ServiceDate");
        numberPlate = intent.getStringExtra("NumberPlate");
        customerEmail = intent.getStringExtra("CustomerEmail");

        btnSubmitFeedback.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        String feedback = etCustomerFeedback.getText().toString().trim();
        String rating = etServiceRating.getText().toString().trim();

        if (feedback.isEmpty() || rating.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        updateServiceHistory(serviceId, serviceDate, numberPlate, rating);
        saveFeedback(customerEmail, feedback, serviceId, serviceDate, numberPlate);

        Toast.makeText(this, "Feedback submitted! Thanks for your response.", Toast.LENGTH_LONG).show();
        finish(); // Close activity after submission
        onBackPressed();
        onBackPressed();
    }

    private void updateServiceHistory(String serviceId, String serviceDate, String numberPlate, String rating) {
        db.collection("ServiceHistory")
                .whereEqualTo("ServiceID", serviceId)
                .whereEqualTo("ServiceDate", serviceDate)
                .whereEqualTo("NumberPlate", numberPlate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        task.getResult().getDocuments().get(0).getReference()
                                .update("ServiceRating", Double.parseDouble(rating))
                                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Service rating updated successfully!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update service rating", Toast.LENGTH_SHORT).show());
                    }
                });
    }

    private void saveFeedback(String customerEmail, String feedback, String serviceId, String serviceDate, String numberPlate) {
        Map<String, Object> feedbackDocument = new HashMap<>();
        feedbackDocument.put("CustomerEmail", customerEmail);
        feedbackDocument.put("Feedback", feedback);
        feedbackDocument.put("ServiceID", serviceId);
        feedbackDocument.put("ServiceDate", serviceDate);
        feedbackDocument.put("NumberPlate", numberPlate);

        db.collection("ServiceFeedback")
                .add(feedbackDocument)
                .addOnSuccessListener(documentReference -> Toast.makeText(this, "Feedback saved successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save feedback", Toast.LENGTH_SHORT).show());
    }
}
