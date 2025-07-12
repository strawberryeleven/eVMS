package com.example.evms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PendingServiceAdapter extends RecyclerView.Adapter<PendingServiceAdapter.PendingServiceViewHolder> {

    private List<PendingService> pendingServiceList;

    public PendingServiceAdapter(List<PendingService> pendingServiceList) {
        this.pendingServiceList = pendingServiceList;
    }

    @NonNull
    @Override
    public PendingServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_service_row, parent, false);
        return new PendingServiceViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull PendingServiceViewHolder holder, int position) {
        PendingService pendingService = pendingServiceList.get(position);
        holder.serviceName.setText(pendingService.getServiceName());
        holder.serviceDate.setText(pendingService.getMaintenanceDate());
        holder.carNumber.setText(pendingService.getNumberPlate());
        holder.price.setText(pendingService.getServicePrice() != null ? "$" + pendingService.getServicePrice() : "N/A");
    }


    @Override
    public int getItemCount() {
        return pendingServiceList.size();
    }

    static class PendingServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName, serviceDate, carNumber, price;

        PendingServiceViewHolder(View view) {
            super(view);
            serviceName = view.findViewById(R.id.tvServiceName);  // Correct ID from layout
            serviceDate = view.findViewById(R.id.tvServiceDate);  // Correct ID from layout
            carNumber = view.findViewById(R.id.tvCarNumber);      // Correct ID from layout
            price = view.findViewById(R.id.Price);              // Correct ID from layout
        }
    }
}