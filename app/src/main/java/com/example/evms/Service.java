package com.example.evms;

import com.google.firebase.Timestamp;

public class Service {
    private String DateOfCreation;
    private String ImageUrl;
    private String ServiceDescription;
    private String ServiceID;
    private String ServiceName;
    private String ServicePrice; // Stored as String
    private String ServiceRating; // Stored as String
    private String ServiceType;

    // Default constructor
    public Service() {
        // Required for Firestore data deserialization
    }

    // Getters
    public String getDateOfCreation() {
        return DateOfCreation;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public String getServiceDescription() {
        return ServiceDescription;
    }

    public String getServiceID() {
        return ServiceID;
    }

    public String getServiceName() {
        return ServiceName;
    }

    // Convert ServicePrice from String to double
//    public double getServicePrice() {
//        try {
//            return Double.parseDouble(ServicePrice);
//        } catch (NumberFormatException e) {
//            return 0.0; // Return default value or handle error
//        }
//    }
    public String getServicePrice() {
        return ServicePrice;
    }

    // Convert ServiceRating from String to double
    public double getServiceRating() {
        try {
            return Double.parseDouble(ServiceRating);
        } catch (NumberFormatException e) {
            return 0.0; // Return default value or handle error
        }
    }

    public String getServiceType() {
        return ServiceType;
    }

    // Setters, including String setters for price and rating if needed
    public void setServicePrice(String servicePrice) {
        this.ServicePrice = servicePrice;
    }

    public void setServiceRating(String serviceRating) {
        this.ServiceRating = serviceRating;
    }


    public void setDateOfCreation(String dateOfCreation) {
        DateOfCreation = dateOfCreation;
    }
}
