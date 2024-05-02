package com.example.evms;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class PendingService {

        private String CustomerEmail;
        private String MaintenanceDate;
        private String NumberPlate;
        private String ServiceID;

        // Default constructor required for Firestore
        public PendingService() {
        }

        public PendingService(String customerEmail, String maintenanceDate, String numberPlate, String serviceID) {
            CustomerEmail = customerEmail;
            MaintenanceDate = maintenanceDate;
            NumberPlate = numberPlate;
            ServiceID = serviceID;
        }

        // Getters and setters
        public String getCustomerEmail() {
            return CustomerEmail;
        }

        public void setCustomerEmail(String customerEmail) {
            CustomerEmail = customerEmail;
        }

        public String getMaintenanceDate() {
            return MaintenanceDate;
        }

        public void setMaintenanceDate(String maintenanceDate) {
            MaintenanceDate = maintenanceDate;
        }

        public String getNumberPlate() {
            return NumberPlate;
        }

        public void setNumberPlate(String numberPlate) {
            NumberPlate = numberPlate;
        }

        public String getServiceID() {
            return ServiceID;
        }

        public void setServiceID(String serviceID) {
            ServiceID = serviceID;
        }
        static public  PendingService filterReturnUpcoming(List<PendingService> PendingServiceList) {
            if (PendingServiceList == null || PendingServiceList.isEmpty()) {
                // Handle empty or null list
                return null;
            }
            // Get today's date
            Date today = new Date();

            // Initialize variables to store the closest service and its date difference
            PendingService closestService = null;
            long minDifference = Long.MAX_VALUE;

            // Define date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Iterate through the service list to find the closest service
            for (PendingService service : PendingServiceList) {
                try {
                    // Parse service date string to Date object
                    Date serviceDate = dateFormat.parse(service.getMaintenanceDate());

                    // Calculate the difference between service date and today's date
                    long difference = Math.abs(serviceDate.getTime() - today.getTime());

                    // Check if this service is closer to today than the previously found closest service
                    if (difference < minDifference) {
                        minDifference = difference;
                        closestService = service;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    // Handle parsing exception if needed
                }
            }

            // At this point, closestService will contain the service with the closest date to today
            return closestService;
        }
    public void getServiceDetails(FirebaseFirestore fb, OnServiceDetailsListener listener) {
        fb.collection("Service")
                .whereEqualTo("ServiceID", this.getServiceID()) // Assuming getServiceID() returns the ID of the service
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Check if any documents were returned
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get the first document
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                        // Convert the document snapshot to a Service object
                        String serviceName = documentSnapshot.getString("ServiceName");
                        String servicePrice = documentSnapshot.getString("ServicePrice");

                        if (servicePrice != null && serviceName != null) {
                            // Pass the service details to the listener
                            listener.onServiceDetails(serviceName, servicePrice);
                        } else {
                            // Handle case where service details are missing
                            listener.onFailure("Service details are missing");
                        }
                    } else {
                        // Handle case where no matching documents were found
                        listener.onFailure("No matching service found");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure appropriately
                    listener.onFailure(e.getMessage());
                });
    }
    public interface OnServiceDetailsListener {
        void onServiceDetails(String serviceName,String servicePrice );
        void onFailure(String errorMessage);
    }
}

