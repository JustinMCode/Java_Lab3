package main.java.ui;

import main.java.model.CountryData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ChartPanelCustom visualizes GDP metrics using JFreeChart.
 * Supports multiple countries for comparison.
 */
public class ChartPanelCustom extends JPanel {
    private final JFreeChart lineChart;
    private List<CountryData> currentData; // Holds the currently filtered data

    // Constructor initializes the chart with the provided data.
    public ChartPanelCustom(List<CountryData> dataList) {
        super(new BorderLayout());
        this.currentData = dataList; // Initialize currentData with dataList

        // Initialize dataset with default metric
        String defaultMetric = "GDP per capita (constant 2005 US$)";
        DefaultCategoryDataset dataset = createDataset(currentData, defaultMetric);

        // Create line chart with legend
        lineChart = ChartFactory.createLineChart(
                "GDP Metrics Over Time",
                "Year",
                defaultMetric,
                dataset,
                PlotOrientation.VERTICAL,
                true, // Include legend
                true, // Tooltips
                false // URLs
        );

        // Enable tooltips
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) lineChart.getCategoryPlot().getRenderer();
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator());

        // Initialize ChartPanel
        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setDisplayToolTips(true); // Ensure tooltips are enabled
        lineChart.getLegend().setPosition(RectangleEdge.RIGHT); // Positions legend to the right

        add(chartPanel, BorderLayout.CENTER);
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

    // Updates the chart based on the filtered data and selected metric.
    public void updateChart(List<CountryData> filteredData, String selectedMetric) {
        this.currentData = filteredData; // Update currentData with filteredData
        updateChartWithMetric(selectedMetric);
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
}
