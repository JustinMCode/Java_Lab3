package main.java.ui;

import main.java.model.CountryData;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StatsPanel extends JPanel {
    private JLabel averageLabel;
    private JLabel minLabel;
    private JLabel maxLabel;

    public StatsPanel(List<CountryData> dataList) {
        super(new GridLayout(3, 1)); // Using GridLayout for simplicity

        averageLabel = new JLabel("Average (2016): ");
        minLabel = new JLabel("Minimum (2016): ");
        maxLabel = new JLabel("Maximum (2016): ");

        add(averageLabel);
        add(minLabel);
        add(maxLabel);

        calculateStats(dataList);
    }

    private void calculateStats(List<CountryData> dataList) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        dataList.stream()
                .filter(data -> data.getSeriesName().equals("GDP per capita (constant 2005 US$)"))
                .map(data -> data.getYearlyData().get(2016))
                .filter(value -> value != null)
                .forEach(stats::addValue);

        double average = stats.getMean();
        double min = stats.getMin();
        double max = stats.getMax();

        averageLabel.setText(String.format("Average GDP per Capita (2016): %.2f", average));
        minLabel.setText(String.format("Minimum GDP per Capita (2016): %.2f", min));
        maxLabel.setText(String.format("Maximum GDP per Capita (2016): %.2f", max));
    }

    // Method to update stats when data changes (e.g., after filtering)
    public void updateStats(List<CountryData> filteredData) {
        calculateStats(filteredData);
    }
}
