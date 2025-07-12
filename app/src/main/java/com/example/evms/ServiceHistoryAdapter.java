package com.example.evms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ServiceHistoryAdapter extends RecyclerView.Adapter<ServiceHistoryAdapter.ServiceHistoryViewHolder> {

    private List<ServiceHistory> serviceHistoryList;

    public ServiceHistoryAdapter(List<ServiceHistory> serviceHistoryList) {
        this.serviceHistoryList = serviceHistoryList;
    }

    @NonNull
    @Override
    public ServiceHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_history_row, parent, false);
        return new ServiceHistoryViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull ServiceHistoryViewHolder holder, int position) {
        ServiceHistory serviceHistory = serviceHistoryList.get(position);
        holder.serviceName.setText(serviceHistory.getServiceName());
        holder.serviceDate.setText(serviceHistory.getServiceDate());
        holder.carNumber.setText(serviceHistory.getNumberPlate());
        holder.price.setText(serviceHistory.getServicePrice() != null ? "$" + serviceHistory.getServicePrice() : "N/A");
    }


    @Override
    public int getItemCount() {
        return serviceHistoryList.size();
    }

    static class ServiceHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName, serviceDate, carNumber, price;

        ServiceHistoryViewHolder(View view) {
            super(view);
            serviceName = view.findViewById(R.id.tvServiceName);  // Correct ID from layout
            serviceDate = view.findViewById(R.id.tvServiceDate);  // Correct ID from layout
            carNumber = view.findViewById(R.id.tvCarNumber);      // Correct ID from layout
            price = view.findViewById(R.id.Price);              // Correct ID from layout
        }
    }
}