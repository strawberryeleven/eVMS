package com.example.evms;

public class Vehicle {
    private String NumberPlate;
    private String Model;
    private String KmsDriven;
    private String Color;

    public Vehicle() {
        // Default constructor required for calls to DataSnapshot.getValue(Vehicle.class)
    }

    public Vehicle(String NumberPlate, String Model, String KmsDriven, String Color) {
        this.NumberPlate = NumberPlate;
        this.Model = Model;
        this.KmsDriven = KmsDriven;
        this.Color = Color;
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
