package com.example.model;

public class OptionPriceRequest {
    private String type;
    private double strikePrice;
    private int expiration;
    private double impliedVolatility;
    private double spotPrice;
    private double interestRate;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public double getImpliedVolatility() {
		return impliedVolatility;
	}
	public void setImpliedVolatility(double impliedVolatility) {
		this.impliedVolatility = impliedVolatility;
	}
	public double getSpotPrice() {
		return spotPrice;
	}
	public void setSpotPrice(double spotPrice) {
		this.spotPrice = spotPrice;
	}
	public double getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}

    // Getters and setters
}