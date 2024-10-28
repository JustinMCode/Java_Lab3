package main.java.ui;

import main.java.model.CountryData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ChartPanelCustom visualizes GDP metrics using JFreeChart.
 * Supports multiple countries for comparison.
 */
public class ChartPanelCustom extends JPanel {
    private final JFreeChart lineChart;
    private final JComboBox<String> metricComboBox;
    private List<CountryData> currentData;

    // Constructor initializes the chart with the provided data.
    public ChartPanelCustom(List<CountryData> dataList) {
        super(new BorderLayout());
        this.currentData = dataList; // Initialize currentData with dataList

        // Initialize metric selection dropdown
        metricComboBox = new JComboBox<>();
        List<String> availableMetrics = dataList.stream()
                .map(CountryData::getSeriesName)
                .distinct()
                .toList();
        availableMetrics.forEach(metricComboBox::addItem);
        metricComboBox.setSelectedItem("GDP per capita (constant 2005 US$)");

        // Initialize dataset
        DefaultCategoryDataset dataset = createDataset(currentData, (String) metricComboBox.getSelectedItem());

        // Create line chart without legend to prevent clutter
        lineChart = ChartFactory.createLineChart(
                "GDP Metrics Over Time",
                "Year",
                (String) metricComboBox.getSelectedItem(),
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        // Remove legend to declutter chart area
        lineChart.removeLegend();

        // Initialize ChartPanel
        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setDisplayToolTips(true);
        add(chartPanel, BorderLayout.CENTER);

        // Add metric selection controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Select GDP Metric: "));
        controlPanel.add(metricComboBox);
        add(controlPanel, BorderLayout.NORTH);

        // Add action listener for metric selection
        metricComboBox.addActionListener(this::changeMetric);
    }

    // Handles metric selection changes.
    private void changeMetric(ActionEvent e) {
        String selectedMetric = (String) metricComboBox.getSelectedItem();
        lineChart.setTitle(selectedMetric + " Over Time");
        lineChart.getCategoryPlot().getRangeAxis().setLabel(selectedMetric);
        updateChartWithMetric(selectedMetric);
    }

    // Updates the chart based on the selected metric.
    private void updateChartWithMetric(String selectedMetric) {
        DefaultCategoryDataset dataset = createDataset(currentData, selectedMetric);
        lineChart.getCategoryPlot().setDataset(dataset);

        // Update chart title with selected countries
        List<String> selectedCountries = currentData.stream()
                .map(CountryData::getCountryName)
                .distinct()
                .collect(Collectors.toList());

        String title = selectedMetric + " Over Time";
        if (!selectedCountries.isEmpty()) {
            title += " for " + String.join(", ", selectedCountries);
        }
        lineChart.setTitle(title);

        lineChart.getCategoryPlot().getRangeAxis().setLabel(selectedMetric);

        System.out.println("Chart updated with " + currentData.size() + " entries for metric: " + selectedMetric);
    }

    // Creates a dataset for the chart based on the selected series.
    private DefaultCategoryDataset createDataset(List<CountryData> dataList, String selectedSeries) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (CountryData data : dataList) {
            if (data.getSeriesName().equals(selectedSeries)) {
                for (Map.Entry<Integer, Double> entry : data.getYearlyData().entrySet()) {
                    Integer year = entry.getKey();
                    Double value = entry.getValue();
                    if (value != null) {
                        dataset.addValue(value, data.getCountryName(), year.toString());
                    }
                }
            }
        }

        return dataset;
    }

    // Updates the chart based on the filtered data and selected metric.

    public void updateChart(List<CountryData> filteredData) {
        this.currentData = filteredData; // Update currentData with filteredData
        String selectedMetric = (String) metricComboBox.getSelectedItem();
        updateChartWithMetric(selectedMetric);
    }
}
