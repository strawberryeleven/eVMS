package com.example.evms;

public class Customer {
    private String CustomerID;
    private String Name;
    private String Email;
    private String Password;
    private String phoneNumber;

    // Constructor
    public Customer(String CustomerID, String Name, String Email, String Password, String phoneNumber) {
        this.CustomerID = CustomerID;
        this.Name = Name;
        this.Email = Email;
        this.Password = Password;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String CustomerID) {
        this.CustomerID = CustomerID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
