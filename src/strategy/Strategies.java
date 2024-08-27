package strategy;

import java.util.ArrayList;
import java.util.List;

import request.ForwardCurve.OptionData;
import simulation.Scenario;

public class Strategies extends Strategy {
	

	public List<Strategy> strategies;
	
	public Strategies() {
		this.strategies = new ArrayList<>();
	}
	
	public void addStrategy(Strategy strat) {
		strategies.add(strat);
	}
	
	@Override
	public double pricingStrategy(double S, double K, double T, double r, double b, double sigma, double spread) {
		double total = 0.0;
		for(Strategy strat : this.strategies) {
			total += strat.pricingStrategy(S, K, T, r, b, sigma, spread);
		}
		return total;
	}

	@Override
	public void backtest(double EPRMS, double pdd, double spread, double r, double vol, double[] maturities,
			double[] averageMonthly, double[] hedgedMonthlyVolume, double[] price) {
		// TODO Auto-generated method stub

	}

	@Override
	public double payoff(double S, double X, double spread) {
		double total = 0.0;
		for(Strategy strat : this.strategies) {
			total += strat.payoff(S, X, spread);
		}
		return total;
	}

	@Override
	public double simulate(double S0, Scenario scenario, double[] hedgedMonthlyVolume, List<OptionData> forwardCurve, double vol) {
		double totalWithHedging = 0.0;
		double totalPremium = 0.0;
		double price = scenario.getPrice();
		for(int i=0;i<hedgedMonthlyVolume.length;i++) {
			double expiry = daysBetween(forwardCurve.get(i+1).expiration)/365.25;
			double currentPremium = hedgedMonthlyVolume[i]*premium(forwardCurve.get(i+1).close,forwardCurve.get(i+1).close, expiry, 0, 0, vol, 0);
			totalPremium += currentPremium;
			System.out.println("Premium in month " + (i+1) + " is : " + currentPremium);
			totalWithHedging += hedgedMonthlyVolume[i]*(price - payoff(price, forwardCurve.get(i+1).close, 0));
		}
		return totalWithHedging+totalPremium;
	}

	@Override
	public double premium(double S, double K, double T, double r, double b, double sigma, double spread) {
		double total = 0.0;
		for(Strategy strat : this.strategies) {
			total += strat.premium(S, K, T, r, b,sigma,spread);
		}
		return total;
	}

	@Override
	public double simulateOneVolume(double S0, double strike, double r, int duration, Scenario scenario,
			double hedgedVolume, List<OptionData> forwardCurve, double vol) {
		// TODO Auto-generated method stub
		return 0;
	}

}
