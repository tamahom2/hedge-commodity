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
    	List<OptionData> options = ForwardCurve.forwardRequest("CME:6M");
    	double S0 = options.get(0).close;
    	double totalVolume = 10000000.0;
    	double[] monthlyHedgedVolume = new double[24];
    	for(int i=0;i<24;i++) {
    		monthlyHedgedVolume[i] = totalVolume/24.0;
    	}
    	
    	Scenario low = new Scenario("low", 30, 60, 0.4);
    	Scenario medium = new Scenario("medium", 60, 80, 0.2);
    	Scenario high = new Scenario("high", 0.08, 0.1, 0.4);

    	Strategies strats = new Strategies();
    	Put putStrat = new Put(0);
    	Call callStrat = new Call(1);
    	/*
    	strats.addStrategy(callStrat);
    	strats.addStrategy(putStrat);
    	double withoutHedge = 1000000.0*medium.getPrice();
    	double withHedge = strats.simulate(S0, medium, monthlyHedgedVolume, options, 0.3);
    	System.out.println("Without Hedge : " + withoutHedge);
    	System.out.println("With Hedge : " + withHedge);
    	System.out.println("PnL : " +(((withoutHedge-withHedge)/withoutHedge)*100));
    	*/
    	int duration = 10;
    	double withoutHedge = totalVolume*high.getPrice();
    	double withHedge = callStrat.simulateOneVolume(S0, options.get(duration).close, 0.02, duration, high, totalVolume, options, 0.2);
    	System.out.println("Without Hedge : " + withoutHedge);
    	System.out.println("With Hedge : " + withHedge);
    	System.out.println("PnL : " +(((withoutHedge-withHedge)/withoutHedge)*100));
    	
    }
}
