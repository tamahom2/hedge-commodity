package com.example.model;

public class OptionStrategy {
    private String type;
    private double strikePrice;
    private int expiration;
    private double impliedVolatility;
    private double percentage;
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
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

}