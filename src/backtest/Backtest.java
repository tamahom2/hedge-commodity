package backtest;

import strategy.Collar;
import strategy.Put;
import strategy.Swap;

public class Backtest {
	
	public static double[] slopeCalc(double start,double slope, int total) {
		double[] res = new double[total];
		res[0] = start;
		for(int i=1;i<total; i++) {
			res[i] = res[i-1]*(1+slope/total); 
		}
		return res;
	}
	
	public static void main(String[] args) {
		
		double swapRate = 402.3538335;
		Put put100 = new Put(1,swapRate);
		Put put65 = new Put(0.65,swapRate);
		Swap swap = new Swap(0,0,swapRate);
		Collar collar = new Collar(-1,1);
		double EPRMS = 402.353889825086;
		double EPRMS65 = 439.4753299626966; 
		double spread = 0.0;
		double r = 0.02;
		double slope = 0.06;
		double[] maturities = {30.0,58,89,119,150,180,211,242,272,303,333,364};
		double[] averageMonthly = slopeCalc(391.4475,slope,maturities.length);
		double vol = 0.3;
		double[] hedgedVolumeMonthly = new double[maturities.length];
		for(int i=0; i<hedgedVolumeMonthly.length ; i++) {
			hedgedVolumeMonthly[i] = 200000.0/12.0;
		}
		double[] price = {395.413043478261,382.523809523810,380.847826086957,400.181818181818,443.208333333333,449.454545454545,464.5, 449.125,459.0,494.166666666667,439.521739130435,369.454545454545};
		
		double pdd = 25.0;
		System.out.println("BACKTESTING FOR 100% PUT :");
		put100.backtest(EPRMS, pdd, spread, r, vol, maturities, averageMonthly, hedgedVolumeMonthly, price);
		System.out.println("BACKTESTING COMPLETE");
		System.out.println("BACKTESTING FOR 65% PUT :");
		put65.backtest(EPRMS65, pdd, spread, r, vol, maturities, averageMonthly, hedgedVolumeMonthly, price);
		System.out.println("BACKTESTING COMPLETE");
		System.out.println("BACKTESTING FOR 100% SWAP :");
		swap.backtest(EPRMS, pdd, spread, r, vol, maturities, averageMonthly, hedgedVolumeMonthly, price);
		System.out.println("BACKTESTING COMPLETE");
		EPRMS = 404.790370983256;
		spread = 0.1;
		System.out.println("BACKTESTING FOR COLLAR :");
		collar.backtest(EPRMS, pdd, spread, r, vol, maturities, averageMonthly, hedgedVolumeMonthly, price);
		System.out.println("BACKTESTING COMPLETE");

	}
}
