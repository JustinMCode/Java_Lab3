package main.java.data;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvValidationException;
import main.java.model.CountryData;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * CSVReader handles the ingestion and parsing of GDP data from a CSV file.
 */
public class CSVReader {
    private final String csvFilePath;

    // Constructor initializes the CSVReader with the file path.
    public CSVReader(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    // Parses the CSV file and returns a list of CountryData objects.
    public List<CountryData> parse() {
        List<CountryData> dataList = new ArrayList<>();

        try (CSVReaderHeaderAware reader = new CSVReaderHeaderAware(new FileReader(csvFilePath))) {
            Map<String, String> values;
            while ((values = reader.readMap()) != null) {
                String countryName = values.get("Country Name");
                String countryCode = values.get("Country Code");
                String seriesName = values.get("Series Name");
                String seriesCode = values.get("Series Code");

                CountryData countryData = new CountryData(countryName, countryCode, seriesName, seriesCode);

                // Iterate through years 1990 to 2016
                for (int year = 1990; year <= 2016; year++) {
                    String key = year + " [YR" + year + "]";
                    String valueStr = values.get(key);
                    Double value = null;

                    if (valueStr != null && !valueStr.equals("..")) {
                        try {
                            value = Double.parseDouble(valueStr);
                        } catch (NumberFormatException e) {
                            // Handle invalid number formats
                            value = null;
                        }
                    }

                    countryData.addYearlyData(year, value);
                }

                dataList.add(countryData);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return dataList;
    }
}
