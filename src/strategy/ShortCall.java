package strategy;

import java.util.List;

import request.ForwardCurve.OptionData;
import simulation.Scenario;

public class ShortCall extends Strategy {
	public ShortCall(double callPercent, double strike, double expiration) {
		super(0, callPercent, strike, expiration);
		
	}

	@Override
	public double pricingStrategy(double S, double K, double T, double r, double b, double sigma, double spread) {
		return optionStrategy(S,r,b,sigma,spread);
	}

	@Override
	public void backtest(double EPRMS, double pdd, double spread, double r, double vol, double[] maturities,
			double[] averageMonthly, double[] hedgedMonthlyVolume, double[] price) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public double payoff(double S, double X, double spread) {
		return Math.max(0, S-X*(1+spread));
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