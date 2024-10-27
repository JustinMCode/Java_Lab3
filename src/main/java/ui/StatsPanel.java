package main.java.ui;

import main.java.model.CountryData;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StatsPanel extends JPanel {
    private final JLabel averageLabel;
    private final JLabel minLabel;
    private final JLabel maxLabel;
    private final List<CountryData> fullDataList; // Store the full data list

    public StatsPanel(List<CountryData> dataList) {
        super(new GridLayout(3, 1)); // Using GridLayout for simplicity

        this.fullDataList = dataList; // Store the full data list

        averageLabel = new JLabel("Average (2016): ");
        minLabel = new JLabel("Minimum (2016): ");
        maxLabel = new JLabel("Maximum (2016): ");

        add(averageLabel);
        add(minLabel);
        add(maxLabel);

        calculateStats(fullDataList); // Use the full data list here
    }

    private void calculateStats(List<CountryData> dataList) {
        DescriptiveStatistics stats = new DescriptiveStatistics();

        dataList.stream()
                .filter(data -> {
                    String seriesName = data.getSeriesName();
                    if (seriesName == null) {
                        System.out.println("Series name is null for country: " + data.getCountryName());
                        return false;
                    }
                    boolean matches = seriesName.trim().equalsIgnoreCase("GDP per capita (constant 2005 US$)");
                    return matches;
                })
                .map(data -> {
                    Map<Integer, Double> yearlyData = data.getYearlyData(); // Use Map<Integer, Double>
                    if (yearlyData == null) {
                        return null;
                    }
                    return yearlyData.get(2016);
                })
                .filter(Objects::nonNull)
                .forEach(stats::addValue); // Directly add Double values

        if (stats.getN() > 0) {
            double average = stats.getMean();
            double min = stats.getMin();
            double max = stats.getMax();

            averageLabel.setText(String.format("Average GDP per Capita (2016): %.2f", average));
            minLabel.setText(String.format("Minimum GDP per Capita (2016): %.2f", min));
            maxLabel.setText(String.format("Maximum GDP per Capita (2016): %.2f", max));
        } else {
            averageLabel.setText("Average GDP per Capita (2016): No data available");
            minLabel.setText("Minimum GDP per Capita (2016): No data available");
            maxLabel.setText("Maximum GDP per Capita (2016): No data available");
        }

        revalidate();
        repaint();
    }

    // Method to update stats when data changes (e.g., after filtering)
    public void updateStats(List<CountryData> filteredData) {
        // Use the full data list to keep stats consistent
        calculateStats(fullDataList);
    }
}
