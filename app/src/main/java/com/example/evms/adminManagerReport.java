package com.example.evms;

import android.graphics.Color;
import android.os.Bundle;
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
import java.util.concurrent.atomic.AtomicInteger;
import android.util.Log;

public class adminManagerReport extends AppCompatActivity {
    BarChart barChart;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manager_report);
        barChart = findViewById(R.id.managerBarChart);
        setupBarChart();
        fetchManagerPerformance();
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
        leftAxis.setAxisMaximum(5f);
        leftAxis.setLabelCount(6, true);
        leftAxis.setGranularity(1f);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        barChart.getDescription().setText("Manager Performance");
        barChart.getDescription().setTextColor(Color.WHITE);
    }

    private void calculateAndUpdateChart(Map<String, List<Double>> managerRatings, Map<String, Integer> managerEmployeeCount) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> managerIDs = new ArrayList<>();
        AtomicInteger index = new AtomicInteger(0);

        managerRatings.forEach((managerID, ratings) -> {
            double sumRatings = ratings.stream().mapToDouble(Double::doubleValue).sum();
            int employeeCount = managerEmployeeCount.getOrDefault(managerID, 0);
            double avgRating = employeeCount > 0 ? sumRatings / employeeCount : 0;
            entries.add(new BarEntry(index.getAndIncrement(), (float) avgRating));
            managerIDs.add(managerID);
        });

        updateChart(entries, managerIDs);
    }

    private void fetchManagerPerformance() {
        db = FirebaseFirestore.getInstance();
        db.collection("EmployeeRating").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Double> employeeRatings = new HashMap<>();
                task.getResult().forEach(document -> {
                    String employeeID = document.getString("EmployeeID");
                    Double rating = document.getDouble("EmployeeRating");
                    employeeRatings.put(employeeID, rating);
                });

                db.collection("Employees").get().addOnSuccessListener(employees -> {
                    Map<String, List<Double>> managerRatings = new HashMap<>();
                    Map<String, Integer> managerEmployeeCount = new HashMap<>();

                    employees.forEach(employee -> {
                        String managerID = employee.getString("ManagedBy");
                        String employeeID = employee.getId();
                        managerRatings.computeIfAbsent(managerID, k -> new ArrayList<>()).add(employeeRatings.getOrDefault(employeeID, 0.0));
                        managerEmployeeCount.merge(managerID, 1, Integer::sum);
                    });

                    calculateAndUpdateChart(managerRatings, managerEmployeeCount);
                });
            } else {
                Log.e("ManagerPerformance", "Error fetching data", task.getException());
            }
        });
    }


    private void updateChart(List<BarEntry> entries, List<String> managerIDs) {
        BarDataSet dataSet = new BarDataSet(entries, "Manager Ratings");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(managerIDs));
        barChart.setFitBars(true);
        barChart.animateY(1500);
        barChart.invalidate(); // Refresh the chart
    }
}