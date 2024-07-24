package test;

import java.io.IOException;
import java.util.List;

import pricing.VanillaOptions;
import request.ForwardCurve;
import request.ForwardCurve.OptionData;
import simulation.Scenario;
import strategy.Call;
import strategy.Put;
import strategy.Strategies;

public class testCallStrategy {
	
	
	public static void main(String[] args) throws IOException {
    	List<OptionData> options = ForwardCurve.forwardRequest("NYMEX:CL");
    	double S0 = options.get(0).close;
    	double[] monthlyHedgedVolume = new double[24];
    	for(int i=0;i<24;i++) {
    		monthlyHedgedVolume[i] = 1000000.0/24.0;
    	}
    	
    	Scenario low = new Scenario("low", 30, 60, 0.4);
    	Scenario medium = new Scenario("medium", 60, 80, 0.2);
    	Scenario high = new Scenario("high", 80, 120, 0.4);

    	Strategies strats = new Strategies();
    	Put putStrat = new Put(0);
    	Call callStrat = new Call(0.5);
    	strats.addStrategy(callStrat);
    	strats.addStrategy(putStrat);
    	double withoutHedge = 1000000.0*medium.getPrice();
    	double withHedge = strats.simulate(S0, medium, monthlyHedgedVolume, options, 0.3);
    	System.out.println("Without Hedge : " + withoutHedge);
    	System.out.println("With Hedge : " + withHedge);
    	System.out.println("PnL : " +(((withoutHedge-withHedge)/withoutHedge)*100));
    }
}
