package main.java.ui;

import main.java.model.CountryData;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

// TablePanel displays CountryData in a JTable.
public class TablePanel extends JPanel {
    private final JTable table;
    private final CountryTableModel tableModel;

    // Constructor initializes the table with the provided data.
    public TablePanel(List<CountryData> dataList) {
        super(new BorderLayout());

        // Initialize table model
        tableModel = new CountryTableModel(dataList);

        // Initialize JTable with the table model
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true); // Enable sorting

        // Initialize row sorter for filtering
        TableRowSorter<CountryTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Customize table appearance (optional)
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Updates the table data and refreshes the view.
    public void updateTableData(List<CountryData> newDataList) {
        tableModel.setDataList(newDataList);
        tableModel.fireTableDataChanged();
    }

    // Returns the JTable instance.
    public JTable getTable() {
        return table;
    }

    // Returns the custom table model.
    public CountryTableModel getTableModel() {
        return tableModel;
    }

    // Custom table model for CountryData.
    public static class CountryTableModel extends AbstractTableModel {
        private final String[] columnNames = {
                "Country Name", "Series Name",
                "2006", "2007", "2008", "2009", "2010",
                "2011", "2012", "2013", "2014", "2015", "2016"
        };
        private List<CountryData> dataList;

        // Constructor initializes the data list.
        public CountryTableModel(List<CountryData> dataList) {
            this.dataList = dataList;
        }

        // Sets a new data list.
        public void setDataList(List<CountryData> dataList) {
            this.dataList = dataList;
        }

        @Override
        public int getRowCount() {
            return dataList.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex >= 2 && columnIndex <= 8) {
                return Double.class;
            }
            return String.class;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            CountryData data = dataList.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> data.getCountryName();
                case 1 -> data.getSeriesName();
                case 2 -> data.getYearlyData().get(2006);
                case 3 -> data.getYearlyData().get(2007);
                case 4 -> data.getYearlyData().get(2008);
                case 5 -> data.getYearlyData().get(2009);
                case 6 -> data.getYearlyData().get(2010);
                case 7 -> data.getYearlyData().get(2011);
                case 8 -> data.getYearlyData().get(2012);
                case 9 -> data.getYearlyData().get(2013);
                case 10 -> data.getYearlyData().get(2014);
                case 11 -> data.getYearlyData().get(2015);
                case 12 -> data.getYearlyData().get(2016);
                default -> null;
            };
        }

        // Retrieves the CountryData object at the specified row.
        public CountryData getCountryDataAt(int row) {
            return dataList.get(row);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false; // Make table non-editable
        }
    }
}
