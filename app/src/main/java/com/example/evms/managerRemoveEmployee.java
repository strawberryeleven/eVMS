package com.example.evms;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

public class managerRemoveEmployee extends AppCompatActivity {

    private EditText editTextEmployeeID;
    private Button buttonRemoveEmployee;
    private RecyclerView recyclerViewEmployees;
    private RemoveEmployeeAdapter adapter;
    private List<Employee> employeeList = new ArrayList<>();
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_remove_employee);
        initUI();
        setupButtonListeners();
        fetchEmployeesAndSetupAdapter(); // This is the new method to fetch and setup

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

    private void initUI() {
        editTextEmployeeID = findViewById(R.id.editTextEmployeeID);
        buttonRemoveEmployee = findViewById(R.id.buttonRemoveEmployee);
        recyclerViewEmployees = findViewById(R.id.recyclerViewRemoveEmployee);
        recyclerViewEmployees.setLayoutManager(new LinearLayoutManager(this)); // Ensure this is set
    }

    private void setupButtonListeners() {
        buttonRemoveEmployee.setOnClickListener(v -> {
            String employeeID = editTextEmployeeID.getText().toString().trim();
            if (!employeeID.isEmpty()) {
                confirmAndRemoveEmployee(employeeID);
            } else {
                Toast.makeText(managerRemoveEmployee.this, "Please enter an Employee ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchEmployeesAndSetupAdapter() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Employees").orderBy("EmployeeID").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                employeeList.clear();  // Clear existing data
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Employee employee = document.toObject(Employee.class);
                    employeeList.add(employee);
                }
                // Setup or refresh the adapter
                if (adapter == null) {
                    adapter = new RemoveEmployeeAdapter(this, employeeList);
                    recyclerViewEmployees.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(this, "Failed to fetch employees", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmAndRemoveEmployee(String employeeID) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Removal")
                .setMessage("Do you really want to remove this employee?")
                .setPositiveButton("Yes", (dialog, which) -> removeEmployee(employeeID))
                .setNegativeButton("No", null)
                .show();
    }

    private void removeEmployee(String employeeID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch employee documents outside the transaction
        db.collection("Employees")
                .whereEqualTo("EmployeeID", employeeID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Start the transaction
                    db.runTransaction((Transaction.Function<Void>) transaction -> {
                        DocumentReference employeeRef = db.collection("Employees").document(employeeID);
                        // Asynchronously get the manager document
                        DocumentSnapshot employeeSnapshot = transaction.get(employeeRef);
                        if (employeeSnapshot.exists()) {
                            // Delete the manager
                            transaction.delete(employeeRef);
                            return null;  // Transaction must return null if it's void
                        } else {
                            throw new FirebaseFirestoreException("Employee does not exist", FirebaseFirestoreException.Code.ABORTED);
                        }
                    }).addOnSuccessListener(aVoid -> {
                        Toast.makeText(managerRemoveEmployee.this, "Employee removed successfully.", Toast.LENGTH_LONG).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(managerRemoveEmployee.this, "Failed to remove employee: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("TransactionError", "Error during transaction: ", e);
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle failure to fetch employee documents
                    Toast.makeText(managerRemoveEmployee.this, "Failed to fetch employee documents: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("FetchError", "Error fetching employee documents: ", e);
                });
    }

    private void refreshEmployeeList() {
        // Code to refresh the RecyclerView with updated data
        // This could involve re-querying the database or removing items from the adapter directly
    }
}
