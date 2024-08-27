package com.example.strategy;

import java.util.List;

import com.example.pricing.VanillaOptions;
import com.example.request.ForwardCurve.OptionData;
import com.example.simulation.Scenario;

public class Swap extends Strategy{
	
	private double swapRate;
	
	public Swap(double putPercent, double callPercent, double swapRate) {
		super(putPercent,callPercent);
		this.swapRate = swapRate;
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
		double totalHedgedCost = 0.0;
		double totalCost = 0.0;
		for(int i=0;i<cost.length;i++) {
			hedgedCost[i] = this.hedgedCost(price[i], EPRMS, EPRMS, pdd, hedgedMonthlyVolume[i] , r, maturities[i]/365.25, spread);
			cost[i] = (pdd+price[i])*hedgedMonthlyVolume[i]* Math.exp(-r*maturities[i]/365.25);
			System.out.println("Total Hedged Cost for month "+ i +" : "+ hedgedCost[i]);
			System.out.println("Total Cost for month "+ i +" without Hedging : "+ cost[i]);

			totalHedgedCost += hedgedCost[i];
			totalCost += cost[i];
		}
		
		System.out.println("Total Hedged Cost : "+ totalHedgedCost);
		System.out.println("Total Cost without Hedging : "+ totalCost);
		double pnl = totalCost - totalHedgedCost;
		System.out.println("P&L : " + pnl);
		System.out.println("Cost reduction : " + 100*(pnl/totalCost));
	}

	@Override
	public double payoff(double S, double X, double spread) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double simulate(double S0, Scenario scenario, double[] hedgedMonthlyVolume, List<OptionData> forwardCurve, double vol) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double premium(double S, double K, double T, double r, double b, double sigma, double spread) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public double[] simulateOneVolume(double S0, double r, int duration, Scenario scenario, double hedgedVolume,
			List<OptionData> forwardCurve, double vol) {
		// TODO Auto-generated method stub
		return null;
	}
		
}
