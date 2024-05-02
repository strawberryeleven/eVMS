package com.example.evms;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class adminEmployeeReport extends AppCompatActivity {
    BarChart barChart;
    FirebaseFirestore db;
    private Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_employee_report);
        barChart = findViewById(R.id.employeeBarChart);
        setupBarChart();
        fetchEmployeeRatings();

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
            super.onBackPressed();
        }
    }

    private void setupBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(true);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setLabelRotationAngle(-45);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);


    }

    private void fetchEmployeeRatings() {
        db = FirebaseFirestore.getInstance();
        Map<String, List<Double>> ratingsMap = new HashMap<>();

        db.collection("EmployeeRating").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String employeeID = document.getString("EmployeeID");
                    Object ratingObj = document.get("EmployeeRating");  // Get the rating as an Object

                    // Check if the rating is a Number before casting it to Double
                    if (ratingObj instanceof Number) {
                        double rating = ((Number) ratingObj).doubleValue();
                        ratingsMap.computeIfAbsent(employeeID, k -> new ArrayList<>()).add(rating);
                    } else {
                        Log.w("adminEmployeeReport", "Invalid type for EmployeeRating: " + ratingObj);
                    }
                }

                List<BarEntry> entries = new ArrayList<>();
                List<String> employeeIDs = new ArrayList<>();
                int index = 0;

                for (Map.Entry<String, List<Double>> entry : ratingsMap.entrySet()) {
                    List<Double> ratings = entry.getValue();
                    double average = 0;
                    for (Double r : ratings) {
                        average += r;
                    }
                    average /= ratings.size();

                    entries.add(new BarEntry(index, (float) average));
                    employeeIDs.add(entry.getKey());
                    index++;
                }

                BarDataSet dataSet = new BarDataSet(entries, "Employee Ratings");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                dataSet.setValueTextColor(Color.WHITE);
                dataSet.setValueTextSize(10f);

                BarData barData = new BarData(dataSet);
                barChart.setData(barData);

                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(employeeIDs));
                barChart.setFitBars(true);
                barChart.animateY(1500);
                barChart.invalidate();
            } else {
                Log.e("adminEmployeeReport", "Error fetching employee ratings: ", task.getException());
            }
        });
    }

}