package com.example.evms;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class adminSalesReport extends AppCompatActivity {
    LineChart lineChart;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sales_report);
        lineChart = findViewById(R.id.managerSalesLineChart);
        setupChartBasics();
        fetchSalesData();  // Asynchronously fetch data and setup chart in its callback

        Toast.makeText(this, "Please Rotate Your Screen", Toast.LENGTH_SHORT).show();


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

    private void setupChartBasics() {
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false); // Disable right y-axis

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // Start at zero
        leftAxis.setTextColor(Color.WHITE);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(30); // Ensuring all days are marked
        xAxis.setValueFormatter(new ValueFormatter() {
            private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d");

            @Override
            public String getFormattedValue(float value) {
                LocalDate date = LocalDate.ofEpochDay((long) value);
                return date.format(dateFormat); // Only day of the month
            }
        });
    }

    private void fetchSalesData() {
        Map<LocalDate, Double> dailySales = new HashMap<>();
        LocalDate startDate = LocalDate.parse("2024-5-1", formatter);
        LocalDate endDate = LocalDate.parse("2024-5-30", formatter);

        db.collection("ServiceHistory")
                .whereGreaterThanOrEqualTo("ServiceDate", "2024-5-1")
                .whereLessThanOrEqualTo("ServiceDate", "2024-5-30")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int expectedCount = queryDocumentSnapshots.size(); // Number of ServiceHistory documents
                    AtomicInteger actualCount = new AtomicInteger(0); // Count successful fetches

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String serviceDateStr = document.getString("ServiceDate");
                        LocalDate serviceDate = LocalDate.parse(serviceDateStr, formatter);
                        if (!serviceDate.isBefore(startDate) && !serviceDate.isAfter(endDate)) {
                            String serviceId = document.getString("ServiceID");
                            // Query to match ServiceID as an attribute
                            db.collection("Service")
                                    .whereEqualTo("ServiceID", serviceId)
                                    .get()
                                    .addOnSuccessListener(serviceDocuments -> {
                                        for (QueryDocumentSnapshot serviceDoc : serviceDocuments) {
                                            String priceStr = serviceDoc.getString("ServicePrice");
                                            if (priceStr != null) {
                                                try {
                                                    double price = Double.parseDouble(priceStr.trim());
                                                    dailySales.merge(serviceDate, price, Double::sum);
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Error parsing price for service ID " + serviceId + ": " + priceStr);
                                                }
                                            } else {
                                                System.out.println("No price found for service ID " + serviceId);
                                            }
                                        }
                                        // When all data is fetched and processed, update the chart
                                        if (actualCount.incrementAndGet() == expectedCount) {
                                            updateChart(dailySales, startDate, endDate);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        System.out.println("Failed to fetch service price for ID " + serviceId + ": " + e.getMessage());
                                        if (actualCount.incrementAndGet() == expectedCount) {
                                            updateChart(dailySales, startDate, endDate);
                                        }
                                    });
                        } else {
                            actualCount.incrementAndGet(); // Increment even if the date is out of range
                        }
                    }
                })
                .addOnFailureListener(e -> System.out.println("Failed to fetch service history: " + e.getMessage()));
    }

    private void updateChart(Map<LocalDate, Double> dailySales, LocalDate startDate, LocalDate endDate) {
        List<Entry> entries = new ArrayList<>();
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            Double sum = dailySales.getOrDefault(date, 0.0);
            long daysSinceStart = date.toEpochDay() - startDate.toEpochDay();
            entries.add(new Entry(daysSinceStart, sum.floatValue()));
            date = date.plusDays(1);
        }

        LineDataSet dataSet = new LineDataSet(entries, "Daily Sales");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setCircleColors(Color.RED);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(9f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); // Refresh the chart
    }
}