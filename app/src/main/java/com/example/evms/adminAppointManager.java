package com.example.evms;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class adminAppointManager extends AppCompatActivity {

    private RecyclerView employeesRecyclerView;
    private EmployeeAdapter adapter;
    private ArrayList<Employee> employees;
    private EditText employeeIDEditText;
    private Button btnAppointManager;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_appoint_manager);

        db = FirebaseFirestore.getInstance();
        employeesRecyclerView = findViewById(R.id.employeesRecyclerView);
        employeeIDEditText = findViewById(R.id.employeeIDEditText);
        btnAppointManager = findViewById(R.id.btnAppointManager);
        employeesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        employees = new ArrayList<>();
        adapter = new EmployeeAdapter(employees);
        employeesRecyclerView.setAdapter(adapter);

        fetchEmployees();

        btnAppointManager.setOnClickListener(v -> {
            String enteredEmployeeID = employeeIDEditText.getText().toString().trim();
            if (enteredEmployeeID.isEmpty()) {
                Toast.makeText(this, "Please enter an Employee ID", Toast.LENGTH_SHORT).show();
            } else {
                attemptToAppointManager(enteredEmployeeID);
            }
        });
    }

    private void fetchEmployees() {
        db.collection("Employees")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        employees.clear();
                        task.getResult().forEach(documentSnapshot -> {
                            employees.add(documentSnapshot.toObject(Employee.class));
                        });
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading employees: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void attemptToAppointManager(String employeeID) {
        db.collection("Employees").document(employeeID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        db.collection("Manager").whereEqualTo("oldEmployeeId", employeeID).get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult().isEmpty()) {
                                        promptForNewSalary(employeeID);
                                    } else {
                                        Toast.makeText(this, "This employee is already a manager.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Invalid Employee ID.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error verifying employee: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void promptForNewSalary(String employeeID) {
        final EditText salaryInput = new EditText(this);
        salaryInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(this)
                .setTitle("Enter New Salary")
                .setMessage("Please enter the new salary for the manager:")
                .setView(salaryInput)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String newSalary = salaryInput.getText().toString();
                    if (!newSalary.isEmpty()) {
                        generateManagerIDAndWriteToDatabase(employeeID, newSalary);
                    } else {
                        Toast.makeText(this, "Salary cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void generateManagerIDAndWriteToDatabase(String employeeID, String newSalary) {
        db.collection("Manager").orderBy("ManagerID", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String newManagerID = "M001"; // Default starting ID
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String lastManagerID = queryDocumentSnapshots.getDocuments().get(0).getString("ManagerID");
                        int numericPart = Integer.parseInt(lastManagerID.substring(1)) + 1;
                        newManagerID = "M" + String.format("%03d", numericPart);
                    }

                    // Create a HashMap to hold manager details with correct case sensitivity
                    HashMap<String, Object> managerDetails = new HashMap<>();
                    managerDetails.put("ManagerID", newManagerID);
                    managerDetails.put("Salary", newSalary);
                    managerDetails.put("dateAppointed", new Timestamp(new Date()));
                    managerDetails.put("oldEmployeeId", employeeID);
                    managerDetails.put("password", "test123");

                    // Now, write the new manager to the Managers collection using the HashMap
                    db.collection("Manager").document(newManagerID).set(managerDetails)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "New manager appointed successfully!", Toast.LENGTH_SHORT).show();
                                redirectToAdminHomepage();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to appoint new manager: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching the last ManagerID: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void redirectToAdminHomepage() {
        startActivity(new Intent(this, adminHomepage.class));
        finish();
    }
}
