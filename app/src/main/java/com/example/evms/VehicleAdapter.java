package com.example.evms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private List<Vehicle> vehicleList;

    public VehicleAdapter(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vehicle_row, parent, false);
        return new VehicleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);
        holder.numberPlate.setText(vehicle.getNumberPlate());
        holder.model.setText(vehicle.getModel());
        holder.kmsDriven.setText(vehicle.getKmsDriven());
        holder.color.setText(vehicle.getColor());
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    static class VehicleViewHolder extends RecyclerView.ViewHolder {
        TextView numberPlate, model, kmsDriven, color;

        VehicleViewHolder(View view) {
            super(view);
            numberPlate = view.findViewById(R.id.tvNumberPlate);
            model = view.findViewById(R.id.tvModel);
            kmsDriven = view.findViewById(R.id.tvKmsDriven);
            color = view.findViewById(R.id.tvColor);
        }
    }
}
