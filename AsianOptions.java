package pricing;

import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Random;


public class AsianOptions {
	public static double GeometricAverageRateOption(String CallPutFlag, double S, double SA, double X, 
            double T, double T2, double r, double b, double v) {
		double t1 = T - T2;
		double bA = 0.5 * (b - Math.pow(v, 2) / 6);
		double vA = v / Math.sqrt(3);
		
		if (t1 > 0) {
			X = (t1 + T2) / T2 * X - t1 / T2 * SA;
			return VanillaOptions.generalBlackScholes(S, X, T2, r, bA, vA,CallPutFlag) * T2 / (t1 + T2);
		} else {
			return VanillaOptions.generalBlackScholes(S, X, T, r, bA, vA, CallPutFlag);
		}
	}

	public static double TurnbullWakemanAsian(String CallPutFlag, double S, double SA, double X, 
			double T, double T2, double tau, double r, double b, double v) {
		double m1 = (Math.exp(b * T) - Math.exp(b * tau)) / (b * (T - tau));
		double m2 = 2 * Math.exp((2 * b + Math.pow(v, 2)) * T) / ((b + Math.pow(v, 2)) * (2 * b + Math.pow(v, 2)) * Math.pow(T - tau, 2)) +
		2 * Math.exp((2 * b + Math.pow(v, 2)) * tau) / (b * Math.pow(T - tau, 2)) * 
		(1 / (2 * b + Math.pow(v, 2)) - Math.exp(b * (T - tau)) / (b + Math.pow(v, 2)));
		
		double bA = Math.log(m1) / T;
		double vA = Math.sqrt(Math.log(m2) / T - 2 * bA);
		double t1 = T - T2;

		if (t1 > 0) {
			X = T / T2 * X - t1 / T2 * SA;
			return VanillaOptions.generalBlackScholes(S, X, T2, r, bA, vA,CallPutFlag) * T2 / T;
		} else {
			return VanillaOptions.generalBlackScholes(S, X, T2, r, bA, vA,CallPutFlag);
		}
	}

	public static double LevyAsian(String CallPutFlag, double S, double SA, double X, 
		double T, double T2, double r, double b, double v) {
			double SE = S / (T * b) * (Math.exp((b - r) * T2) - Math.exp(-r * T2));
			double m = 2 * Math.pow(S, 2) / (b + Math.pow(v, 2)) * 
			((Math.exp((2 * b + Math.pow(v, 2)) * T2) - 1) / (2 * b + Math.pow(v, 2)) - 
			(Math.exp(b * T2) - 1) / b);
			double d = m / Math.pow(T, 2);
			double Sv = Math.log(d) - 2 * (r * T2 + Math.log(SE));
			double XStar = X - (T - T2) / T * SA;
			double d1 = 1 / Math.sqrt(Sv) * (Math.log(d) / 2 - Math.log(XStar));
			double d2 = d1 - Math.sqrt(Sv);
			
			NormalDistribution norm = new NormalDistribution();
			
			if (CallPutFlag.equals("c")) {
				return SE * norm.cumulativeProbability(d1) - XStar * Math.exp(-r * T2) * norm.cumulativeProbability(d2);
			} else if (CallPutFlag.equals("p")) {
				return (SE * norm.cumulativeProbability(d1) - XStar * Math.exp(-r * T2) * norm.cumulativeProbability(d2)) - SE + XStar * Math.exp(-r * T2);
			} else {
				throw new IllegalArgumentException("Invalid CallPutFlag value");
			}
		}
	
	public static double MonteCarloGeometricAsian(String CallPutFlag, double S, double X, double T, double r, double b, double v, int numSimulations, int numSteps) {
        Random rand = new Random();
        double dt = T / numSteps;
        double discount = Math.exp(-r * T);
        double payoffSum = 0.0;

        for (int i = 0; i < numSimulations; i++) {
            double pathProduct = 1.0;
            double St = S;

            for (int j = 0; j < numSteps; j++) {
                double dW = rand.nextGaussian() * Math.sqrt(dt);
                St = St * Math.exp((b - 0.5 * v * v) * dt + v * dW);
                pathProduct *= St;
            }

            double geometricMean = Math.pow(pathProduct, 1.0 / numSteps);
            double payoff = (CallPutFlag.equals("c") ? Math.max(geometricMean - X, 0) : Math.max(X - geometricMean, 0));
            payoffSum += payoff;
        }

        return discount * payoffSum / numSimulations;
    }

    public static double MonteCarloArithmeticAsian(String CallPutFlag, double S, double X, double T, double r, double b, double v, int numSimulations, int numSteps) {
        Random rand = new Random();
        double dt = T / numSteps;
        double discount = Math.exp(-r * T);
        double payoffSum = 0.0;

        for (int i = 0; i < numSimulations; i++) {
            double pathSum = 0.0;
            double St = S;

            for (int j = 0; j < numSteps; j++) {
                double dW = rand.nextGaussian() * Math.sqrt(dt);
                St = St * Math.exp((b - 0.5 * v * v) * dt + v * dW);
                pathSum += St;
            }

            double arithmeticMean = pathSum / numSteps;
            double payoff = (CallPutFlag.equals("c") ? Math.max(arithmeticMean - X, 0) : Math.max(X - arithmeticMean, 0));
            payoffSum += payoff;
        }

        return discount * payoffSum / numSimulations;
    }
	public static void main(String[] args) {
        // Example usage:
		double S = 253.5;
		double Sav = 331.47;
		double X = 324.3*(1-0.1);
		double t = 0.08;
		double T = 0.08;
		double r = 0.02;
		double b = 0.0;
		double v = (0.42);
		
        double geoResult = GeometricAverageRateOption("p", S, Sav, X, t, T, r, b, v);
        System.out.println("Geometric Average Rate Option Result: " + geoResult);

        
	}
}
