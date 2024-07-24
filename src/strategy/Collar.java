package strategy;

import java.util.List;

import request.ForwardCurve.OptionData;
import simulation.Scenario;

public class Collar extends Strategy {
	
	public Collar(double putPercent, double callPercent) {
		super(putPercent,callPercent);
	}
	
	public double pricingStrategy(double S, double K, double T, double r, double b, double sigma, double spread) {
		
		double optionStrat = optionStrategy(S,K,T,r,b,sigma,spread);
		double discountFactor = Math.exp(-r*T);
		return (S-K)*discountFactor + optionStrat;
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
		return putPercent*Math.max(X*(1-spread)-S, 0) + callPercent*Math.max(S-X*(1+spread), 0);
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
}
