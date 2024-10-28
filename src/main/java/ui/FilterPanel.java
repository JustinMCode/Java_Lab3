package main.java.ui;

import main.java.model.CountryData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FilterPanel allows users to filter data based on specific criteria,
 * including selecting multiple countries and metrics.
 */
public class FilterPanel extends JPanel {
    private final JComboBox<String> metricComboBox;

    private final JList<String> countryList;
    private final DefaultListModel<String> countryListModel;

    private final List<CountryData> originalData;
    private final TablePanel tablePanel;
    private final StatsPanel statsPanel;
    private final ChartPanelCustom chartPanel;

    private static final int MAX_COUNTRIES = 5;

    // Constructor
    public FilterPanel(List<CountryData> dataList, TablePanel tablePanel, StatsPanel statsPanel, ChartPanelCustom chartPanel) {
        this.originalData = dataList;
        this.tablePanel = tablePanel;
        this.statsPanel = statsPanel;
        this.chartPanel = chartPanel;

        setLayout(new BorderLayout());

        // Initialize top filter components (Metric selection and filter buttons)
        JPanel topFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Extract unique series names for filtering and metric selection
        Set<String> seriesNames = originalData.stream()
                .map(CountryData::getSeriesName)
                .collect(Collectors.toSet());

        // Initialize metricComboBox with series names (this will be our single filter)
        metricComboBox = new JComboBox<>();
        metricComboBox.addItem("All Series"); // Option to select all series
        seriesNames.forEach(metricComboBox::addItem);
        metricComboBox.setSelectedItem("GDP per capita (constant 2005 US$)");

        // Initialize buttons
        JButton applyFilterButton = new JButton("Apply Filter");
        JButton clearFilterButton = new JButton("Clear Filter");

        // Add components to the top filter panel
        topFilterPanel.add(new JLabel("Select Metric: "));
        topFilterPanel.add(metricComboBox);

        topFilterPanel.add(applyFilterButton);
        topFilterPanel.add(clearFilterButton);

        add(topFilterPanel, BorderLayout.NORTH);

        // Initialize country selection list within a fixed height scroll pane
        JPanel countryFilterPanel = new JPanel();
        countryFilterPanel.setLayout(new BorderLayout());
        countryFilterPanel.setBorder(BorderFactory.createTitledBorder(
                "Select Countries for Comparison\n(Use Ctrl or Shift for multiple selections)"
        ));

        // Extract unique country names
        Set<String> countryNames = originalData.stream()
                .map(CountryData::getCountryName)
                .collect(Collectors.toSet());

        // Initialize DefaultListModel and JList
        countryListModel = new DefaultListModel<>();
        countryNames.forEach(countryListModel::addElement);
        countryList = new JList<>(countryListModel);
        countryList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        countryList.setVisibleRowCount(10);

        // Add ListSelectionListener to enforce selection limit
        countryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                List<String> selectedCountries = countryList.getSelectedValuesList();
                if (selectedCountries.size() > MAX_COUNTRIES) {
                    // Deselect the last selected country
                    int[] selectedIndices = countryList.getSelectedIndices();
                    int deselectIndex = selectedIndices[selectedIndices.length - 1];
                    countryList.removeSelectionInterval(deselectIndex, deselectIndex);
                    JOptionPane.showMessageDialog(this, "You can select up to " + MAX_COUNTRIES + " countries at a time.", "Selection Limit", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Initialize JScrollPane with a preferred size to limit its height
        JScrollPane countryScrollPane = new JScrollPane(countryList);
        countryScrollPane.setPreferredSize(new Dimension(250, 200));

        // Initialize search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Search Country: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Initialize deselect all button
        JPanel selectionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deselectAllButton = new JButton("Deselect All");
        selectionButtonsPanel.add(deselectAllButton);

        // Add search panel and selection buttons to the countryFilterPanel
        countryFilterPanel.add(searchPanel, BorderLayout.NORTH);
        countryFilterPanel.add(countryScrollPane, BorderLayout.CENTER);
        countryFilterPanel.add(selectionButtonsPanel, BorderLayout.SOUTH);

        add(countryFilterPanel, BorderLayout.CENTER);

        // Add action listeners
        applyFilterButton.addActionListener(this::applyFilter);
        clearFilterButton.addActionListener(this::clearFilter);
        searchButton.addActionListener(e -> searchCountry(searchField.getText()));
        deselectAllButton.addActionListener(e -> countryList.clearSelection());
        metricComboBox.addActionListener(this::changeMetric); // Update when metric changes

        // Add MouseListener for double-click on countryList
        countryList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Detect double-click
                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    e.consume();
                    int index = countryList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        countryListModel.getElementAt(index);
                        // Clear existing selections and select the double-clicked country
                        countryList.clearSelection();
                        countryList.setSelectedIndex(index);
                        // Apply filter
                        filterData();
                    }
                }
            }
        });
    }

    // Applies the selected filters to the data and updates UI components
    private void applyFilter(ActionEvent e) {
        filterData();
    }

    // Clears all applied filters and resets UI components to show all data.
    private void clearFilter(ActionEvent e) {
        metricComboBox.setSelectedItem("All Series");
        countryList.clearSelection();

        List<CountryData> allData = originalData;
        String selectedMetric = (String) metricComboBox.getSelectedItem();

        // Update TablePanel
        tablePanel.updateTableData(allData);

        // Update StatsPanel
        statsPanel.updateStats(allData, selectedMetric);

        // Update ChartPanelCustom
        chartPanel.updateChart(allData, selectedMetric);
    }

    /**
     * Filters the data based on selected metric and countries,
     * and updates the UI components accordingly.
     */
    private void filterData() {
        String selectedMetric = (String) metricComboBox.getSelectedItem();
        List<CountryData> filteredData;

        // Filter by selected metric
        assert selectedMetric != null;
        if (selectedMetric.equals("All Series")) {
            filteredData = originalData;
        } else {
            filteredData = originalData.stream()
                    .filter(data -> data.getSeriesName().equals(selectedMetric))
                    .collect(Collectors.toList());
        }

        // Further filter by selected countries
        List<String> selectedCountries = countryList.getSelectedValuesList();
        if (!selectedCountries.isEmpty()) {
            filteredData = filteredData.stream()
                    .filter(data -> selectedCountries.contains(data.getCountryName()))
                    .collect(Collectors.toList());
        }

        // Provide feedback if no data is available
        if (filteredData.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data available for the selected criteria.", "No Data", JOptionPane.INFORMATION_MESSAGE);
        }

        // Debug: Print number of filtered entries
        System.out.println("Filtered Data Size: " + filteredData.size());

        // Update TablePanel
        tablePanel.updateTableData(filteredData);

        // Update StatsPanel
        statsPanel.updateStats(filteredData, selectedMetric);

        // Update ChartPanelCustom
        chartPanel.updateChart(filteredData, selectedMetric);
    }

    // Updates when the metric selection changes
    private void changeMetric(ActionEvent e) {
        filterData();
    }

    // Searches and selects a country based on user input.
    private void searchCountry(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a country name to search.", "Search Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String normalizedSearch = searchText.trim();
        int index = countryListModel.indexOf(normalizedSearch);

        if (index >= 0) {
            countryList.setSelectedIndex(index);
            countryList.ensureIndexIsVisible(index);
            filterData();
        } else {
            JOptionPane.showMessageDialog(this, "Country not found: " + normalizedSearch, "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
