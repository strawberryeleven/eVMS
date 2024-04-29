package com.example.evms;

import com.google.firebase.Timestamp;

public class Manager {
    private String EmployeeID;
    private String ManagerID;
    private String Salary;
    private String password;
    private Timestamp dateAppointed;

    // Constructor with parameters
    public Manager(String EmployeeID, String ManagerID, String Salary, String password, Timestamp dateAppointed) {
        this.EmployeeID = EmployeeID;
        this.ManagerID = ManagerID;
        this.Salary = Salary;
        this.password = password;
        this.dateAppointed = dateAppointed;
    }

    // Getters and Setters
    public String getEmployeeID() { return EmployeeID; }
    public void setEmployeeID(String EmployeeID) { this.EmployeeID = EmployeeID; }
    public String getManagerID() { return ManagerID; }
    public void setManagerID(String ManagerID) { this.ManagerID = ManagerID; }
    public String getSalary() { return Salary; }
    public void setSalary(String Salary) { this.Salary = Salary; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Timestamp getDateAppointed() { return dateAppointed; }
    public void setDateAppointed(Timestamp dateAppointed) { this.dateAppointed = dateAppointed; }

    // Empty constructor needed for Firestore
    public Manager() {
    }
}
