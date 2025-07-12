package com.example.evms;

import com.google.firebase.Timestamp;

public class Manager {
    private String oldEmployeeId;
    private String ManagerID;
    private String Salary;
    private String password;
    private Timestamp dateAppointed;

    // Constructor with parameters
    public Manager(String oldEmployeeId, String ManagerID, String Salary, String password, Timestamp dateAppointed) {
        this.oldEmployeeId = oldEmployeeId;
        this.ManagerID = ManagerID;
        this.Salary = Salary;
        this.password = password;
        this.dateAppointed = dateAppointed;
    }

    // Getters and Setters
    // Correctly named getter and setter for the oldEmployeeId
    public String getOldEmployeeId() {
        return oldEmployeeId;
    }

    public void setOldEmployeeId(String oldEmployeeId) {
        this.oldEmployeeId = oldEmployeeId;
    }
    public String getManagerID() { return ManagerID; }
    public void setManagerID(String ManagerID) { this.ManagerID = ManagerID; }
    public String getSalary() { return Salary; }
    public void setSalary(String Salary) { this.Salary = Salary; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Timestamp getDateAppointed() { return dateAppointed; }
    public void setDateAppointed(Timestamp dateAppointed) { this.dateAppointed = dateAppointed; }

    // Empty constructor needed for Firestore
    public Manager() {    }
}