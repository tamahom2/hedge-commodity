package com.example.model;

public class BacktestRequest {
    
	
	private String strategy;
    private double eprms;
    private double pdd;
    private double spread;
    private double interestRate;
    private double volatility;
    private double slope;
    private double initialPrice;
    private double swapRate;
    private double putPercentage;
    private double collarLowerBound;
    private double collarUpperBound;
    private double hedgedVolume;
    private MonthlyData[] monthlyData; 
    
    public BacktestRequest() {}
    
   
    
	public String getStrategy() {
		return strategy;
	}
	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}
	public double getEprms() {
		return eprms;
	}
	public void setEprms(double eprms) {
		this.eprms = eprms;
	}
	public double getPdd() {
		return pdd;
	}
	public void setPdd(double pdd) {
		this.pdd = pdd;
	}
	public double getSpread() {
		return spread;
	}
	public void setSpread(double spread) {
		this.spread = spread;
	}
	public double getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}
	public double getVolatility() {
		return volatility;
	}
	public void setVolatility(double volatility) {
		this.volatility = volatility;
	}
	public double getSlope() {
		return slope;
	}
	public void setSlope(double slope) {
		this.slope = slope;
	}
	public double getInitialPrice() {
		return initialPrice;
	}
	public void setInitialPrice(double initialPrice) {
		this.initialPrice = initialPrice;
	}
	public double getSwapRate() {
		return swapRate;
	}
	public void setSwapRate(double swapRate) {
		this.swapRate = swapRate;
	}
	public double getPutPercentage() {
		return putPercentage;
	}
	public void setPutPercentage(double putPercentage) {
		this.putPercentage = putPercentage;
	}
	public double getCollarLowerBound() {
		return collarLowerBound;
	}
	public void setCollarLowerBound(double collarLowerBound) {
		this.collarLowerBound = collarLowerBound;
	}
	public double getCollarUpperBound() {
		return collarUpperBound;
	}
	public void setCollarUpperBound(double collarUpperBound) {
		this.collarUpperBound = collarUpperBound;
	}
	public double getHedgedVolume() {
		return hedgedVolume;
	}
	public void setHedgedVolume(double hedgedVolume) {
		this.hedgedVolume = hedgedVolume;
	}
	
	public MonthlyData[] getMonthlyData() {
		return monthlyData;
	}



	public void setMonthlyData(MonthlyData[] monthlyData) {
		this.monthlyData = monthlyData;
	}

	public static class MonthlyData {
        private double price;
        private int maturity;

        // Getters and setters

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getMaturity() {
            return maturity;
        }

        public void setMaturity(int maturity) {
            this.maturity = maturity;
        }
    }
}