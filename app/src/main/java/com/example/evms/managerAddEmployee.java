package com.example.evms;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class managerAddEmployee extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText editTextName, editTextEmail, editTextPhone, editTextSalary, editTextManagerId;
    private Button btnAddEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager_add_employee);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        editTextName = findViewById(R.id.editTextTextPersonName);
        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextSalary = findViewById(R.id.editTextNumber);
        editTextManagerId = findViewById(R.id.editTextManagerId);
        btnAddEmployee = findViewById(R.id.btn_AddEmployee);

        btnAddEmployee.setOnClickListener(v -> showConfirmationDialog());
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Addition")
                .setMessage("Are you sure you want to add this employee?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        validateManagerIdAndAddEmployee();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // If 'No' is clicked, just close the dialog
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void validateManagerIdAndAddEmployee() {
        String managerId = editTextManagerId.getText().toString().trim();
        // Check if Manager ID is valid
        db.collection("Manager")
                .whereEqualTo("ManagerID", managerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        generateEmployeeIdAndAdd();
                    } else {
                        Toast.makeText(managerAddEmployee.this, "Invalid Manager ID.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(managerAddEmployee.this, "Failed to validate Manager ID.", Toast.LENGTH_SHORT).show());
    }

    private void generateEmployeeIdAndAdd() {
        // Get the last Employee ID and increment it
        db.collection("Employees")
                .orderBy("EmployeeID", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String lastId = queryDocumentSnapshots.getDocuments().get(0).getString("EmployeeID");
                    String newId = incrementEmployeeId(lastId);
                    addNewEmployee(newId);
                })
                .addOnFailureListener(e -> Toast.makeText(managerAddEmployee.this, "Failed to generate new Employee ID.", Toast.LENGTH_SHORT).show());
    }

    private String incrementEmployeeId(String lastId) {
        // Assumes the last ID is in the format "E001"
        int numericPart = Integer.parseInt(lastId.substring(1)) + 1;
        return "E" + String.format("%03d", numericPart);
    }

    private void addNewEmployee(String employeeId) {
        Log.d("AddEmployee", "Adding new employee with ID: " + employeeId);

        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String salary = editTextSalary.getText().toString().trim();
        String managerId = editTextManagerId.getText().toString().trim();

        // Build the employee object
        Employee employee = new Employee(employeeId, name, email, phone, salary, "123", managerId);

        // Add a new document with the generated ID
        db.collection("Employees").document(employeeId).set(employee)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(managerAddEmployee.this, "Employee added successfully.", Toast.LENGTH_SHORT).show();
                    Log.d("AddEmployee", "Employee added successfully");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(managerAddEmployee.this, "Error adding employee: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("AddEmployee", "Error adding employee", e);
                });
    }

    class Employee {
        public String EmployeeID, Name, Email, PhoneNumber, Salary, Password, ManagedBy;

        public Employee() {}
        public Employee(String employeeID, String name, String email, String phoneNumber, String salary, String password, String managedBy) {
            EmployeeID = employeeID;
            Name = name;
            Email = email;
            PhoneNumber = phoneNumber;
            Salary = salary;
            Password = password;
            ManagedBy = managedBy;
        }

        // Getters and setters if needed
    }
}
