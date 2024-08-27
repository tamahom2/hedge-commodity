package com.example.strategy;

import java.util.List;

import com.example.pricing.VanillaOptions;
import com.example.request.ForwardCurve.OptionData;
import com.example.simulation.Scenario;

public class Call extends Strategy {

	
	private double swapRate;
	
	public Call(double callPercent, double swapRate) {
		super(0.0,callPercent);
		this.swapRate = swapRate;
	}
	
	public Call(double callPercent) {
		super(0.0,callPercent);
	}
	
	public Call(double callPercent, double strike, double expiry, double volatility) {
		super(0, callPercent, strike, expiry, volatility);
	}
	
	
	public double pricingStrategy(double S, double K, double T, double r, double b, double sigma, double spread) {
		
		double put = VanillaOptions.generalBlackScholes(S, K*(1-spread), T, r, b, sigma, "p");

		return K - this.swapRate - this.getPutPercent()*put;
	}

	@Override
	public void backtest(double EPRMS, double pdd, double spread, double r, double vol, double[] maturities,
			double[] averageMonthly, double[] hedgedMonthlyVolume, double[] price) {
		double totalPrem = 0.0;
		double[] pricingStrat = new double[maturities.length];
		for(int i=0;i<maturities.length;i++) {
			System.out.println("Call : " + this.getCallPrice(averageMonthly[i], EPRMS, maturities[i]/365.25, r, 0.0, vol, spread));
			System.out.println("Put : " + this.getPutPrice(averageMonthly[i], EPRMS, maturities[i]/365.25, r, 0.0, vol, spread));
			System.out.println("Option Strategy : " + this.optionStrategy(averageMonthly[i], EPRMS, maturities[i]/365.25, r, 0.0, vol, spread));
			pricingStrat[i] = this.pricingStrategy(averageMonthly[i], EPRMS, maturities[i]/365.25, r, 0.0, vol, spread);
			System.out.println("Pricing Strategy : " + pricingStrat[i]);
			totalPrem += pricingStrat[i];
		}
		System.out.println("Total Premium is : " + totalPrem);
		double[] hedgedCost = new double[maturities.length];
		double[] cost = new double[maturities.length];
		double[] premium = new double[cost.length];
		double totalHedgedCost = 0.0;
		double totalCost = 0.0;
		double totalPremium = 0.0;
		for(int i=0;i<cost.length;i++) {
			hedgedCost[i] = this.hedgedCost(price[i], EPRMS, EPRMS, pdd, hedgedMonthlyVolume[i] , r, maturities[i]/365.25, spread);
			cost[i] = (pdd+price[i])*hedgedMonthlyVolume[i]* Math.exp(-r*maturities[i]/365.25);
			//Premium in case of Put
			premium[i] = -pricingStrat[i]*hedgedMonthlyVolume[i];
			System.out.println("Total Hedged Cost for month "+ i +" : "+ hedgedCost[i]);
			System.out.println("Total Cost for month "+ i +" without Hedging : "+ cost[i]);
			System.out.println("Total Premium for month "+ i +" : "+ premium[i]);

			totalHedgedCost += hedgedCost[i];
			totalCost += cost[i];
			totalPremium += premium[i];
		}
		
		System.out.println("Total Hedged Cost : "+ totalHedgedCost);
		System.out.println("Total Cost without Hedging : "+ totalCost);
		double pnl = totalCost - totalHedgedCost - totalPremium;
		System.out.println("Total premium cost in % : " + (totalPremium/totalCost)*100);
		System.out.println("P&L : " + pnl);
		System.out.println("Cost reduction : " + 100*(pnl/totalCost));
	}

	@Override
	public double payoff(double S, double X, double spread) {
		// TODO Auto-generated method stub
		return Math.max(S-X*(1+spread), 0);
	}

	@Override
	public double simulate(double S0, Scenario scenario, double[] hedgedMonthlyVolume, List<OptionData> forwardCurve, double vol) {
		double totalWithHedging = 0.0;
		double totalPremium = 0.0;
		double price = scenario.getPrice();
		for(int i=0;i<hedgedMonthlyVolume.length;i++) {
			double expiry = daysBetween(forwardCurve.get(i+1).expiration)/365.25;
			double currentPremium = hedgedMonthlyVolume[i]*premium(forwardCurve.get(i+1).close,forwardCurve.get(i+1).close, expiry,0,0, vol, 0);
			totalPremium += currentPremium;
			System.out.println("Premium in month " + (i+1) + " is : " + currentPremium);
			totalWithHedging += hedgedMonthlyVolume[i]*(price - payoff(price, forwardCurve.get(i+1).close, 0));
		}
		return totalWithHedging+totalPremium;
	}

	@Override
	public double premium(double S, double K, double T, double r, double b, double sigma, double spread) {
		return this.callPercent*VanillaOptions.generalBlackScholes(S, K*(1+spread), T, r, b, sigma, "c");
	}

	@Override
    public double[] simulateOneVolume(double S0, double r, int duration, Scenario scenario, double hedgedVolume, List<OptionData> forwardCurve, double vol) {
        double price = scenario.getPrice();
        double payoff = payoff(price, this.strike, 0);
        double hedgedCost = hedgedVolume * (price - payoff);
        double premium = premium(S0, this.strike, this.expiration, r, 0, vol, 0) * hedgedVolume;
        double[] res = new double[2];
        res[0] = hedgedCost + premium;
        res[1] = premium;
        return res;
    }

	public double getStrike() {
		return this.strike;
	}

	public double getExpiration() {
		return this.expiration;
	}

	public double getVolatility() {
		return this.volatility;
	}

	
	
}
