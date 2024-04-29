package com.example.evms;

public class Employee {
    private String EmployeeID;
    private String ManagedBy; // This can be null or empty if the employee is not a manager.
    private String Name;
    private String email;
    private String password; // Be careful with storing passwords, ideally, passwords shouldn't be retrieved.
    private String phoneNumber;
    private String salary;

    public Employee() {  }

    // Constructor, getters, and setters.
    public Employee(String EmployeeID, String ManagedBy, String Name, String email, String password, String phoneNumber, String salary) {
        this.EmployeeID = EmployeeID;
        this.ManagedBy = ManagedBy;
        this.Name = Name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.salary = salary;
    }

    // Getters
    public String getEmployeeID() { return EmployeeID; }
    public String getManagedBy() { return ManagedBy; }
    public String getName() { return Name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getSalary() { return salary; }

    // Setters - if you need to update the fields
    // ...
}
