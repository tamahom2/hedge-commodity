package pricing;

import org.apache.commons.math3.distribution.NormalDistribution;

public class Greeks {
	
	public static NormalDistribution norm = new NormalDistribution();
	
	public static double GDelta(String CallPutFlag, double S, double X, double T, double r, double b, double v) {
        double d1 = (Math.log(S / X) + (b + Math.pow(v, 2) / 2) * T) / (v * Math.sqrt(T));

        if (CallPutFlag.equals("c")) {
            return Math.exp((b - r) * T) * norm.cumulativeProbability(d1);
        } else if (CallPutFlag.equals("p")) {
            return Math.exp((b - r) * T) * (norm.cumulativeProbability(d1) - 1);
        } else {
            throw new IllegalArgumentException("Invalid CallPutFlag value");
        }
    }

    public static double GGamma(double S, double X, double T, double r, double b, double v) {
        double d1 = (Math.log(S / X) + (b + Math.pow(v, 2) / 2) * T) / (v * Math.sqrt(T));
        return Math.exp((b - r) * T) * norm.density(d1) / (S * v * Math.sqrt(T));
    }

    public static double GTheta(String CallPutFlag, double S, double X, double T, double r, double b, double v) {
        double d1 = (Math.log(S / X) + (b + Math.pow(v, 2) / 2) * T) / (v * Math.sqrt(T));
        double d2 = d1 - v * Math.sqrt(T);

        if (CallPutFlag.equals("c")) {
            return -S * Math.exp((b - r) * T) * norm.density(d1) * v / (2 * Math.sqrt(T)) - 
                    (b - r) * S * Math.exp((b - r) * T) * norm.cumulativeProbability(d1) - 
                    r * X * Math.exp(-r * T) * norm.cumulativeProbability(d2);
        } else if (CallPutFlag.equals("p")) {
            return -S * Math.exp((b - r) * T) * norm.density(d1) * v / (2 * Math.sqrt(T)) + 
                    (b - r) * S * Math.exp((b - r) * T) * norm.cumulativeProbability(-d1) + 
                    r * X * Math.exp(-r * T) * norm.cumulativeProbability(-d2);
        } else {
            throw new IllegalArgumentException("Invalid CallPutFlag value");
        }
    }

    public static double GVega(double S, double X, double T, double r, double b, double v) {
        double d1 = (Math.log(S / X) + (b + Math.pow(v, 2) / 2) * T) / (v * Math.sqrt(T));
        return S * Math.exp((b - r) * T) * norm.density(d1) * Math.sqrt(T);
    }

    public static double GRho(String CallPutFlag, double S, double X, double T, double r, double b, double v) {
        double d1 = (Math.log(S / X) + (b + Math.pow(v, 2) / 2) * T) / (v * Math.sqrt(T));
        double d2 = d1 - v * Math.sqrt(T);

        if (CallPutFlag.equals("c")) {
            if (b != 0) {
                return T * X * Math.exp(-r * T) * norm.cumulativeProbability(d2);
            } else {
                return -T * VanillaOptions.generalBlackScholes(S, X, T, r, b, v, CallPutFlag);
            }
        } else if (CallPutFlag.equals("p")) {
            if (b != 0) {
                return -T * X * Math.exp(-r * T) * norm.cumulativeProbability(-d2);
            } else {
                return -T * VanillaOptions.generalBlackScholes(S, X, T, r, b, v, CallPutFlag);
            }
        } else {
            throw new IllegalArgumentException("Invalid CallPutFlag value");
        }
    }

    public static double GCarry(String CallPutFlag, double S, double X, double T, double r, double b, double v) {
        double d1 = (Math.log(S / X) + (b + Math.pow(v, 2) / 2) * T) / (v * Math.sqrt(T));

        if (CallPutFlag.equals("c")) {
            return T * S * Math.exp((b - r) * T) * norm.cumulativeProbability(d1);
        } else if (CallPutFlag.equals("p")) {
            return -T * S * Math.exp((b - r) * T) * norm.cumulativeProbability(-d1);
        } else {
            throw new IllegalArgumentException("Invalid CallPutFlag value");
        }
    }
    
    public static void main(String[] args) {
    	double S = 0.024140;
    	double X = 0.018;
    	double T = 0.25;
    	double r = 0.0;
    	double b = 0.0;
    	double v = 0.3;
    	
    	System.out.println("The delta is : "+ GDelta("c", S, X, T, r, b, v));
    	System.out.println("The gamma is : "+ GGamma(S, X, T, r, b, v));
    	System.out.println("The theta is : "+ GTheta("c", S, X, T, r, b, v));
    	System.out.println("The vega is : "+ GVega(S, X, T, r, b, v));
    	System.out.println("The rho is : "+ GRho("c", S, X, T, r, b, v));
    	System.out.println("The carry is : "+ GCarry("c", S, X, T, r, b, v));

    }
    
	
}
