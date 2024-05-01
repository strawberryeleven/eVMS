package com.example.evms;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {
    private List<Service> services;
    private Context context;
    private final View.OnClickListener onItemClickListener;
    private String customerEmail;

    public ServiceAdapter(Context context, List<Service> services, View.OnClickListener onItemClickListener, String email) {
        this.context = context;
        this.services = services;
        this.onItemClickListener = onItemClickListener;
        customerEmail = email;
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
                // Retrieve the selected service
                Service service = services.get(pos);
                // Implement buying logic here
                purchaseService(service);
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

        holder.itemView.setOnClickListener(v -> {
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

            buyButton.setOnClickListener(buyView -> {
                // Prompt the user to select a date
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        (view, year, monthOfYear, dayOfMonth) -> {
                            // Date selected, now prompt for number plate
                            promptForNumberPlate(service, year, monthOfYear, dayOfMonth);
                        },
                        // Set initial date to current date
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void promptForNumberPlate(Service service, int year, int month, int dayOfMonth) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Number Plate");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String numberPlate = input.getText().toString().trim();
            if (!numberPlate.isEmpty()) {
                // Construct the selected date
                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;

                // Prepare data to be added to Firestore
                Map<String, Object> pendingServiceData = new HashMap<>();
                pendingServiceData.put("CustomerEmail", customerEmail);
                pendingServiceData.put("MaintenanceDate", selectedDate);
                pendingServiceData.put("NumberPlate", numberPlate);
                pendingServiceData.put("ServiceID", service.getServiceID());

                // Access the Firestore instance
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Add data to the PendingService collection
                db.collection("PendingService")
                        .add(pendingServiceData)
                        .addOnSuccessListener(documentReference -> {
                            // On success, show a success message
                            Toast.makeText(context, "Service purchased on " + selectedDate + " for number plate: " + numberPlate, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // On failure, show an error message
                            Toast.makeText(context, "Failed to purchase service. Please try again.", Toast.LENGTH_SHORT).show();
                            Log.e("Firestore", "Error adding document", e);
                        });
            } else {
                Toast.makeText(context, "Please enter the number plate", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    private void purchaseService(Service service) {


    }
    public void showServiceDetailsDialog(Service service) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_service_details, null);
        builder.setView(dialogView);

        // Initialize views and set service details...

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

    public void setCustomerEmail(String email) {
        this.customerEmail = email;
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
