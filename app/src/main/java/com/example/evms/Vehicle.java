package com.example.evms;
public class Vehicle {
    private String NumberPlate;
    private String Model;
    private String KmsDriven;
    private String Color;
    private String CustomerEmail;

    public Vehicle() {
        // Default constructor required for calls to DataSnapshot.getValue(Vehicle.class)
    }

    public Vehicle(String NumberPlate, String Model, String KmsDriven, String Color, String CustomerEmail) {
        this.NumberPlate = NumberPlate;
        this.Model = Model;
        this.KmsDriven = KmsDriven;
        this.Color = Color;
        this.CustomerEmail = CustomerEmail;
    }

    public String getCustomerEmail() {
        return CustomerEmail;
    }

    public void setCustomerEmail(String CustomerEmail) {
        this.CustomerEmail = CustomerEmail;
    }

    public String getNumberPlate() {
        return NumberPlate;
    }

    public String getModel() {
        return Model;
    }

    public String getKmsDriven() {
        return KmsDriven;
    }

    public String getColor() {
        return Color;
    }
}
