package main.java.ui;

import main.java.model.CountryData;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StatsPanel extends JPanel {
    private final JLabel averageLabel;
    private final JLabel minLabel;
    private final JLabel maxLabel;

    public StatsPanel(List<CountryData> dataList) {
        super(new GridLayout(3, 1)); // Using GridLayout for simplicity

        averageLabel = new JLabel("Average: ");
        minLabel = new JLabel("Minimum: ");
        maxLabel = new JLabel("Maximum: ");

        add(averageLabel);
        add(minLabel);
        add(maxLabel);
    }

    private void calculateStats(List<CountryData> dataList, String selectedMetric) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        List<Integer> years = new ArrayList<>();

        // If "All Series" is selected, default to "GDP per capita (constant 2005 US$)"
        String metricForStats = selectedMetric.equals("All Series") ? "GDP per capita (constant 2005 US$)" : selectedMetric;

        for (CountryData data : dataList) {
            String seriesName = data.getSeriesName();
            if (seriesName == null) {
                System.out.println("Series name is null for country: " + data.getCountryName());
                continue;
            }
            if (seriesName.trim().equalsIgnoreCase(metricForStats)) {
                Map<Integer, Double> yearlyData = data.getYearlyData();
                if (yearlyData != null) {
                    for (Map.Entry<Integer, Double> entry : yearlyData.entrySet()) {
                        int year = entry.getKey();
                        Double value = entry.getValue();
                        if (value != null) {
                            stats.addValue(value);
                            years.add(year);
                        }
                    }
                }
            }
        }

        if (stats.getN() > 0) {
            double average = stats.getMean();
            double min = stats.getMin();
            double max = stats.getMax();

            String yearRange = "";
            if (!years.isEmpty()) {
                int minYear = Collections.min(years);
                int maxYear = Collections.max(years);
                yearRange = String.format("(%d - %d)", minYear, maxYear);
            }

            averageLabel.setText(String.format("Average %s %s: %.2f", metricForStats, yearRange, average));
            minLabel.setText(String.format("Minimum %s %s: %.2f", metricForStats, yearRange, min));
            maxLabel.setText(String.format("Maximum %s %s: %.2f", metricForStats, yearRange, max));
        } else {
            averageLabel.setText(String.format("Average %s: No data available", metricForStats));
            minLabel.setText(String.format("Minimum %s: No data available", metricForStats));
            maxLabel.setText(String.format("Maximum %s: No data available", metricForStats));
        }

        revalidate();
        repaint();
    }

    // Method to update stats when data changes (e.g., after filtering)
    public void updateStats(List<CountryData> filteredData, String selectedMetric) {
        calculateStats(filteredData, selectedMetric);
    }
}
