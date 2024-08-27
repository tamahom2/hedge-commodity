package model;

public class SimulationRequest {
    private String strategyType;
    private double strikePrice;
    private int expiration;
    private double interestRate;
    private double volatility;
    private double initialPrice;
    private double[] hedgedVolumes;
    private double putPercentage;
    private double callPercentage;
	public String getStrategyType() {
		return strategyType;
	}
	public void setStrategyType(String strategyType) {
		this.strategyType = strategyType;
	}
	public double getStrikePrice() {
		return strikePrice;
	}
	public void setStrikePrice(double strikePrice) {
		this.strikePrice = strikePrice;
	}
	public int getExpiration() {
		return expiration;
	}
	public void setExpiration(int expiration) {
		this.expiration = expiration;
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
	public double getInitialPrice() {
		return initialPrice;
	}
	public void setInitialPrice(double initialPrice) {
		this.initialPrice = initialPrice;
	}
	public double[] getHedgedVolumes() {
		return hedgedVolumes;
	}
	public void setHedgedVolumes(double[] hedgedVolumes) {
		this.hedgedVolumes = hedgedVolumes;
	}
	public double getPutPercentage() {
		return putPercentage;
	}
	public void setPutPercentage(double putPercentage) {
		this.putPercentage = putPercentage;
	}
	public double getCallPercentage() {
		return callPercentage;
	}
	public void setCallPercentage(double callPercentage) {
		this.callPercentage = callPercentage;
	}

    // Add getters and setters for all fields
}
