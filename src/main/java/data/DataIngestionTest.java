// File: src/main/java/data/DataIngestionTest.java
package main.java.data;

import main.java.model.CountryData;
import com.opencsv.exceptions.CsvValidationException;

import java.util.List;

public class DataIngestionTest {
    public static void main(String[] args) {
        String csvFilePath = "src/main/resources/data.csv"; // Adjust this path if necessary

        CSVReader csvReader = new CSVReader(csvFilePath);
        List<CountryData> dataList = null;
        try {
            dataList = csvReader.parse();
        } catch (Exception e) { // Since we handled exceptions inside parse, this might not be needed
            e.printStackTrace();
            System.exit(1);
        }

        // Print the number of records parsed
        System.out.println("Total Records Parsed: " + dataList.size());

        // Print first 5 records for inspection
        for (int i = 0; i < Math.min(1000, dataList.size()); i++) {
            System.out.println(dataList.get(i));
        }

        // Additional Analysis: Count records with missing GDP per capita in 2016
        long missingGDP2016 = dataList.stream()
                .filter(data -> data.getSeriesName().equals("GDP per capita (constant 2005 US$)"))
                .filter(data -> data.getYearlyData().get(2016) == null)
                .count();

        System.out.println("Number of 'GDP per capita' records with missing 2016 data: " + missingGDP2016);

        // Example: Calculate average GDP per capita in 2016
        double averageGDP2016 = dataList.stream()
                .filter(data -> data.getSeriesName().equals("GDP per capita (constant 2005 US$)"))
                .map(data -> data.getYearlyData().get(2016))
                .filter(value -> value != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(Double.NaN);

        System.out.println(String.format("Average GDP per Capita in 2016: %.2f", averageGDP2016));
    }
}
