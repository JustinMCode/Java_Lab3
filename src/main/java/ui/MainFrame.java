package main.java.ui;

import main.java.data.CSVReader;
import main.java.model.CountryData;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// MainFrame serves as the primary window of the application, integrating all UI components.
public class MainFrame extends JFrame {

    private final TablePanel tablePanel;
    private final DetailsPanel detailsPanel;

    public MainFrame(String csvFilePath) {
        super("Data Visualization Tool");

        // Load data using CSVReader
        CSVReader csvReader = new CSVReader(csvFilePath);
        List<CountryData> dataList = csvReader.parse();

        // Initialize UI components
        tablePanel = new TablePanel(dataList);
        StatsPanel statsPanel = new StatsPanel(dataList);
        ChartPanelCustom chartPanel = new ChartPanelCustom(dataList);
        detailsPanel = new DetailsPanel();
        FilterPanel filterPanel = new FilterPanel(dataList, tablePanel, statsPanel, chartPanel);

        // Set up layout manager
        setLayout(new BorderLayout());

        // Add FilterPanel at the top
        add(filterPanel, BorderLayout.NORTH);

        // Add main split pane to the center
        add(createMainSplitPane(statsPanel, chartPanel), BorderLayout.CENTER);

        // Event Handling: Update DetailsPanel when a table row is selected
        tablePanel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Prevent multiple events
                int selectedRow = tablePanel.getTable().getSelectedRow();
                if (selectedRow >= 0) {
                    // Convert view row index to model row index
                    int modelRow = tablePanel.getTable().convertRowIndexToModel(selectedRow);
                    CountryData selectedData = tablePanel.getTableModel().getCountryDataAt(modelRow);
                    detailsPanel.updateDetails(selectedData);
                } else {
                    // Clear details when no selection
                    detailsPanel.updateDetails(null);
                }
            }
        });

        // Final Frame Settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    // Method to create the main split pane
    private JSplitPane createMainSplitPane(StatsPanel statsPanel, ChartPanelCustom chartPanel) {
        // Adjusted divider location and resize weight for topSplitPane
        JSplitPane topSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        topSplitPane.setTopComponent(tablePanel);
        topSplitPane.setBottomComponent(detailsPanel);
        topSplitPane.setDividerLocation(300);
        topSplitPane.setResizeWeight(0.3);
        topSplitPane.setOneTouchExpandable(true);

        // Adjusted divider location and resize weight for bottomSplitPane
        JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        bottomSplitPane.setLeftComponent(statsPanel);
        bottomSplitPane.setRightComponent(chartPanel);
        bottomSplitPane.setDividerLocation(300);
        bottomSplitPane.setResizeWeight(0.3);
        bottomSplitPane.setOneTouchExpandable(true);

        // Main Split Pane: Top and Bottom
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setTopComponent(topSplitPane);
        mainSplitPane.setBottomComponent(bottomSplitPane);
        mainSplitPane.setDividerLocation(600);
        mainSplitPane.setResizeWeight(0.6);
        mainSplitPane.setOneTouchExpandable(true);

        return mainSplitPane;
    }
}
