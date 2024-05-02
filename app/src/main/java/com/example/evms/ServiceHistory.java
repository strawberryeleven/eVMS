package com.example.evms;

import com.google.firebase.Timestamp;

public class ServiceHistory {
    private String CustomerEmail;
    private String EmployeeID;
    private String EmployeeRemarks;
    private String FeedbackStatus;
    private String NumberPlate;
    private String ServiceDate;
    private String ServiceID;
    private double  ServiceRating; // Stored as Number in Database

    private String ServiceName;

    private String ServicePrice;

    // No-argument constructor
    public ServiceHistory() {
    }

    // All-argument constructor
    public ServiceHistory(String CustomerEmail, String EmployeeID, String EmployeeRemarks, String FeedbackStatus, String NumberPlate, String ServiceDate, String ServiceID, double  ServiceRating) {
        this.CustomerEmail = CustomerEmail;
        this.EmployeeID = EmployeeID;
        this.EmployeeRemarks = EmployeeRemarks;
        this.FeedbackStatus = FeedbackStatus;
        this.NumberPlate = NumberPlate;
        this.ServiceDate = ServiceDate;
        this.ServiceID = ServiceID;
        this.ServiceRating = ServiceRating;
    }
    private Service serviceDetails;  // Add this to store the linked service object

    // Constructor, getters, and setters
    public Service getServiceDetails() {
        return serviceDetails;
    }

    public void setServiceDetails(Service serviceDetails) {
        this.serviceDetails = serviceDetails;
    }
    public String getServiceName() { return  ServiceName;}

    public void setServiceName(String serviceName) {this.ServiceName=serviceName;}
    public String getServicePrice() {return ServicePrice;}

    public void setServicePrice(String servicePrice) {this.ServicePrice=servicePrice;}

    // Getters and setters
    public String getCustomerEmail() {
        return CustomerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        CustomerEmail = customerEmail;
    }

    public String getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(String employeeID) {
        EmployeeID = employeeID;
    }

    public String getEmployeeRemarks() {
        return EmployeeRemarks;
    }

    public void setEmployeeRemarks(String employeeRemarks) {
        EmployeeRemarks = employeeRemarks;
    }

    public String getFeedbackStatus() {
        return FeedbackStatus;
    }

    public void setFeedbackStatus(String feedbackStatus) {
        FeedbackStatus = feedbackStatus;
    }

    public String getNumberPlate() {
        return NumberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        NumberPlate = numberPlate;
    }

    public String getServiceDate() {
        return ServiceDate;
    }

    public void setServiceDate(String serviceDate) {
        ServiceDate = serviceDate;
    }

    public String getServiceID() {
        return ServiceID;
    }

    public void setServiceID(String serviceID) {
        ServiceID = serviceID;
    }

    public double  getServiceRating() {
        return ServiceRating;
    }

    public void setServiceRating(double  serviceRating) {
        ServiceRating = serviceRating;
    }

}
