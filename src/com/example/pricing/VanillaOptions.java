package com.example.pricing;

import java.util.Random;
import org.apache.commons.math3.distribution.NormalDistribution;

public class VanillaOptions {

	    private static NormalDistribution norm = new NormalDistribution();

	    public static double blackScholes(double S0, double K, double r, double T, double sigma, String flag) {
	        double d1 = (Math.log(S0 / K) + (r + 0.5 * sigma * sigma) * T) / (sigma * Math.sqrt(T));
	        double d2 = d1 - sigma * Math.sqrt(T);

	        if (flag.equals("c")) {  // Call option
	            return (S0 * norm.cumulativeProbability(d1)) - (K * Math.exp(-r * T) * norm.cumulativeProbability(d2));
	        } else if (flag.equals("p")) {  // Put option
	            return (K * Math.exp(-r * T) * norm.cumulativeProbability(-d2)) - (S0 * norm.cumulativeProbability(-d1));
	        } else {
	            throw new IllegalArgumentException("Invalid flag, use 'c' for call and 'p' for put");
	        }
	    }

	    public static double generalBlackScholes(double S0, double K, double T, double r, double b, double sigma, String flag) {
	        double d1 = (Math.log(S0 / K) + (b + 0.5 * sigma * sigma) * T) / (sigma * Math.sqrt(T));
	        double d2 = d1 - sigma * Math.sqrt(T);

	        if (flag.equals("c")) {  // Call option
	            return (S0 * Math.exp((b - r) * T) * norm.cumulativeProbability(d1)) - (K * Math.exp(-r * T) * norm.cumulativeProbability(d2));
	        } else if (flag.equals("p")) {  // Put option
	            return (K * Math.exp(-r * T) * norm.cumulativeProbability(-d2)) - (S0 * Math.exp((b - r) * T) * norm.cumulativeProbability(-d1));
	        } else {
	            throw new IllegalArgumentException("Invalid flag, use 'c' for call and 'p' for put");
	        }
	    }

	    public static double monteCarloPrice(double S0, double K, double r, double T, double sigma, String flag, int numSimulations, int numSteps) {
	        double dt = T / numSteps;
	        double[] prices = new double[numSimulations];
	        Random rand = new Random();

	        for (int i = 0; i < numSimulations; i++) {
	            double price = S0;
	            for (int t = 0; t < numSteps; t++) {
	                double Z = rand.nextGaussian();
	                price *= Math.exp((r - 0.5 * sigma * sigma) * dt + sigma * Math.sqrt(dt) * Z);
	            }
	            prices[i] = price;
	        }

	        double payoffSum = 0.0;
	        for (double price : prices) {
	            if (flag.equals("c")) {  // Call option
	                payoffSum += Math.max(price - K, 0);
	            } else if (flag.equals("p")) {  // Put option
	                payoffSum += Math.max(K - price, 0);
	            } else {
	                throw new IllegalArgumentException("Invalid flag, use 'c' for call and 'p' for put");
	            }
	        }

	        return Math.exp(-r * T) * payoffSum / numSimulations;
	    }

	    public static double black76(double S0, double K, double r, double T, double sigma, String flag) {
	        double d1 = (Math.log(S0 / K) + (r + 0.5 * sigma * sigma) * T) / (sigma * Math.sqrt(T));
	        double d2 = d1 - sigma * Math.sqrt(T);

	        if (flag.equals("c")) {  // Call option
	            return Math.exp(-r * T) * (S0 * norm.cumulativeProbability(d1) - K * norm.cumulativeProbability(d2));
	        } else if (flag.equals("p")) {  // Put option
	            return Math.exp(-r * T) * (K * norm.cumulativeProbability(-d2) - S0 * norm.cumulativeProbability(-d1));
	        } else {
	            throw new IllegalArgumentException("Invalid flag, use 'c' for call and 'p' for put");
	        }
	    }
	    
	    
	    public static void main(String[] args) {

	    	String flag = "c";
	    	double S = 75;
	    	double X = 80;
			double T = 30/365.25;
			double r = 0.02;
			double b = 0.0;
			double v = 0.0;
	        System.out.println("BS : " + generalBlackScholes(S,X,T,r,b,v,flag));
	        
	        /**
	        System.out.println("Double Barrier: " + doubleBarrier("pi", S, X, L, U, T, r, b, v, delta1, delta2));
	        S = 149.0;
	        X = 150.0;
	        T = 100/365.25;
	        double mean = 0.0;
	        v = 0.01;
	        L = 135.0;
	        U = 165.0;
	        System.out.println("Double Barrier Monte Carlo : " + monteCarloDoubleBarrier("pi", S, X, L, U, T, mean , v, 10000, 100));
	    	**/
	    }
}

