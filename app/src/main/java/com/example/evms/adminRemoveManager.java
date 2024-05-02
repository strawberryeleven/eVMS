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

public class adminRemoveManager extends AppCompatActivity {

    private EditText editTextManagerID;
    private Button buttonRemoveManager;
    private RecyclerView recyclerViewManagers;
    private ManagerAdapter adapter; // Assuming you have a RecyclerView adapter named ManagerAdapter
    private List<Manager> managerList = new ArrayList<>();
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_remove_manager);
        initUI();
        setupButtonListeners();
        fetchManagersAndSetupAdapter(); // This is the new method to fetch and setup
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
        editTextManagerID = findViewById(R.id.editTextManagerID);
        buttonRemoveManager = findViewById(R.id.buttonRemoveManager);
        recyclerViewManagers = findViewById(R.id.recyclerViewManagers);
        recyclerViewManagers.setLayoutManager(new LinearLayoutManager(this)); // Ensure this is set
    }

    private void setupButtonListeners() {
        buttonRemoveManager.setOnClickListener(v -> {
            String managerID = editTextManagerID.getText().toString().trim();
            if (!managerID.isEmpty()) {
                confirmAndRemoveManager(managerID);
            } else {
                Toast.makeText(adminRemoveManager.this, "Please enter a Manager ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchManagersAndSetupAdapter() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Manager").orderBy("ManagerID").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                managerList.clear();  // Clear existing data
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Manager manager = document.toObject(Manager.class);
                    managerList.add(manager);
                }
                // Setup or refresh the adapter
                if (adapter == null) {
                    adapter = new ManagerAdapter(this, managerList);
                    recyclerViewManagers.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(this, "Failed to fetch managers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmAndRemoveManager(String managerID) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Removal")
                .setMessage("Do you really want to remove this manager?")
                .setPositiveButton("Yes", (dialog, which) -> removeManager(managerID))
                .setNegativeButton("No", null)
                .show();
    }

    private void removeManager(String managerID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch employee documents outside the transaction
        db.collection("Employees")
                .whereEqualTo("ManagedBy", managerID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Start the transaction
                    db.runTransaction((Transaction.Function<Void>) transaction -> {
                        DocumentReference managerRef = db.collection("Manager").document(managerID);
                        // Asynchronously get the manager document
                        DocumentSnapshot managerSnapshot = transaction.get(managerRef);
                        if (managerSnapshot.exists()) {
                            // Delete the manager
                            transaction.delete(managerRef);

                            // Process all employees managed by this manager
                            for (QueryDocumentSnapshot employeeSnapshot : queryDocumentSnapshots) {
                                DocumentReference employeeRef = db.collection("Employees").document(employeeSnapshot.getId());
                                transaction.update(employeeRef, "ManagedBy", "");  // Clear the ManagedBy field
                            }

                            // Also delete the employee document with the oldEmployeeID
                            String oldEmployeeId = managerSnapshot.getString("oldEmployeeId");
                            if (oldEmployeeId != null && !oldEmployeeId.isEmpty()) {
                                // Delete the employee document
                                DocumentReference oldEmployeeRef = db.collection("Employees").document(oldEmployeeId);
                                transaction.delete(oldEmployeeRef);
                            }

                            return null;  // Transaction must return null if it's void
                        } else {
                            throw new FirebaseFirestoreException("Manager does not exist", FirebaseFirestoreException.Code.ABORTED);
                        }
                    }).addOnSuccessListener(aVoid -> {
                        Toast.makeText(adminRemoveManager.this, "Manager removed and related employee records updated successfully.", Toast.LENGTH_LONG).show();
                        refreshManagerList();  // Refresh the manager list
                    }).addOnFailureListener(e -> {
                        Toast.makeText(adminRemoveManager.this, "Failed to remove manager or update employees: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("TransactionError", "Error during transaction: ", e);
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle failure to fetch employee documents
                    Toast.makeText(adminRemoveManager.this, "Failed to fetch employee documents: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("FetchError", "Error fetching employee documents: ", e);
                });
    }

    private void refreshManagerList() {
        // Code to refresh the RecyclerView with updated data
        // This could involve re-querying the database or removing items from the adapter directly
    }
}
