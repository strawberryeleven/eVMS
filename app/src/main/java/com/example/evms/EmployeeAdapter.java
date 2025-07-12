package com.example.evms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private List<Employee> employees;

    public EmployeeAdapter(List<Employee> employees) {
        this.employees = employees;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_row_item, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employees.get(position);
        holder.tvEmployeeID.setText(employee.getEmployeeID());
        holder.tvEmployeeName.setText(employee.getName());
        holder.tvEmployeeEmail.setText(employee.getEmail());
        holder.tvEmployeeSalary.setText(employee.getSalary());
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmployeeID, tvEmployeeName, tvEmployeeEmail, tvEmployeeSalary;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmployeeID = itemView.findViewById(R.id.tvEmployeeID);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvEmployeeEmail = itemView.findViewById(R.id.tvEmployeeEmail);
            tvEmployeeSalary = itemView.findViewById(R.id.tvEmployeeSalary);
            // Make sure there is no reference to the phone number TextView here.
        }
    }
}
