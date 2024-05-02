package com.example.evms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MakePayment extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showPaymentDialog(this);
    }

    private void showPaymentDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.activity_make_payment, null);

        Intent intentReceived = getIntent();
        String notificationText = intentReceived.getStringExtra("NotificationText");
        String serviceId = intentReceived.getStringExtra("ServiceID");
        String serviceDate = intentReceived.getStringExtra("ServiceDate");
        String numberPlate = intentReceived.getStringExtra("NumberPlate");
        String payment = intentReceived.getStringExtra("Payment");
        String customerEmail = intentReceived.getStringExtra("CustomerEmail");

        TextView notificationTextView = new TextView(context);
        notificationTextView.setText(notificationText);
        notificationTextView.setPadding(20, 10, 20, 10);
        notificationTextView.setTextSize(16);

        final EditText cardNumberField = layout.findViewById(R.id.cardNumber);
        final EditText cvcField = layout.findViewById(R.id.cvc);
        final EditText expiryMonthField = layout.findViewById(R.id.expiryMonth);
        final EditText expiryYearField = layout.findViewById(R.id.expiryYear);

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.addView(notificationTextView);
        container.addView(layout);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(container)
                .setTitle("Enter Card Details")
                .setPositiveButton("Pay", null)
                .setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.cancel())
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String cardNumber = cardNumberField.getText().toString();
            String cvc = cvcField.getText().toString();
            String expiryMonth = expiryMonthField.getText().toString();
            String expiryYear = expiryYearField.getText().toString();
            String result = ProcessPayment.processPayment(context, cardNumber, cvc, expiryMonth, expiryYear);
            if (result.isEmpty()) {
                Toast.makeText(context, "Payment Successful!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                updateNotificationStatus(serviceId, serviceDate, numberPlate, customerEmail);
                Intent feedbackIntent = new Intent(MakePayment.this, CustomerServiceFeedback.class);
                feedbackIntent.putExtras(intentReceived); // Pass all received data to the feedback activity
                startActivity(feedbackIntent);
            } else {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();  // Keep dialog open to show error message
            }
        });
    }

    private void updateNotificationStatus(String serviceId, String serviceDate, String numberPlate, String customerEmail) {
        db.collection("Notification")
                .whereEqualTo("ServiceID", serviceId)
                .whereEqualTo("ServiceDate", serviceDate)
                .whereEqualTo("NumberPlate", numberPlate)
                .whereEqualTo("CustomerEmail", customerEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        querySnapshot.getDocuments().forEach(documentSnapshot ->
                                documentSnapshot.getReference().update("Status", "Processing")
                        );
                    } else {
                        Toast.makeText(this, "Error updating notification status", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
