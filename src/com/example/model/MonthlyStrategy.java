package com.example.model;

import java.util.List;

public class MonthlyStrategy {
    private double volume;
    private List<OptionStrategy> options;
	public double getVolume() {
		return volume;
	}
	public void setVolume(double volume) {
		this.volume = volume;
	}
	public List<OptionStrategy> getOptions() {
		return options;
	}
	public void setOptions(List<OptionStrategy> options) {
		this.options = options;
	}

    // Getters and setters
}