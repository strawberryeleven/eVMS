package com.example.evms;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.UUID;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import android.widget.EditText;
import android.util.Log;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class adminAddService extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText editTextServiceName, editTextServiceDescription, editTextServicePrice, editTextServiceType;
    private ImageView uploadImageView;
    private Uri selectedImageUri;
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_service);

        // Initialize views
        editTextServiceName = findViewById(R.id.nameField);
        editTextServiceType = findViewById(R.id.typeField);
        editTextServiceDescription = findViewById(R.id.descriptionField);
        editTextServicePrice = findViewById(R.id.priceField);
        uploadImageView = findViewById(R.id.uploadImageView);

        // Handle add service button click
        Button addServiceButton = findViewById(R.id.addServiceButton);
        addServiceButton.setOnClickListener(v -> addService());

        // Handle image upload
        uploadImageView.setOnClickListener(v -> pickImage());

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
            super.onBackPressed();}}



    private void pickImage() {
        ImagePicker.create(this)
                .single()
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image image = ImagePicker.getFirstImageOrNull(data);
            if (image != null) {
                selectedImageUri = Uri.fromFile(new File(image.getPath()));
                uploadImageView.setImageURI(selectedImageUri);
            }
        }
    }

    private void generateServiceIdAndAdd() {
        // Get the last Service ID and increment it
        db.collection("Service")
                .orderBy("ServiceID", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String lastId = queryDocumentSnapshots.getDocuments().get(0).getString("ServiceID");
                    String newId = incrementServiceId(lastId);
                    addNewService(newId);
                })
                .addOnFailureListener(e -> Toast.makeText(adminAddService.this, "Failed to generate new Service ID.", Toast.LENGTH_SHORT).show());
    }

    private String incrementServiceId(String lastId) {
        int numericPart;
        try {
            numericPart = Integer.parseInt(lastId.substring(1)) + 1;
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            // If the lastId does not follow the expected format, start from ID 1
            numericPart = 1;
        }
        return "S" + String.format("%03d", numericPart);
    }
    private void addService() {
        // Get the next available service ID
        getNextServiceIDFromDatabase(new OnNextServiceIDListener() {
            @Override
            public void onNextServiceID(int nextID) {
                if (nextID != -1) {
                    // Generate the service ID
                    String serviceId = "S" + String.format("%03d", nextID);
                    // Add the new service with the generated service ID
                    addNewService(serviceId);
                } else {
                    Toast.makeText(adminAddService.this, "Failed to generate new Service ID.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    interface OnNextServiceIDListener {
        void onNextServiceID(int nextID);
    }
    private void getNextServiceIDFromDatabase(OnNextServiceIDListener listener) {
        // Reference to your Firestore collection containing services
        CollectionReference serviceCollection = db.collection("Service");

        // Query to get the service documents sorted by ServiceID in descending order
        Query query = serviceCollection.orderBy("ServiceID", Query.Direction.DESCENDING).limit(1);

        // Perform the query
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int nextID;
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                // Get the document with the highest ServiceID
                String lastServiceID = doc.getString("ServiceID");
                if (lastServiceID != null && lastServiceID.matches("S\\d{3}")) {
                    // Extract the numeric part and increment it
                    int lastIDNumericPart = Integer.parseInt(lastServiceID.substring(1));
                    nextID = lastIDNumericPart + 1;
                    // Pass the next ID to the listener
                    listener.onNextServiceID(nextID);
                    return;
                }
            }
            // If no valid service ID found, start from ID 1
            nextID = 1;
            listener.onNextServiceID(nextID);
        }).addOnFailureListener(e -> {
            // Handle failure to retrieve the next service ID
            Log.e("getNextServiceID", "Failed to get next service ID: " + e.getMessage(), e);
            // Pass -1 to the listener to indicate failure
            listener.onNextServiceID(-1);
        });
    }



    private void addNewService(String serviceId) {
        String serviceName = editTextServiceName.getText().toString().trim();
        String serviceDescription = editTextServiceDescription.getText().toString().trim();
        String servicePrice = editTextServicePrice.getText().toString().trim();
        String serviceType = editTextServiceType.getText().toString().trim();

        // Check if all fields are filled and an image is selected
        if (serviceName.isEmpty() || serviceDescription.isEmpty() || servicePrice.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please fill out all fields and select an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Service Name validation: should not contain numbers
        if (!serviceName.matches("[^0-9]+")) {
            Toast.makeText(getApplicationContext(), "Service Name must not include numbers.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Service Type validation: should not contain numbers
        if (!serviceType.matches("[^0-9]+")) {
            Toast.makeText(getApplicationContext(), "Service Type must not include numbers.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Service Description validation: should not be longer than 50 characters
        if (serviceDescription.length() > 50) {
            Toast.makeText(getApplicationContext(), "Service Description must not exceed 50 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Service Price validation: should not be less than 100
        try {
            double price = Double.parseDouble(servicePrice);
            if (price < 100) {
                Toast.makeText(getApplicationContext(), "Service Price must be at least 100.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "Invalid Service Price format.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map to store service details
        Map<String, Object> service = new HashMap<>();
        service.put("ServiceID", serviceId);
        service.put("ServiceName", serviceName);
        service.put("ServiceType", serviceType);
        service.put("ServiceDescription", serviceDescription);
        service.put("ServicePrice", servicePrice);
        String num = "0";
        service.put("ServiceRating", num );
        service.put("DateOfCreation", new Date().toString());

        // Upload image and add service to Firestore
        uploadImageAndAddService(service);
    }

    private void uploadImageAndAddService(Map<String, Object> service) {
        String defaultImageUrl = "default"; // Default image URL

        uploadImageToStorage(selectedImageUri, imageUrl -> {
            service.put("ImageUrl", imageUrl != null ? imageUrl : defaultImageUrl);

            db.collection("Service").add(service)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(adminAddService.this, "Service added successfully.", Toast.LENGTH_SHORT).show();
                        editTextServiceName.getText().clear();
                        editTextServiceDescription.getText().clear();
                        editTextServicePrice.getText().clear();
                        editTextServiceType.getText().clear();
                        uploadImageView.setImageResource(R.drawable.upload_image); // Reset image view
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(adminAddService.this, "Error adding service: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void uploadImageToStorage(Uri imageUri, OnImageUploadListener listener) {
        Log.d("UploadImage", "Image URI: " + imageUri.toString());
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String filename = UUID.randomUUID().toString() + ".jpg"; // Unique filename with extension
        StorageReference imageRef = storageRef.child("ServiceImages/" + filename);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                        .addOnSuccessListener(uri -> listener.onImageUpload(uri.toString()))
                        .addOnFailureListener(e -> {
                            Log.e("UploadImage", "Failed to get download URL: " + e.getMessage(), e);
                            listener.onImageUpload(null);
                        }))
                .addOnFailureListener(e -> {
                    Log.e("UploadImage", "Error uploading image: " + e.getMessage(), e);
                    listener.onImageUpload(null);
                });
    }

    interface OnImageUploadListener {
        void onImageUpload(String imageUrl);
    }
}