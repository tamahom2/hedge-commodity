package strategy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import pricing.VanillaOptions;
import request.ForwardCurve.OptionData;
import simulation.Scenario;

public abstract class Strategy {
	
	
	protected double putPercent = 0.0;
	protected double callPercent = 0.0;
	protected int sizeCall = 0;
	protected int sizePut = 0;
	protected double expiration = 0.0;
	protected double strike = 0.0;
	
	
	public Strategy() {}
	
	public Strategy(double putPercent, double callPercent) {
		this.putPercent = putPercent;
		this.callPercent = callPercent;
	}
	
	public Strategy(double putPercent, double callPercent, double strike, double expiration) {
		this.putPercent = putPercent;
		this.callPercent = callPercent;
		this.expiration = expiration;
		this.strike = strike;
	}
	
	
	
	public double optionStrategy(double S, double K, double T, double r, double b, double sigma, double spread) {
		double call = VanillaOptions.generalBlackScholes(S, K*(1+spread), T, r, b, sigma, "c");
		double put = VanillaOptions.generalBlackScholes(S, K*(1-spread), T, r, b, sigma, "p");
		
		return this.callPercent*call+this.putPercent*put;
		
	}
	
	public double optionStrategy(double S, double r, double b, double sigma, double spread) {
		double call = VanillaOptions.generalBlackScholes(S, this.strike*(1+spread), this.expiration, r, b, sigma, "c");
		double put = VanillaOptions.generalBlackScholes(S, this.strike*(1-spread), this.expiration, r, b, sigma, "p");
		
		return this.sizeCall*call + this.sizePut * put;
	}
	
	
	
	public double getPutPrice(double S, double K, double T, double r, double b, double sigma, double spread) {
		return VanillaOptions.generalBlackScholes(S, K*(1-spread), T, r, b, sigma, "p");
	}
	
	public double getCallPrice(double S, double K, double T, double r, double b, double sigma, double spread) {
		return VanillaOptions.generalBlackScholes(S, K*(1+spread), T, r, b, sigma, "c");
	}
	
	public double getPutPercent() {
		return putPercent;
	}
	
	public double getCallPercent() {
		return callPercent;
	}
	
	public static long daysBetween(String yyyymmdd) {
        // Define the date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        // Parse the input date
        LocalDate inputDate = LocalDate.parse(yyyymmdd, formatter);

        // Get today's date
        LocalDate today = LocalDate.now();

        // Calculate the number of days between the input date and today
        long daysBetween = ChronoUnit.DAYS.between(today, inputDate);

        return daysBetween;
    }
	
	public static long daysBetweenTwoDates(String startDateStr, String endDateStr) {
        // Define the date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        // Parse the input dates
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);

        // Calculate the number of days between the start date and end date
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        return daysBetween;
    }
	
	public abstract double pricingStrategy(double S, double K, double T, double r, double b, double sigma, double spread);
	
	public abstract double premium(double S, double K, double T, double r, double b, double sigma, double spread);
	
	public abstract double payoff(double S, double X, double spread);
	
	public double hedgedCost(double S, double X,double eprms, double pdd, double volume, double r, double T,double spread) {
		double discountFactor = Math.exp(-r*T);
		return ((eprms+pdd-this.getPutPercent()*Math.max(X*(1-spread)-S,0)-this.getCallPercent()*Math.max(S-X*(1+spread), 0)))*volume*discountFactor;
	}
	
	
	public abstract void backtest(double EPRMS, double pdd, double spread, double r, double vol, double[] maturities, double[] averageMonthly, double[] hedgedMonthlyVolume, double[] price);
	public abstract double simulate(double S0, Scenario scenario, double[] hedgedMonthlyVolume, List<OptionData> forwardCurve, double vol);
	public abstract double simulateOneVolume(double S0, double strike, double r, int duration, Scenario scenario, double hedgedVolume, List<OptionData> forwardCurve, double vol);

}
