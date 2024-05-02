// AndroidManifest.xml update: Ensure file provider and required permissions are set correctly.

package com.example.evms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import android.util.Log;


public class employeeScanNumberPlate extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_CROP_IMAGE = 102;
    private Button buttonCapture;
    private TextView textView19;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_scan_number_plate);

        buttonCapture = findViewById(R.id.button3);
        textView19 = findViewById(R.id.textView19);

        checkCameraPermission();

        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".provider",
                        photoFile);
                imageUri = photoURI; // Save the URI for later use
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageUri = Uri.fromFile(image); // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    private void startCrop(Uri sourceUri) {
        startCrop(sourceUri, 0, 0);  // Call the overloaded method with default values for aspect ratio
    }

    private void startCrop(Uri sourceUri, float aspectX, float aspectY) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped.jpg"));
        UCrop uCrop = UCrop.of(sourceUri, destinationUri);
        if (aspectX > 0 && aspectY > 0) {
            uCrop.withAspectRatio(aspectX, aspectY);
        } else {
            uCrop.useSourceImageAspectRatio();
        }
        uCrop.start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            startCrop(imageUri); // Ensures compatibility with existing calls
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                handleCroppedImage(resultUri);
            } else {
                Toast.makeText(this, "Error retrieving cropped image", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void handleCroppedImage(Uri resultUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
            extractText(bitmap);
        } catch (IOException e) {
            Toast.makeText(this, "Failed to load the cropped image", Toast.LENGTH_SHORT).show();
        }
    }

    private void extractText(Bitmap bitmap) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Toast.makeText(getApplicationContext(), "Text recognizer could not be set up on your device :(", Toast.LENGTH_SHORT).show();
            return;
        }
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<TextBlock> items = textRecognizer.detect(frame);
        StringBuilder allText = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            TextBlock item = items.valueAt(i);
            allText.append(item.getValue()).append("\n");
        }

        // Process all text to extract and format license plates correctly
        StringBuilder extractedText = new StringBuilder();
        Pattern pattern = Pattern.compile("([A-Z]{2,4})\\s*-?\\s*\\n?\\s*([0-9Oo]{3,4})", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(allText.toString());
        while (matcher.find()) {
            String letters = matcher.group(1).toUpperCase();  // Standardize letters to uppercase
            String numbers = matcher.group(2).replaceAll("[Oo]", "0");  // Replace common OCR errors
            extractedText.append(letters).append('-').append(numbers).append("\n");
        }

        textView19.setText(extractedText.toString().isEmpty() ? "No text found" : extractedText.toString());

        // Set the result and finish the activity
        if (!extractedText.toString().isEmpty()) {
            Intent data = new Intent();
            data.putExtra("numberPlate", extractedText.toString().trim());
            setResult(RESULT_OK, data);
            finish();  // Close this activity and return
        } else {
            setResult(RESULT_CANCELED);
            finish();  // Close this activity and return
        }
    }

}