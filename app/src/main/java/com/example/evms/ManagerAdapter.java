package com.example.evms;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerAdapter extends RecyclerView.Adapter<ManagerAdapter.ManagerViewHolder> {

    private Context context;
    private List<Manager> managersList;
    private Map<String, String> employeeNames = new HashMap<>();
    private Map<String, Integer> employeeCounts = new HashMap<>();

    public ManagerAdapter(Context context, List<Manager> managersList) {
        this.context = context;
        this.managersList = managersList;
        fetchEmployeeDetails();  // Trigger fetching and counting here, but ensure data is ready before using it
    }

    @NonNull
    @Override
    public ManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_item, parent, false);
        return new ManagerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ManagerViewHolder holder, int position) {
        Manager manager = managersList.get(position);
        holder.managerID.setText(manager.getManagerID());
        holder.salary.setText(manager.getSalary());
        holder.name.setText(employeeNames.getOrDefault(manager.getOldEmployeeId(), "N/A"));
        Integer count = employeeCounts.get(manager.getManagerID()); // Safely retrieve count
        if (count != null) {
            holder.numberOfEmployees.setText(String.valueOf(count));
        } else {
            holder.numberOfEmployees.setText("0"); // Default to 0 if not found
        }
    }

    @Override
    public int getItemCount() {
        return managersList.size();
    }

    private void fetchEmployeeDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Employees").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                employeeNames.clear();
                employeeCounts.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Employee employee = document.toObject(Employee.class);
                    employeeNames.put(employee.getEmployeeID(), employee.getName());
                    String managedBy = employee.getManagedBy();
                    if (managedBy != null) {
                        int count = employeeCounts.getOrDefault(managedBy, 0);
                        employeeCounts.put(managedBy, count + 1);
                    }
                }
                notifyDataSetChanged();  // Ensure this is called after all data is ready
            } else {
                Log.e("ManagerAdapter", "Failed to fetch employee details: ", task.getException());
            }
        });
    }

    public static class ManagerViewHolder extends RecyclerView.ViewHolder {
        public TextView managerID, name, salary, numberOfEmployees;

        public ManagerViewHolder(View view) {
            super(view);
            managerID = view.findViewById(R.id.textViewManagerID);
            name = view.findViewById(R.id.textViewName);
            salary = view.findViewById(R.id.textViewSalary);
            numberOfEmployees = view.findViewById(R.id.textViewNumberOfEmployees);
        }
    }
}

