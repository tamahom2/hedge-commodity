package com.example.model;

public class BacktestResult {
    

	private double totalHedgedCost;
    private double totalUnhedgedCost;
    private double totalPremium;
    private double pnl;

    public BacktestResult(double totalHedgedCost, double totalUnhedgedCost, double totalPremium, double pnl) {
        this.totalHedgedCost = totalHedgedCost;
        this.totalUnhedgedCost = totalUnhedgedCost;
        this.totalPremium = totalPremium;
        this.pnl = pnl;
    }

    public double getTotalHedgedCost() {
		return totalHedgedCost;
	}

	public void setTotalHedgedCost(double totalHedgedCost) {
		this.totalHedgedCost = totalHedgedCost;
	}

	public double getTotalUnhedgedCost() {
		return totalUnhedgedCost;
	}

	public void setTotalUnhedgedCost(double totalUnhedgedCost) {
		this.totalUnhedgedCost = totalUnhedgedCost;
	}

	public double getTotalPremium() {
		return totalPremium;
	}

	public void setTotalPremium(double totalPremium) {
		this.totalPremium = totalPremium;
	}

	public double getPnl() {
		return pnl;
	}

	public void setPnl(double pnl) {
		this.pnl = pnl;
	}
}