package com.example.model;

public class SimulationResult {
    private double totalHedgedCost;
    private double totalPremium;
    private double pnl;
    private double totalUnhedgedCost;

    public SimulationResult(double totalHedgedCost, double totalPremium, double pnl, double totalUnhedgedCost) {
        this.totalHedgedCost = totalHedgedCost;
        this.totalPremium = totalPremium;
        this.pnl = pnl;
        this.totalUnhedgedCost = totalUnhedgedCost;
    }

    // Getters
    public double getTotalHedgedCost() {
        return totalHedgedCost;
    }

    public double getTotalPremium() {
        return totalPremium;
    }

    public double getPnl() {
        return pnl;
    }

    public double getTotalUnhedgedCost() {
        return totalUnhedgedCost;
    }

    // Setters
    public void setTotalHedgedCost(double totalHedgedCost) {
        this.totalHedgedCost = totalHedgedCost;
    }

    public void setTotalPremium(double totalPremium) {
        this.totalPremium = totalPremium;
    }

    public void setPnl(double pnl) {
        this.pnl = pnl;
    }

    public void setTotalUnhedgedCost(double totalUnhedgedCost) {
        this.totalUnhedgedCost = totalUnhedgedCost;
    }

    @Override
    public String toString() {
        return "SimulationResult{" +
                "totalHedgedCost=" + totalHedgedCost +
                ", totalPremium=" + totalPremium +
                ", pnl=" + pnl +
                ", totalUnhedgedCost=" + totalUnhedgedCost +
                '}';
    }
}