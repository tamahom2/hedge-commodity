package strategy;

import java.util.List;

import pricing.BarrierOptions;
import request.ForwardCurve.OptionData;
import simulation.Scenario;

public class PutBarrier extends Strategy {
	
	private double barrier;
	private String type;
	
	private boolean barrierTouched = false;
	
	public PutBarrier(double putPercent, double strike, double expiration, double barrier, String type) {
		super(putPercent, 0, strike, expiration);
		this.barrier = barrier;
		this.type = type;
	}
	

	@Override
	public double pricingStrategy(double S, double K, double T, double r, double b, double sigma, double spread) {
		return BarrierOptions.standardBarrier(type, S, this.strike*(1-spread), this.barrier, 0, T, r, b, sigma);
	}

	@Override
	public double payoff(double S, double X, double spread) {
		if(barrierTouched && type.equals("pdi")) {
			return Math.max(X*(1-spread)-S, 0);
		}
		return 0;
	}

	@Override
	public void backtest(double EPRMS, double pdd, double spread, double r, double vol, double[] maturities,
			double[] averageMonthly, double[] hedgedMonthlyVolume, double[] price) {
		// TODO Auto-generated method stub
		
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
	public double simulateOneVolume(double S0, double strike, double r, int duration, Scenario scenario,
			double hedgedVolume, List<OptionData> forwardCurve, double vol) {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
