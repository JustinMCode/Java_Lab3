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
    private JFreeChart lineChart;
    private ChartPanel chartPanel;
    private JComboBox<String> metricComboBox;
    private List<CountryData> originalData;

    /**
     * Constructor initializes the chart with the provided data.
     *
     * @param dataList List of CountryData to visualize.
     */
    public ChartPanelCustom(List<CountryData> dataList) {
        super(new BorderLayout());
        this.originalData = dataList;

        // Initialize metric selection dropdown
        metricComboBox = new JComboBox<>();
        List<String> availableMetrics = dataList.stream()
                .map(CountryData::getSeriesName)
                .distinct()
                .collect(Collectors.toList());
        availableMetrics.forEach(metricComboBox::addItem);
        metricComboBox.setSelectedItem("GDP per capita (constant 2005 US$)");

        // Initialize dataset
        DefaultCategoryDataset dataset = createDataset(dataList, (String) metricComboBox.getSelectedItem());

        // Create line chart
        lineChart = ChartFactory.createLineChart(
                "GDP Metrics Over Time",
                "Year",
                (String) metricComboBox.getSelectedItem(),
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        // Initialize ChartPanel
        chartPanel = new ChartPanel(lineChart);
        add(chartPanel, BorderLayout.CENTER);

        // Add metric selection controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Select GDP Metric: "));
        controlPanel.add(metricComboBox);
        add(controlPanel, BorderLayout.NORTH);

        // Add action listener for metric selection
        metricComboBox.addActionListener(this::changeMetric);
    }

    /**
     * Handles metric selection changes.
     *
     * @param e ActionEvent triggered by selecting a different metric.
     */
    private void changeMetric(ActionEvent e) {
        String selectedMetric = (String) metricComboBox.getSelectedItem();
        lineChart.setTitle(selectedMetric + " Over Time");
        lineChart.getCategoryPlot().getRangeAxis().setLabel(selectedMetric);
        updateChartWithMetric(selectedMetric);
    }

    /**
     * Updates the chart based on the selected metric.
     *
     * @param selectedMetric The GDP metric selected by the user.
     */
    private void updateChartWithMetric(String selectedMetric) {
        DefaultCategoryDataset dataset = createDataset(originalData, selectedMetric);
        lineChart.getCategoryPlot().setDataset(dataset);
    }

    /**
     * Creates a dataset for the chart based on the selected series.
     *
     * @param dataList      List of CountryData.
     * @param selectedSeries Series to visualize (e.g., "GDP per capita (constant 2005 US$)").
     * @return DefaultCategoryDataset for the chart.
     */
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

    /**
     * Updates the chart based on the filtered data and selected metric.
     *
     * @param filteredData List of filtered CountryData.
     */
    public void updateChart(List<CountryData> filteredData) {
        String selectedMetric = (String) metricComboBox.getSelectedItem();
        DefaultCategoryDataset dataset = createDataset(filteredData, selectedMetric);
        lineChart.getCategoryPlot().setDataset(dataset);
        System.out.println("Chart updated with " + filteredData.size() + " entries for metric: " + selectedMetric);
    }
}
