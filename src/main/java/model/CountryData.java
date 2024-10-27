package main.java.model;

import java.util.HashMap;
import java.util.Map;

// CountryData represents GDP-related metrics for a specific country and series.
public class CountryData {
    private final String countryName;
    private final String countryCode;
    private final String seriesName;
    private final String seriesCode;
    private Map<Integer, Double> yearlyData; // Year -> Value

    // Constructor initializes the CountryData object.
    public CountryData(String countryName, String countryCode, String seriesName, String seriesCode) {
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.seriesName = seriesName;
        this.seriesCode = seriesCode;
        this.yearlyData = new HashMap<>();
    }

    // Getters and Setters
    public String getCountryName() {
        return countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public String getSeriesCode() {
        return seriesCode;
    }

    public Map<Integer, Double> getYearlyData() {
        return yearlyData;
    }

    public void setYearlyData(Map<Integer, Double> yearlyData) {
        this.yearlyData = yearlyData;
    }

    // Adds data for a specific year.
    public void addYearlyData(int year, Double value) {
        this.yearlyData.put(year, value);
    }

    @Override
    public String toString() {
        return "CountryData{" +
                "countryName='" + countryName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", seriesName='" + seriesName + '\'' +
                ", seriesCode='" + seriesCode + '\'' +
                ", yearlyData=" + yearlyData +
                '}';
    }
}
