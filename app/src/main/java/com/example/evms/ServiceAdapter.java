package com.example.evms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {
    private List<Service> services;
    private Context context;
    private final View.OnClickListener onItemClickListener;

    public ServiceAdapter(Context context, List<Service> services, View.OnClickListener onItemClickListener) {
        this.context = context;
        this.services = services;
        this.onItemClickListener = onItemClickListener; // Preserves any custom click behavior passed externally
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        // Setup the click listener to open the service details dialog
        view.setOnClickListener(v -> {
            int pos = viewHolder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                showServiceDetailsDialog(services.get(pos));
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Service service = services.get(position);
        holder.serviceName.setText(service.getServiceName());
        holder.serviceType.setText(service.getServiceType());
        holder.servicePrice.setText(String.format("$%.2f", service.getServicePrice()));
        holder.serviceRating.setText(String.format("%.1f", service.getServiceRating()));
    }

    public void showServiceDetailsDialog(Service service) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_service_details, null);
        builder.setView(dialogView);

        TextView serviceName = dialogView.findViewById(R.id.dialog_serviceName);
        TextView serviceType = dialogView.findViewById(R.id.dialog_serviceType);
        CircleImageView serviceImage = dialogView.findViewById(R.id.dialog_serviceImage);
        TextView serviceDescription = dialogView.findViewById(R.id.dialog_serviceDescription);
        TextView servicePrice = dialogView.findViewById(R.id.dialog_servicePrice);
        TextView serviceRating = dialogView.findViewById(R.id.dialog_serviceRating);
        Button buyButton = dialogView.findViewById(R.id.dialog_buyButton);

        serviceName.setText(service.getServiceName());
        serviceType.setText(service.getServiceType());
        Picasso.get().load(service.getImageUrl()).into(serviceImage);
        serviceDescription.setText(service.getServiceDescription());
        servicePrice.setText(String.format("$%.2f", service.getServicePrice()));
        serviceRating.setText(String.format("%.1f", service.getServiceRating()));

        buyButton.setOnClickListener(v -> {
            // Implement buying logic or redirect
            Toast.makeText(context, "Buy feature not implemented", Toast.LENGTH_SHORT).show();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public void updateData(List<Service> newServices) {
        services = newServices;
        notifyDataSetChanged();
    }

    public Service getItemAt(int position) {
        if (position >= 0 && position < services.size()) {
            return services.get(position);
        } else {
            return null; // or throw an exception if that's your error handling strategy
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView serviceName, serviceType, servicePrice, serviceRating;

        public ViewHolder(View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.serviceName);
            serviceType = itemView.findViewById(R.id.serviceType);
            servicePrice = itemView.findViewById(R.id.servicePrice);
            serviceRating = itemView.findViewById(R.id.serviceRating);
        }
    }
}