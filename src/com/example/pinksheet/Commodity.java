package com.example.pinksheet;

import java.util.ArrayList;
import java.util.List;

public class Commodity {
	private String commodity;
    private List<String> months;
    private List<Double> prices;
    private String unit;

    public Commodity(String commodity, String unit) {
        this.commodity = commodity;
        this.unit = unit;
        this.months = new ArrayList<>();
        this.prices = new ArrayList<>();
    }

    public void addMonthlyPrice(String month, double price) {
        this.months.add(month);
        this.prices.add(price);
    }

    public String getCommodity() {
        return commodity;
    }

    public List<String> getMonths() {
        return months;
    }

    public List<Double> getPrices() {
        return prices;
    }

    public String getUnit() {
        return unit;
    }

    public List<Double> getPricesInMetricTon() {
        List<Double> pricesInMetricTon = new ArrayList<>();
        for (double price : prices) {
            pricesInMetricTon.add(convertToMetricTon(price));
        }
        return pricesInMetricTon;
    }

    private double convertToMetricTon(double price) {
        // Add conversion logic here based on the unit
        // For now, returning the price as is
        return price; // Placeholder
    }
}
