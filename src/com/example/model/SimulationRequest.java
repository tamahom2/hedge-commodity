package com.example.model;

import java.util.List;

public class SimulationRequest {
    private double initialPrice;
    private double interestRate;
    private ScenarioData scenario;

    private List<MonthlyStrategy> monthlyStrategies;
	public double getInitialPrice() {
		return initialPrice;
	}
	public void setInitialPrice(double initialPrice) {
		this.initialPrice = initialPrice;
	}
	public double getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}
	public List<MonthlyStrategy> getMonthlyStrategies() {
		return monthlyStrategies;
	}
	public void setMonthlyStrategies(List<MonthlyStrategy> monthlyStrategies) {
		this.monthlyStrategies = monthlyStrategies;
	}
	public ScenarioData getScenario() {
		return scenario;
	}
	public void setScenario(ScenarioData scenario) {
		this.scenario = scenario;
	}

    // Getters and setters
}