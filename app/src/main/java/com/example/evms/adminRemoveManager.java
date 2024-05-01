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
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

public class adminRemoveManager extends AppCompatActivity {

    private EditText editTextManagerID;
    private Button buttonRemoveManager;
    private RecyclerView recyclerViewManagers;
    private ManagerAdapter adapter; // Assuming you have a RecyclerView adapter named ManagerAdapter
    private List<Manager> managerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_remove_manager);
        initUI();
        setupButtonListeners();
        fetchManagersAndSetupAdapter(); // This is the new method to fetch and setup
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
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentReference managerRef = db.collection("Manager").document(managerID);
            DocumentSnapshot managerSnapshot = transaction.get(managerRef);
            if (managerSnapshot.exists()) {
                // Assuming 'oldEmployeeId' is correctly fetched and exists
                String oldEmployeeId = managerSnapshot.getString("oldEmployeeId");
                DocumentReference employeeRef = db.collection("Employees").document(oldEmployeeId);
                transaction.delete(managerRef);  // Delete manager
                transaction.delete(employeeRef);  // Delete linked employee
                return null;
            } else {
                throw new FirebaseFirestoreException("Manager does not exist",
                        FirebaseFirestoreException.Code.ABORTED);
            }
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(adminRemoveManager.this, "Manager and related employee removed successfully", Toast.LENGTH_LONG).show();
            refreshManagerList(); // Refresh the list to show updated data
        }).addOnFailureListener(e -> {
            Toast.makeText(adminRemoveManager.this, "Failed to remove manager or employee: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("RemoveManagerError", "Error removing manager or employee: ", e);
        });
    }



    private void refreshManagerList() {
        // Code to refresh the RecyclerView with updated data
        // This could involve re-querying the database or removing items from the adapter directly
    }
}
