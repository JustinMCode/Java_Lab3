package main.java;

import main.java.ui.MainFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Ensure GUI creation is done on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            String csvFilePath = "src/main/resources/data.csv";
            new MainFrame(csvFilePath);
        });
    }
}
