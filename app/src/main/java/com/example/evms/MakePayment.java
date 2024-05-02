package com.example.evms;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MakePayment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showPaymentDialog(this);
    }

    private void showPaymentDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.activity_make_payment, null);

        String notificationText = getIntent().getStringExtra("NotificationText");

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
                .setPositiveButton("Pay", null)  // Listener is null here
                .setNegativeButton("Cancel", (dialog1, which) -> dialog1.cancel())
                .create();

        dialog.show();

        // Now setting the positive button click listener manually after showing the dialog
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String cardNumber = cardNumberField.getText().toString();
            String cvc = cvcField.getText().toString();
            String expiryMonth = expiryMonthField.getText().toString();
            String expiryYear = expiryYearField.getText().toString();
            String result = ProcessPayment.processPayment(context, cardNumber, cvc, expiryMonth, expiryYear);
            if (result.isEmpty()) {
                Toast.makeText(context, "Payment Sent!", Toast.LENGTH_LONG).show();
                dialog.dismiss(); // Only dismiss dialog if payment is successful
                finish();  // End activity after successful payment
            } else {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();  // Show error message
            }
        });
    }
}
