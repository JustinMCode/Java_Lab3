package main.java.ui;

import main.java.model.CountryData;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

// DetailsPanel displays detailed information about a selected CountryData entry.
public class DetailsPanel extends JPanel {
    private final JTextArea detailsArea;

    // Constructor initializes the details display area.
    public DetailsPanel() {
        super(new BorderLayout());

        // Initialize JTextArea for displaying details
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);

        // Add JScrollPane for scrollable text area
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        add(scrollPane, BorderLayout.CENTER);

        // Set preferred size
        setPreferredSize(new Dimension(800, 200));
    }

    // Updates the details area with information from the selected CountryData.
    public void updateDetails(CountryData data) {
        if (data == null) {
            detailsArea.setText("No data available.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("Country Name: ").append(data.getCountryName() != null ? data.getCountryName() : "N/A").append("\n");
        sb.append("Country Code: ").append(data.getCountryCode() != null ? data.getCountryCode() : "N/A").append("\n");
        sb.append("Series Name: ").append(data.getSeriesName() != null ? data.getSeriesName() : "N/A").append("\n");
        sb.append("Series Code: ").append(data.getSeriesCode() != null ? data.getSeriesCode() : "N/A").append("\n\n");
        sb.append("Yearly Data:\n");

        Map<Integer, ? extends Number> yearlyData = data.getYearlyData();
        if (yearlyData != null && !yearlyData.isEmpty()) {
            for (Map.Entry<Integer, ? extends Number> entry : yearlyData.entrySet()) {
                sb.append("  ").append(entry.getKey()).append(": ");
                sb.append(entry.getValue() != null ? entry.getValue().toString() : "N/A");
                sb.append("\n");
            }
        } else {
            sb.append("  No yearly data available.\n");
        }

        detailsArea.setText(sb.toString());
        detailsArea.setCaretPosition(0);
    }
}
