package com.example.evms;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoveEmployeeAdapter extends RecyclerView.Adapter<RemoveEmployeeAdapter.RemoveEmployeeViewHolder> {

    private Context context;
    private List<Employee> employeesList;
    private Map<String, Double> employeeRatings = new HashMap<>();

    public RemoveEmployeeAdapter(Context context, List<Employee> employeesList) {
        this.context = context;
        this.employeesList = employeesList;
        fetchEmployeeDetails();  // Triggering fetching and rating here
    }

    @NonNull
    @Override
    public RemoveEmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.remove_employee_row, parent, false);
        return new RemoveEmployeeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RemoveEmployeeViewHolder holder, int position) {
        Employee employee = employeesList.get(position);
        holder.employeeID.setText(employee.getEmployeeID());
        holder.salary.setText(employee.getSalary());
        holder.name.setText(employee.getName());

        Double avgRating = employeeRatings.get(employee.getEmployeeID()); // Safely retrieve avg rating
        if (avgRating != null) {
            holder.avgEmployeeRating.setText(String.format("%.2f", avgRating)); // Format to 2 decimal places
        } else {
            holder.avgEmployeeRating.setText("N/A"); // Display N/A if rating is not available
        }
    }

    @Override
    public int getItemCount() {
        return employeesList.size();
    }

    private void fetchEmployeeDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // First fetch all employees
        db.collection("Employees").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                employeeRatings.clear();
                List<Task<QuerySnapshot>> ratingTasks = new ArrayList<>();
                for (QueryDocumentSnapshot employeeDocument : task.getResult()) {
                    Employee employee = employeeDocument.toObject(Employee.class);
                    // Fetch ratings for each employee and calculate the average
                    Task<QuerySnapshot> ratingTask = db.collection("EmployeeRating")
                            .whereEqualTo("EmployeeID", employee.getEmployeeID())
                            .get();

                    ratingTasks.add(ratingTask);
                }

                // Wait for all rating fetch tasks to complete
                Tasks.whenAllComplete(ratingTasks).addOnCompleteListener(ratingTasksComplete -> {
                    for (Task<QuerySnapshot> ratingTask : ratingTasks) {
                        if (ratingTask.isSuccessful()) {
                            double sum = 0;
                            int count = 0;
                            String employeeID = "";
                            for (QueryDocumentSnapshot ratingDoc : ratingTask.getResult()) {
                                Double rating = ratingDoc.getDouble("EmployeeRating");
                                employeeID = ratingDoc.getString("EmployeeID"); // assuming the employee ID is stored in each rating
                                if (rating != null) {
                                    sum += rating;
                                    count++;
                                }
                            }
                            if (count > 0) {
                                double avgRating = sum / count;
                                employeeRatings.put(employeeID, avgRating); // Store the average rating
                            }
                        }
                    }
                    notifyDataSetChanged();  // Update adapter once all ratings are calculated
                });
            } else {
                Log.e("RemoveEmployeeAdapter", "Failed to fetch employee details: ", task.getException());
            }
        });
    }


    public static class RemoveEmployeeViewHolder extends RecyclerView.ViewHolder {
        public TextView employeeID, name, salary, avgEmployeeRating;

        public RemoveEmployeeViewHolder(View view) {
            super(view);
            employeeID = view.findViewById(R.id.textViewEmployeeID);
            name = view.findViewById(R.id.textViewName);
            salary = view.findViewById(R.id.textViewSalary);
            avgEmployeeRating = view.findViewById(R.id.textViewEmployeeRating);
        }
    }
}

