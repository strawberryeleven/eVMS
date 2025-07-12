package com.example.evms;

import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.HashMap;
import java.util.Map;

public class managerAddEmployee extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText editTextName, editTextEmail, editTextPhone, editTextSalary, editTextManagerId;
    private Button btnAddEmployee;
    private Button backButton;
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

        backButton = findViewById(R.id.backButton);
        // Set up the listener for the back button
        backButton.setOnClickListener(v->onBackPressed());
    }
    @Override
    public void onBackPressed() {
        // Check if there are fragments in the back stack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // If there are fragments in the back stack, pop the fragment
            getSupportFragmentManager().popBackStack();
        } else {
            // If there are no fragments in the back stack, perform default back button behavior
            super.onBackPressed();
        }
    }

    private void showConfirmationDialog() {
        if(validateFields())
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

    private boolean validateFields() {
        if (editTextName.getText().toString().trim().isEmpty() ||
                editTextEmail.getText().toString().trim().isEmpty() ||
                editTextPhone.getText().toString().trim().isEmpty() ||
                editTextSalary.getText().toString().trim().isEmpty() ||
                editTextManagerId.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Empty field(s). Please fill out all fields.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void validateManagerIdAndAddEmployee() {
        validateEmployeeEmail();
    }

    private void validateEmployeeEmail() {
        String email = editTextEmail.getText().toString().trim();

        // Check if the email already exists in the Employees collection
        db.collection("Employees")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Email already exists, show error message
                        Toast.makeText(managerAddEmployee.this, "Email is already in use!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Email is unique, proceed with validating Manager ID
                        String managerId = editTextManagerId.getText().toString().trim();
                        validateManagerId(managerId);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(managerAddEmployee.this, "Error checking email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void validateManagerId(String managerId) {
        // Check if Manager ID is valid
        db.collection("Manager")
                .whereEqualTo("ManagerID", managerId) // Use the correct field name as per your schema
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Manager ID is valid, proceed to generate Employee ID and add employee
                        generateEmployeeIdAndAdd();
                    } else {
                        Toast.makeText(managerAddEmployee.this, "Invalid Manager ID.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("AddEmployee", "Failed to validate Manager ID", e));
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
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String salary = editTextSalary.getText().toString().trim();
        String managerId = editTextManagerId.getText().toString().trim();

        // Name validation: should not contain numbers
        if (!name.matches("[^0-9]+")) {
            Toast.makeText(getApplicationContext(), "Name must not include numbers.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Email validation: should follow proper email syntax
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "Enter a valid email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Phone validation: should only contain numbers and be exactly 11 digits long
        if (!phone.matches("\\d{11}")) {
            Toast.makeText(getApplicationContext(), "Phone must be exactly 11 digits long.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Salary validation: should be a valid number
        try {
            double salaryValue = Double.parseDouble(salary);
            if (salaryValue <= 0) {
                Toast.makeText(getApplicationContext(), "Salary must be greater than zero.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "Invalid salary format.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> employee = new HashMap<>();
        employee.put("EmployeeID", employeeId);
        employee.put("ManagedBy", managerId);
        employee.put("Name", name);
        employee.put("email", email);
        employee.put("password", "123");
        employee.put("phoneNumber", phone);
        employee.put("salary", salary); // Assuming salary is stored as a String


        // Add a new document with the generated ID
        db.collection("Employees").document(employeeId).set(employee)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(managerAddEmployee.this, "Employee added successfully.", Toast.LENGTH_SHORT).show();
                    Log.d("AddEmployee", "Employee added successfully");
                    redirectToAdminHomepage();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(managerAddEmployee.this, "Error adding employee: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("AddEmployee", "Error adding employee", e);
                });
    }


    private void redirectToAdminHomepage() {
        // Redirect to the admin homepage. Adjust this if you have a different activity for the homepage.
        Intent intent = new Intent(managerAddEmployee.this, ManagerHomepage.class);
        startActivity(intent);
        finish();
    }
}