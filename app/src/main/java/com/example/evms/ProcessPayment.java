package com.example.evms;

import android.content.Context;

public class ProcessPayment {

    public static String processPayment(Context ignoredContext, String cardNumber, String cvc, String expMonth, String expYear) {
        // Validate card details first
        String validationError = validateCardDetails(cardNumber, cvc, expMonth, expYear);
        if (!validationError.isEmpty()) {
            return validationError;  // Return validation error message if any
        }

        // Simulate payment processing (always successful in this dummy example)
        return "";  // Return empty string on success
    }

    // Validate card details
    private static String validateCardDetails(String cardNumber, String cvc, String expMonth, String expYear) {
        if (cardNumber == null || cardNumber.length() != 16) {
            return "Invalid card number. Card number must be 16 digits.";
        }
        if (cvc == null || cvc.length() != 3) {
            return "Invalid CVC. CVC must be 3 digits.";
        }
        if (expMonth == null || Integer.parseInt(expMonth) < 1 || Integer.parseInt(expMonth) > 12) {
            return "Invalid expiration month. Enter a valid month (01-12).";
        }

        if (Integer.parseInt(expMonth) < 5 && Integer.parseInt(expYear) == 2024) {
            return "Invalid expiration month.";
        }

        if (expYear == null || Integer.parseInt(expYear) < 2024) { // Adjust year validation based on current year
            return "Invalid expiration year. Enter a valid year.";
        }
        return "";  // No validation errors
    }
}
