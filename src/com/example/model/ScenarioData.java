package com.example.model;

public class ScenarioData {
	private String name;
    private double lowerBound;
    private double upperBound;
    private double probability;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLowerBound() {
		return lowerBound;
	}
	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}
	public double getUpperBound() {
		return upperBound;
	}
	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}
	public double getProbability() {
		return probability;
	}
	public void setProbability(double probability) {
		this.probability = probability;
	}
}
