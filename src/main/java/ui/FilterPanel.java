package main.java.ui;

import main.java.model.CountryData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * FilterPanel allows users to filter data based on specific criteria,
 * including selecting multiple countries.
 */
public class FilterPanel extends JPanel {
    private final JComboBox<String> seriesComboBox;

    private final JList<String> countryList;
    private final DefaultListModel<String> countryListModel;

    private final List<CountryData> originalData;
    private final TablePanel tablePanel;
    private final StatsPanel statsPanel;
    private final ChartPanelCustom chartPanel;
    // Constructor
    public FilterPanel(List<CountryData> dataList, TablePanel tablePanel, StatsPanel statsPanel, ChartPanelCustom chartPanel) {
        this.originalData = dataList;
        this.tablePanel = tablePanel;
        this.statsPanel = statsPanel;
        this.chartPanel = chartPanel;

        setLayout(new BorderLayout());

        // Initialize top filter components (Series selection and filter buttons)
        JPanel topFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Extract unique series names for filtering
        Set<String> seriesNames = originalData.stream()
                .map(CountryData::getSeriesName)
                .collect(Collectors.toSet());

        // Initialize JComboBox with series names
        seriesComboBox = new JComboBox<>();
        seriesComboBox.addItem("All Series");
        seriesNames.forEach(seriesComboBox::addItem);

        // Initialize buttons
        JButton applyFilterButton = new JButton("Apply Filter");
        JButton clearFilterButton = new JButton("Clear Filter");

        // Add components to the top filter panel
        topFilterPanel.add(new JLabel("Filter by Series: "));
        topFilterPanel.add(seriesComboBox);
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
        countryList.setVisibleRowCount(10); // Adjust as needed

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

        // Initialize select/deselect all buttons
        JPanel selectionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton selectAllButton = new JButton("Select All");
        JButton deselectAllButton = new JButton("Deselect All");
        selectionButtonsPanel.add(selectAllButton);
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
        selectAllButton.addActionListener(e -> selectAllCountries());
        deselectAllButton.addActionListener(e -> countryList.clearSelection());

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
        seriesComboBox.setSelectedItem("All Series");
        countryList.clearSelection();

        List<CountryData> allData = originalData;

        // Update TablePanel
        tablePanel.updateTableData(allData);

        // Update StatsPanel
        statsPanel.updateStats(allData);

        // Update ChartPanelCustom
        chartPanel.updateChart(allData);
    }

    /**
     * Filters the data based on selected series and countries,
     * and updates the UI components accordingly.
     */
    private void filterData() {
        String selectedSeries = (String) seriesComboBox.getSelectedItem();
        List<CountryData> filteredData;

        // Filter by series
        assert selectedSeries != null;
        if (selectedSeries.equals("All Series")) {
            filteredData = originalData;
        } else {
            filteredData = originalData.stream()
                    .filter(data -> data.getSeriesName().equals(selectedSeries))
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
        statsPanel.updateStats(filteredData);

        // Update ChartPanelCustom
        chartPanel.updateChart(filteredData);
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

    // Selects all countries in the list.
    private void selectAllCountries() {
        int start = 0;
        int end = countryListModel.getSize() - 1;
        if (end >= 0) {
            countryList.setSelectionInterval(start, end);
        }
    }
}
