package main.java.ui;

import main.java.model.CountryData;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import javax.swing.*;
import java.awt.*;
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
        var ref = new Object() {
            int minYear = Integer.MAX_VALUE;
            int maxYear = Integer.MIN_VALUE;
        };

        dataList.stream()
                .filter(data -> {
                    String seriesName = data.getSeriesName();
                    if (seriesName == null) {
                        System.out.println("Series name is null for country: " + data.getCountryName());
                        return false;
                    }
                    return seriesName.trim().equalsIgnoreCase(selectedMetric);
                })
                .forEach(data -> {
                    Map<Integer, Double> yearlyData = data.getYearlyData();
                    if (yearlyData != null) {
                        for (Map.Entry<Integer, Double> entry : yearlyData.entrySet()) {
                            Integer year = entry.getKey();
                            Double value = entry.getValue();
                            if (value != null) {
                                stats.addValue(value);
                                if (year < ref.minYear) ref.minYear = year;
                                if (year > ref.maxYear) ref.maxYear = year;
                            }
                        }
                    }
                });

        if (stats.getN() > 0) {
            double average = stats.getMean();
            double min = stats.getMin();
            double max = stats.getMax();

            String yearRange = "";
            if (ref.minYear != Integer.MAX_VALUE && ref.maxYear != Integer.MIN_VALUE) {
                yearRange = String.format("(%d - %d)", ref.minYear, ref.maxYear);
            }

            averageLabel.setText(String.format("Average %s %s: %.2f", selectedMetric, yearRange, average));
            minLabel.setText(String.format("Minimum %s %s: %.2f", selectedMetric, yearRange, min));
            maxLabel.setText(String.format("Maximum %s %s: %.2f", selectedMetric, yearRange, max));
        } else {
            averageLabel.setText(String.format("Average %s: No data available", selectedMetric));
            minLabel.setText(String.format("Minimum %s: No data available", selectedMetric));
            maxLabel.setText(String.format("Maximum %s: No data available", selectedMetric));
        }

        revalidate();
        repaint();
    }

    // Method to update stats when data changes (e.g., after filtering)
    public void updateStats(List<CountryData> filteredData, String selectedMetric) {
        calculateStats(filteredData, selectedMetric);
    }
}
