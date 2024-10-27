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
    }

    // Updates the details area with information from the selected CountryData.
    public void updateDetails(CountryData data) {
        StringBuilder sb = new StringBuilder();

        sb.append("Country Name: ").append(data.getCountryName()).append("\n");
        sb.append("Country Code: ").append(data.getCountryCode()).append("\n");
        sb.append("Series Name: ").append(data.getSeriesName()).append("\n");
        sb.append("Series Code: ").append(data.getSeriesCode()).append("\n\n");
        sb.append("Yearly Data:\n");

        for (Map.Entry<Integer, Double> entry : data.getYearlyData().entrySet()) {
            sb.append("  ").append(entry.getKey()).append(": ");
            if (entry.getValue() != null) {
                sb.append(entry.getValue());
            } else {
                sb.append("N/A");
            }
            sb.append("\n");
        }

        detailsArea.setText(sb.toString());
        detailsArea.setCaretPosition(0);
    }
}
