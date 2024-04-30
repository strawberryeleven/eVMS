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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.UUID;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import android.widget.EditText;
import android.util.Log;

public class adminAddService extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText editTextServiceName, editTextServiceDescription, editTextServicePrice, editTextServiceType;
    private ImageView uploadImageView;
    private Uri selectedImageUri;

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
    }

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

    private void addService() {
        String serviceName = editTextServiceName.getText().toString().trim();
        String serviceDescription = editTextServiceDescription.getText().toString().trim();
        String servicePrice = editTextServicePrice.getText().toString().trim();
        String serviceType = editTextServiceType.getText().toString().trim();

        if (serviceName.isEmpty() || serviceDescription.isEmpty() || servicePrice.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please fill out all fields and select an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> service = new HashMap<>();
        service.put("ServiceName", serviceName);
        service.put("ServiceType", serviceType);
        service.put("ServiceDescription", serviceDescription);
        service.put("ServicePrice", servicePrice);
        service.put("ServiceRating", 0);
        service.put("DateOfCreation", new Date());

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
