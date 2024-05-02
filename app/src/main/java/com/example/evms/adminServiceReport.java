package com.example.evms;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class adminServiceReport extends AppCompatActivity {

    PieChart pieChart;
    FirebaseFirestore db = FirebaseFirestore.getInstance();  // Firestore instance
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_service_report);
        pieChart = findViewById(R.id.MpieChart);
        setupPieChart();
        loadPieChartData();  // Load data from Firestore

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

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);  // Set the legend text color to white
    }

    private void loadPieChartData() {
        Map<String, Integer> serviceCount = new HashMap<>();
        db.collection("ServiceHistory").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String serviceId = document.getString("ServiceID");
                    serviceCount.merge(serviceId, 1, Integer::sum);
                }
                fetchServiceNames(serviceCount);
            } else {
                System.out.println("Error getting documents: " + task.getException());
            }
        });
    }

    private void fetchServiceNames(Map<String, Integer> serviceCount) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        db.collection("Service").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, String> serviceNames = new HashMap<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    serviceNames.put(document.getString("ServiceID"), document.getString("ServiceName"));
                }
                int total = serviceCount.values().stream().mapToInt(Integer::intValue).sum();
                for (Map.Entry<String, Integer> entry : serviceCount.entrySet()) {
                    String serviceName = serviceNames.get(entry.getKey());
                    if (serviceName != null && total > 0) {
                        float percentage = (float) entry.getValue() / total * 100;
                        entries.add(new PieEntry(percentage, serviceName));
                    }
                }
                PieDataSet dataSet = new PieDataSet(entries, "Service Popularity");
                dataSet.setSliceSpace(3f);
                dataSet.setSelectionShift(5f);
                dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                PieData data = new PieData(dataSet);
                data.setValueTextSize(10f);
                data.setValueTextColor(Color.BLACK);  // Set the value text color to black
                pieChart.setData(data);
                pieChart.invalidate(); // Refresh the chart
            } else {
                System.out.println("Error getting documents: " + task.getException());
            }
        });
    }
}