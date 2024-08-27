package com.example.pricing;

import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Arrays;
import java.util.Random;

public class BarrierOptions {
	private static NormalDistribution norm = new NormalDistribution();

    public static double discreteAdjustedBarrier(double S, double H, double v, double dt) {
        if (H > S) {
            return H * Math.exp(0.5826 * v * Math.sqrt(dt));
        } else if (H < S) {
            return H * Math.exp(-0.5826 * v * Math.sqrt(dt));
        }
        return H;
    }

    public static double standardBarrier(String TypeFlag, double S, double X, double H, double K, double T, double r, double b, double v) {
        if (!Arrays.asList("cdi", "cdo", "cui", "cuo", "pdi", "pdo", "pui", "puo").contains(TypeFlag)) {
            throw new IllegalArgumentException("The type of the option should be a Call or Put (Down and out, Down and in, Up and out, Up and In)");
        }

        double mu = (b - v * v / 2) / (v * v);
        double lambda = Math.sqrt(mu * mu + 2 * r / (v * v));
        double X1 = Math.log(S / X) / (v * Math.sqrt(T)) + (1 + mu) * v * Math.sqrt(T);
        double X2 = Math.log(S / H) / (v * Math.sqrt(T)) + (1 + mu) * v * Math.sqrt(T);
        double y1 = Math.log(H * H / (S * X)) / (v * Math.sqrt(T)) + (1 + mu) * v * Math.sqrt(T);
        double y2 = Math.log(H / S) / (v * Math.sqrt(T)) + (1 + mu) * v * Math.sqrt(T);
        double Z = Math.log(H / S) / (v * Math.sqrt(T)) + lambda * v * Math.sqrt(T);

        int eta, phi;
        if (TypeFlag.equals("cdi") || TypeFlag.equals("cdo")) {
            eta = 1;
            phi = 1;
        } else if (TypeFlag.equals("cui") || TypeFlag.equals("cuo")) {
            eta = -1;
            phi = 1;
        } else if (TypeFlag.equals("pdi") || TypeFlag.equals("pdo")) {
            eta = 1;
            phi = -1;
        } else {
            eta = -1;
            phi = -1;
        }

        double f1 = phi * S * Math.exp((b - r) * T) * norm.cumulativeProbability(phi * X1) - phi * X * Math.exp(-r * T) * norm.cumulativeProbability(phi * X1 - phi * v * Math.sqrt(T));
        double f2 = phi * S * Math.exp((b - r) * T) * norm.cumulativeProbability(phi * X2) - phi * X * Math.exp(-r * T) * norm.cumulativeProbability(phi * X2 - phi * v * Math.sqrt(T));
        double f3 = phi * S * Math.exp((b - r) * T) * Math.pow(H / S, 2 * (mu + 1)) * norm.cumulativeProbability(eta * y1) - phi * X * Math.exp(-r * T) * Math.pow(H / S, 2 * mu) * norm.cumulativeProbability(eta * (y1 - v * Math.sqrt(T)));
        double f4 = phi * S * Math.exp((b - r) * T) * Math.pow(H / S, 2 * (mu + 1)) * norm.cumulativeProbability(eta * y2) - phi * X * Math.exp(-r * T) * Math.pow(H / S, 2 * mu) * norm.cumulativeProbability(eta * (y2 - v * Math.sqrt(T)));
        double f5 = K * Math.exp(-r * T) * (norm.cumulativeProbability(eta * (X2 - v * Math.sqrt(T))) - Math.pow(H / S, 2 * mu) * norm.cumulativeProbability(eta * (y2 - v * Math.sqrt(T))));
        double f6 = K * (Math.pow(H / S, mu + lambda) * norm.cumulativeProbability(eta * Z) + Math.pow(H / S, mu - lambda) * norm.cumulativeProbability(eta * Z - 2 * eta * lambda * v * Math.sqrt(T)));

        if (X > H) {
            switch (TypeFlag) {
                case "cdi": return f3 + f5;
                case "cui": return f1 + f5;
                case "pdi": return f2 - f3 + f4 + f5;
                case "pui": return f1 - f2 + f4 + f5;
                case "cdo": return f1 - f3 + f6;
                case "cuo": return f6;
                case "pdo": return f1 - f2 + f3 - f4 + f6;
                case "puo": return f2 - f4 + f6;
            }
        } else if (X < H) {
            switch (TypeFlag) {
                case "cdi": return f1 - f2 + f4 + f5;
                case "cui": return f2 - f3 + f4 + f5;
                case "pdi": return f1 + f5;
                case "pui": return f3 + f5;
                case "cdo": return f2 + f6 - f4;
                case "cuo": return f1 - f2 + f3 - f4 + f6;
                case "pdo": return f6;
                case "puo": return f1 - f3 + f6;
            }
        }
        return 0.0;
    }

    public static double monteCarloBarrier(String TypeFlag, double S, double X, double H, double T, double r, double v, int numSimulations, int numSteps) {
        if (!Arrays.asList("cdi", "cdo", "cui", "cuo", "pdi", "pdo", "pui", "puo").contains(TypeFlag)) {
            throw new IllegalArgumentException("The type of the option should be a Call or Put (Down and out, Down and in, Up and out, Up and In)");
        }

        double dt = T / numSteps;
        double discountFactor = Math.exp(-r * T);
        double[][] SPaths = new double[numSimulations][numSteps + 1];
        Random rand = new Random();
        
        for (int i = 0; i < numSimulations; i++) {
            SPaths[i][0] = S;
            for (int t = 1; t <= numSteps; t++) {
                double Z = rand.nextGaussian();
                SPaths[i][t] = SPaths[i][t-1] * Math.exp((r - 0.5 * v * v) * dt + v * Math.sqrt(dt) * Z);
            }
        }

        boolean[] barrierCrossed = new boolean[numSimulations];
        Arrays.fill(barrierCrossed, false);
        
        if (TypeFlag.equals("cdo") || TypeFlag.equals("pdo") || TypeFlag.equals("cdi") || TypeFlag.equals("pdi")) {
            for (int i = 0; i < numSimulations; i++) {
                for (int t = 0; t <= numSteps; t++) {
                    if (SPaths[i][t] <= H) {
                        barrierCrossed[i] = true;
                        break;
                    }
                }
            }
        } else if (TypeFlag.equals("cuo") || TypeFlag.equals("puo") || TypeFlag.equals("cui") || TypeFlag.equals("pui")) {
            for (int i = 0; i < numSimulations; i++) {
                for (int t = 0; t <= numSteps; t++) {
                    if (SPaths[i][t] >= H) {
                        barrierCrossed[i] = true;
                        break;
                    }
                }
            }
        }

        double[] payoffs = new double[numSimulations];
        for (int i = 0; i < numSimulations; i++) {
            if (TypeFlag.equals("cdo") || TypeFlag.equals("cuo") || TypeFlag.equals("cdi") || TypeFlag.equals("cui")) {
                payoffs[i] = Math.max(SPaths[i][numSteps] - X, 0);
            } else if (TypeFlag.equals("pdo") || TypeFlag.equals("puo") || TypeFlag.equals("pdi") || TypeFlag.equals("pui")) {
                payoffs[i] = Math.max(X - SPaths[i][numSteps], 0);
            }

            if (TypeFlag.equals("cdo") || TypeFlag.equals("pdo") || TypeFlag.equals("cuo") || TypeFlag.equals("puo")) {
                if (barrierCrossed[i]) {
                    payoffs[i] = 0;
                }
            } else if (TypeFlag.equals("cdi") || TypeFlag.equals("pdi") || TypeFlag.equals("cui") || TypeFlag.equals("pui")) {
                if (!barrierCrossed[i]) {
                    payoffs[i] = 0;
                }
            }
        }

        double optionPrice = 0.0;
        for (double payoff : payoffs) {
            optionPrice += payoff;
        }
        optionPrice /= numSimulations;
        optionPrice *= discountFactor;

        return optionPrice;
    }
    
    public static double doubleBarrier(String TypeFlag, double S, double X, double L, double U, double T, double r, double b, double v, double delta1, double delta2) {
        double E = L * Math.exp(delta1 * T);
        double F = U * Math.exp(delta1 * T);
        double Sum1 = 0;
        double Sum2 = 0;
        double OutValue = 0.0;

        if (TypeFlag.equals("co") || TypeFlag.equals("ci")) {
            for (int n = -5; n <= 5; n++) {
                double d1 = (Math.log(S * Math.pow(U, 2 * n) / (X * Math.pow(L, 2 * n))) + (b + Math.pow(v, 2) / 2) * T) / (v * Math.sqrt(T));
                double d2 = (Math.log(S * Math.pow(U, 2 * n) / (F * Math.pow(L, 2 * n))) + (b + Math.pow(v, 2) / 2) * T) / (v * Math.sqrt(T));
                double d3 = (Math.log(Math.pow(L, 2 * n + 2) / (X * S * Math.pow(U, 2 * n))) + (b + Math.pow(v, 2) / 2) * T) / (v * Math.sqrt(T));
                double d4 = (Math.log(Math.pow(L, 2 * n + 2) / (F * S * Math.pow(U, 2 * n))) + (b + Math.pow(v, 2) / 2) * T) / (v * Math.sqrt(T));
                double mu1 = 2 * (b - delta2 - n * (delta1 - delta2)) / Math.pow(v, 2) + 1;
                double mu2 = 2 * n * (delta1 - delta2) / Math.pow(v, 2);
                double mu3 = 2 * (b - delta2 + n * (delta1 - delta2)) / Math.pow(v, 2) + 1;
                Sum1 += Math.pow(Math.pow(U, n) / Math.pow(L, n), mu1) * Math.pow(L / S, mu2) * (norm.cumulativeProbability(d1) - norm.cumulativeProbability(d2))
                        - Math.pow(Math.pow(L, n + 1) / (Math.pow(U, n) * S), mu3) * (norm.cumulativeProbability(d3) - norm.cumulativeProbability(d4));
                Sum2 += Math.pow(Math.pow(U, n) / Math.pow(L, n) , (mu1 - 2)) * Math.pow(L / S, mu2) * (norm.cumulativeProbability(d1 - v * Math.sqrt(T)) - norm.cumulativeProbability(d2 - v * Math.sqrt(T)))
                        - Math.pow(Math.pow(L, n + 1) / (Math.pow(U, n) * S) , (mu3 - 2)) * (norm.cumulativeProbability(d3 - v * Math.sqrt(T)) - norm.cumulativeProbability(d4 - v * Math.sqrt(T)));
            }
            OutValue = S * Math.exp((b - r) * T) * Sum1 - X * Math.exp(-r * T) * Sum2;
        } else if (TypeFlag.equals("po") || TypeFlag.equals("pi")) {
            for (int n = -5; n <= 5; n++) {
                double d1 = (Math.log(S * Math.pow(U, 2 * n) / (E * Math.pow(L, 2 * n))) + (b + Math.pow(v, 2) / 2) * T) / (v * Math.sqrt(T));
                double d2 = (Math.log(S * Math.pow(U, 2 * n) / (X * Math.pow(L, 2 * n))) + (b + Math.pow(v, 2) / 2) * T) / (v * Math.sqrt(T));
                double d3 = (Math.log(Math.pow(L, 2 * n + 2) / (E * S * Math.pow(U, 2 * n))) + (b + Math.pow(v, 2) / 2) * T) / (v * Math.sqrt(T));
                double d4 = (Math.log(Math.pow(L, 2 * n + 2) / (X * S * Math.pow(U, 2 * n))) + (b + Math.pow(v, 2) / 2) * T) / (v * Math.sqrt(T));
                double mu1 = 2 * (b - delta2 - n * (delta1 - delta2)) / Math.pow(v, 2) + 1;
                double mu2 = 2 * n * (delta1 - delta2) / Math.pow(v, 2);
                double mu3 = 2 * (b - delta2 + n * (delta1 - delta2)) / Math.pow(v, 2) + 1;
                Sum1 += Math.pow(Math.pow(U, n) / Math.pow(L, n) , mu1) * Math.pow(L / S, mu2) * (norm.cumulativeProbability(d1) - norm.cumulativeProbability(d2))
                        - Math.pow(Math.pow(L, n + 1) / (Math.pow(U, n) * S) , mu3) * (norm.cumulativeProbability(d3) - norm.cumulativeProbability(d4));
                Sum2 += Math.pow(Math.pow(U, n) / Math.pow(L, n) , (mu1 - 2)) * Math.pow(L / S, mu2) * (norm.cumulativeProbability(d1 - v * Math.sqrt(T)) - norm.cumulativeProbability(d2 - v * Math.sqrt(T)))
                        - Math.pow(Math.pow(L, n + 1) / (Math.pow(U, n) * S) , (mu3 - 2)) * (norm.cumulativeProbability(d3 - v * Math.sqrt(T)) - norm.cumulativeProbability(d4 - v * Math.sqrt(T)));
            }
            OutValue =  X * Math.exp(-r * T) * Sum2 - S * Math.exp((b - r) * T) * Sum1;
        }
        if(TypeFlag.equals("co") || TypeFlag.equals("po") ){
        	return OutValue;
        }
        else if (TypeFlag.equals("ci")) {
        	return VanillaOptions.generalBlackScholes(S, X, T, r, b, v, "c") - OutValue; 
        } else {
        	return VanillaOptions.generalBlackScholes(S, X, T, r, b, v, "p") - OutValue; 
        }
    }
    
    public static double monteCarloDoubleBarrier(String TypeFlag, double S, double X, double L, double U, double T, double mean, double v, int numSimulations, int numSteps) {
        if (!TypeFlag.equals("co") && !TypeFlag.equals("ci") && !TypeFlag.equals("po") && !TypeFlag.equals("pi")) {
            throw new IllegalArgumentException("The type of the option should be a Call or Put (Down and out, Down and in, Up and out, Up and In)");
        }

        double[][] SPaths = new double[numSimulations][numSteps + 1];
        for (int i = 0; i < numSimulations; i++) {
            SPaths[i][0] = S;
        }

        NormalDistribution newNorm = new NormalDistribution(mean,v);
        Random random = new Random();

        for (int t = 1; t <= numSteps; t++) {
            for (int i = 0; i < numSimulations; i++) {
                double Z = newNorm.inverseCumulativeProbability(random.nextDouble());
                SPaths[i][t] = SPaths[i][t - 1] * Math.exp(Z);
            }
        }

        boolean[] barriersCrossed = new boolean[numSimulations];
        for (int i = 0; i < numSimulations; i++) {
            for (int t = 0; t <= numSteps; t++) {
                if (SPaths[i][t] <= L || SPaths[i][t] >= U) {
                    barriersCrossed[i] = true;
                    break;
                }
            }
        }

        double[] payoffs = new double[numSimulations];
        for (int i = 0; i < numSimulations; i++) {
            if (TypeFlag.equals("ci") || TypeFlag.equals("co")) {
                payoffs[i] = Math.max(SPaths[i][numSteps] - X, 0);
            } else if (TypeFlag.equals("pi") || TypeFlag.equals("po")) {
                payoffs[i] = Math.max(X - SPaths[i][numSteps], 0);
            }
        }

        if (TypeFlag.equals("ci") || TypeFlag.equals("pi")) {
            for (int i = 0; i < numSimulations; i++) {
                if (!barriersCrossed[i]) {
                    payoffs[i] = 0;
                }
            }
        } else if (TypeFlag.equals("co") || TypeFlag.equals("po")) {
            for (int i = 0; i < numSimulations; i++) {
                if (barriersCrossed[i]) {
                    payoffs[i] = 0;
                }
            }
        }

        double optionPrice = 0.0;
        for (int i = 0; i < numSimulations; i++) {
            optionPrice += payoffs[i];
        }
        optionPrice /= numSimulations;

        return optionPrice;
    }
    
    public static double CND(double x) {
        return norm.cumulativeProbability(x);
    }
    
    public static double CBND(double a, double b, double rho) {
        double[] X = {0.24840615, 0.39233107, 0.21141819, 0.03324666, 0.00082485334};
        double[] y = {0.10024215, 0.48281397, 1.0609498, 1.7797294, 2.6697604};
        double a1 = a / Math.sqrt(2 * (1 - Math.pow(rho, 2)));
        double b1 = b / Math.sqrt(2 * (1 - Math.pow(rho, 2)));
        double sum = 0.0;
        
        if (a <= 0 && b <= 0 && rho <= 0) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    sum += X[i] * X[j] * Math.exp(a1 * (2 * y[i] - a1) + b1 * (2 * y[j] - b1) + 2 * rho * (y[i] - a1) * (y[j] - b1));
                }
            }
            return Math.sqrt(1 - Math.pow(rho, 2)) / Math.PI * sum;
        } else if (a <= 0 && b >= 0 && rho >= 0) {
            return CND(a) - CBND(a, -b, -rho);
        } else if (a >= 0 && b <= 0 && rho >= 0) {
            return CND(b) - CBND(-a, b, -rho);
        } else if (a >= 0 && b >= 0 && rho <= 0) {
            return CND(a) + CND(b) - 1 + CBND(-a, -b, rho);
        } else if (a * b * rho > 0) {
            double rho1 = (rho * a - b) * Math.signum(a) / Math.sqrt(a * a - 2 * rho * a * b + b * b);
            double rho2 = (rho * b - a) * Math.signum(b) / Math.sqrt(a * a - 2 * rho * a * b + b * b);
            double delta = (1 - Math.signum(a) * Math.signum(b)) / 4;
            return CBND(a, 0, rho1) + CBND(b, 0, rho2) - delta;
        }
        return 0.0;
    }

    public static double partialTimeBarrier(String TypeFlag, double S, double X, double H, double t1, double T2, double r, double b, double v) {
        double d1 = (Math.log(S / X) + (b + Math.pow(v, 2) / 2) * T2) / (v * Math.sqrt(T2));
        double d2 = d1 - v * Math.sqrt(T2);
        double f1 = (Math.log(S / X) + 2 * Math.log(H / S) + (b + Math.pow(v, 2) / 2) * T2) / (v * Math.sqrt(T2));
        double f2 = f1 - v * Math.sqrt(T2);
        double e1 = (Math.log(S / H) + (b + Math.pow(v, 2) / 2) * t1) / (v * Math.sqrt(t1));
        double e2 = e1 - v * Math.sqrt(t1);
        double e3 = e1 + 2 * Math.log(H / S) / (v * Math.sqrt(t1));
        double e4 = e3 - v * Math.sqrt(t1);
        double mu = (b - Math.pow(v, 2) / 2) / Math.pow(v, 2);
        double rho = Math.sqrt(t1 / T2);
        double g1 = (Math.log(S / H) + (b + Math.pow(v, 2) / 2) * T2) / (v * Math.sqrt(T2));
        double g2 = g1 - v * Math.sqrt(T2);
        double g3 = g1 + 2 * Math.log(H / S) / (v * Math.sqrt(T2));
        double g4 = g3 - v * Math.sqrt(T2);
        
        double z1 = norm.cumulativeProbability(e2) - Math.pow(H / S, 2 * mu) * norm.cumulativeProbability(e4);
        double z2 = norm.cumulativeProbability(-e2) - Math.pow(H / S, 2 * mu) * norm.cumulativeProbability(-e4);
        double z3 = CBND(g2, e2, rho) - Math.pow(H / S, 2 * mu) * CBND(g4, -e4, -rho);
        double z4 = CBND(-g2, -e2, rho) - Math.pow(H / S, 2 * mu) * CBND(-g4, e4, -rho);
        double z5 = norm.cumulativeProbability(e1) - Math.pow(H / S, 2 * (mu + 1)) * norm.cumulativeProbability(e3);
        double z6 = norm.cumulativeProbability(-e1) - Math.pow(H / S, 2 * (mu + 1)) * norm.cumulativeProbability(-e3);
        double z7 = CBND(g1, e1, rho) - Math.pow(H / S, 2 * (mu + 1)) * CBND(g3, -e3, -rho);
        double z8 = CBND(-g1, -e1, rho) - Math.pow(H / S, 2 * (mu + 1)) * CBND(-g3, e3, -rho);
        
        double partialTimeBarrierPrice = 0.0;
        int eta = 0;

        switch (TypeFlag) {
            case "cdoA":
                eta = 1;
                partialTimeBarrierPrice = S * Math.exp((b - r) * T2) * (CBND(d1, eta * e1, eta * rho) - Math.pow(H / S, 2 * (mu + 1)) * CBND(f1, eta * e3, eta * rho))
                    - X * Math.exp(-r * T2) * (CBND(d2, eta * e2, eta * rho) - Math.pow(H / S, 2 * mu) * CBND(f2, eta * e4, eta * rho));
                break;
            case "cuoA":
                eta = -1;
                partialTimeBarrierPrice = S * Math.exp((b - r) * T2) * (CBND(d1, eta * e1, eta * rho) - Math.pow(H / S, 2 * (mu + 1)) * CBND(f1, eta * e3, eta * rho))
                    - X * Math.exp(-r * T2) * (CBND(d2, eta * e2, eta * rho) - Math.pow(H / S, 2 * mu) * CBND(f2, eta * e4, eta * rho));
                break;
            case "cdoB2":
                if (X < H) {
                    partialTimeBarrierPrice = S * Math.exp((b - r) * T2) * (CBND(g1, e1, rho) - Math.pow(H / S, 2 * (mu + 1)) * CBND(g3, -e3, -rho))
                        - X * Math.exp(-r * T2) * (CBND(g2, e2, rho) - Math.pow(H / S, 2 * mu) * CBND(g4, -e4, -rho));
                } else {
                    partialTimeBarrierPrice = partialTimeBarrier("coB1", S, X, H, t1, T2, r, b, v);
                }
                break;
            case "cuoB2":
                if (X < H) {
                    partialTimeBarrierPrice = S * Math.exp((b - r) * T2) * (CBND(-g1, -e1, rho) - Math.pow(H / S, 2 * (mu + 1)) * CBND(-g3, e3, -rho))
                        - X * Math.exp(-r * T2) * (CBND(-g2, -e2, rho) - Math.pow(H / S, 2 * mu) * CBND(-g4, e4, -rho))
                        - S * Math.exp((b - r) * T2) * (CBND(-d1, -e1, rho) - Math.pow(H / S, 2 * (mu + 1)) * CBND(e3, -f1, -rho))
                        + X * Math.exp(-r * T2) * (CBND(-d2, -e2, rho) - Math.pow(H / S, 2 * mu) * CBND(e4, -f2, -rho));
                } else {
                    partialTimeBarrierPrice = S * Math.exp((b - r) * T2) * (CBND(-g1, -e1, rho) - Math.pow(H / S, 2 * (mu + 1)) * CBND(-g3, e3, -rho))
                        - X * Math.exp(-r * T2) * (CBND(-g2, -e2, rho) - Math.pow(H / S, 2 * mu) * CBND(-g4, e4, -rho))
                        - S * Math.exp((b - r) * T2) * (CBND(-d1, -e1, rho) - Math.pow(H / S, 2 * (mu + 1)) * CBND(-f1, e3, -rho))
                        + X * Math.exp(-r * T2) * (CBND(-d2, -e2, rho) - Math.pow(H / S, 2 * mu) * CBND(-f2, e4, -rho))
                        + S * Math.exp((b - r) * T2) * (CBND(g1, e1, rho) - Math.pow(H / S, 2 * (mu + 1)) * CBND(g3, -e3, -rho))
                        - X * Math.exp(-r * T2) * (CBND(g2, e2, rho) - Math.pow(H / S, 2 * mu) * CBND(g4, -e4, -rho));
                }
                break;
            case "coB1":
                if (X > H) {
                    partialTimeBarrierPrice = S * Math.exp((b - r) * T2) * (CBND(d1, e1, rho) - Math.pow(H / S, 2 * (mu + 1)) * CBND(f1, -e3, -rho))
                        - X * Math.exp(-r * T2) * (CBND(d2, e2, rho) - Math.pow(H / S, 2 * mu) * CBND(f2, -e4, -rho));
                } else {
                    partialTimeBarrierPrice = S * Math.exp((b - r) * T2) * (CBND(-g1, -e1, rho) - Math.pow(H / S, 2 * (mu + 1)) * CBND(-g3, e3, -rho))
                        - X * Math.exp(-r * T2) * (CBND(-g2, -e2, rho) - Math.pow(H / S, 2 * mu) * CBND(-g4, e4, -rho))
                        - S * Math.exp((b - r) * T2) * (CBND(-d1, -e1, rho) - Math.pow(H / S, 2 * (mu + 1)) * CBND(-f1, e3, -rho))
                        + X * Math.exp(-r * T2) * (CBND(-d2, -e2, rho) - Math.pow(H / S, 2 * mu) * CBND(-f2, e4, -rho))
                        + S * Math.exp((b - r) * T2) * (CBND(g1, e1, rho) - Math.pow(H / S, 2 * (mu + 1)) * CBND(g3, -e3, -rho))
                        - X * Math.exp(-r * T2) * (CBND(g2, e2, rho) - Math.pow(H / S, 2 * mu) * CBND(g4, -e4, -rho));
                }
                break;
            case "pdoA":
                partialTimeBarrierPrice = partialTimeBarrier("cdoA", S, X, H, t1, T2, r, b, v)
                    - S * Math.exp((b - r) * T2) * z5 + X * Math.exp(-r * T2) * z1;
                break;
            case "puoA":
                partialTimeBarrierPrice = partialTimeBarrier("cuoA", S, X, H, t1, T2, r, b, v)
                    - S * Math.exp((b - r) * T2) * z6 + X * Math.exp(-r * T2) * z2;
                break;
            case "poB1":
                partialTimeBarrierPrice = partialTimeBarrier("coB1", S, X, H, t1, T2, r, b, v)
                    - S * Math.exp((b - r) * T2) * z8 + X * Math.exp(-r * T2) * z4
                    - S * Math.exp((b - r) * T2) * z7 + X * Math.exp(-r * T2) * z3;
                break;
            case "pdoB2":
                partialTimeBarrierPrice = partialTimeBarrier("cdoB2", S, X, H, t1, T2, r, b, v)
                    - S * Math.exp((b - r) * T2) * z7 + X * Math.exp(-r * T2) * z3;
                break;
            case "puoB2":
                partialTimeBarrierPrice = partialTimeBarrier("cuoB2", S, X, H, t1, T2, r, b, v)
                    - S * Math.exp((b - r) * T2) * z8 + X * Math.exp(-r * T2) * z4;
                break;
            default:
                throw new IllegalArgumentException("Invalid TypeFlag");
        }

        return partialTimeBarrierPrice;
    }
    
    public static double twoAssetBarrier(String TypeFlag, double S1, double S2, double X, double H, double T, double r, double b1, double b2, double v1, double v2, double rho) {
        double mu1 = b1 - Math.pow(v1, 2) / 2;
        double mu2 = b2 - Math.pow(v2, 2) / 2;

        double d1 = (Math.log(S1 / X) + (mu1 + Math.pow(v1, 2) / 2) * T) / (v1 * Math.sqrt(T));
        double d2 = d1 - v1 * Math.sqrt(T);
        double d3 = d1 + 2 * rho * Math.log(H / S2) / (v2 * Math.sqrt(T));
        double d4 = d2 + 2 * rho * Math.log(H / S2) / (v2 * Math.sqrt(T));
        double e1 = (Math.log(H / S2) - (mu2 + rho * v1 * v2) * T) / (v2 * Math.sqrt(T));
        double e2 = e1 + rho * v1 * Math.sqrt(T);
        double e3 = e1 - 2 * Math.log(H / S2) / (v2 * Math.sqrt(T));
        double e4 = e2 - 2 * Math.log(H / S2) / (v2 * Math.sqrt(T));

        int eta = 0, phi = 0;

        switch (TypeFlag) {
            case "cuo":
            case "cui":
                eta = 1;
                phi = 1;
                break;
            case "cdo":
            case "cdi":
                eta = 1;
                phi = -1;
                break;
            case "puo":
            case "pui":
                eta = -1;
                phi = 1;
                break;
            case "pdo":
            case "pdi":
                eta = -1;
                phi = -1;
                break;
            default:
                throw new IllegalArgumentException("Invalid TypeFlag value");
        }

        double KnockOutValue = (eta * S1 * Math.exp((b1 - r) * T) *
                (CBND(eta * d1, phi * e1, -eta * phi * rho) -
                        Math.exp(2 * (mu2 + rho * v1 * v2) * Math.log(H / S2) / Math.pow(v2, 2)) *
                                CBND(eta * d3, phi * e3, -eta * phi * rho)) -
                eta * Math.exp(-r * T) * X *
                        (CBND(eta * d2, phi * e2, -eta * phi * rho) -
                                Math.exp(2 * mu2 * Math.log(H / S2) / Math.pow(v2, 2)) *
                                        CBND(eta * d4, phi * e4, -eta * phi * rho)));

        switch (TypeFlag) {
            case "cuo":
            case "cdo":
            case "puo":
            case "pdo":
                return KnockOutValue;
            case "cui":
            case "cdi":
                return VanillaOptions.generalBlackScholes(S1, X, T, r, b1, v1, "c") - KnockOutValue;
            case "pui":
            case "pdi":
                return VanillaOptions.generalBlackScholes(S1, X, T, r, b1, v1, "p") - KnockOutValue;
            default:
                throw new IllegalArgumentException("Invalid TypeFlag value");
        }
    }
    
    public static double partialTimeTwoAssetBarrier(String TypeFlag, double S1, double S2, double X, double H, double t1, double T2, double r, double b1, double b2, double v1, double v2, double rho) {
        int phi, eta;
        
        if (TypeFlag.equals("cdo") || TypeFlag.equals("pdo") || TypeFlag.equals("cdi") || TypeFlag.equals("pdi")) {
            phi = -1;
        } else {
            phi = 1;
        }
        
        if (TypeFlag.equals("cdo") || TypeFlag.equals("cuo") || TypeFlag.equals("cdi") || TypeFlag.equals("cui")) {
            eta = 1;
        } else {
            eta = -1;
        }
        
        double mu1 = b1 - Math.pow(v1, 2) / 2;
        double mu2 = b2 - Math.pow(v2, 2) / 2;
        
        double d1 = (Math.log(S1 / X) + (mu1 + Math.pow(v1, 2)) * T2) / (v1 * Math.sqrt(T2));
        double d2 = d1 - v1 * Math.sqrt(T2);
        double d3 = d1 + 2 * rho * Math.log(H / S2) / (v2 * Math.sqrt(T2));
        double d4 = d2 + 2 * rho * Math.log(H / S2) / (v2 * Math.sqrt(T2));
        
        double e1 = (Math.log(H / S2) - (mu2 + rho * v1 * v2) * t1) / (v2 * Math.sqrt(t1));
        double e2 = e1 + rho * v1 * Math.sqrt(t1);
        double e3 = e1 - 2 * Math.log(H / S2) / (v2 * Math.sqrt(t1));
        double e4 = e2 - 2 * Math.log(H / S2) / (v2 * Math.sqrt(t1));
        
        double rhoAdjusted = -eta * phi * rho * Math.sqrt(t1 / T2);
        
        double OutBarrierValue = (eta * S1 * Math.exp((b1 - r) * T2) *
                (CBND(eta * d1, phi * e1, rhoAdjusted) -
                        Math.exp(2 * Math.log(H / S2) * (mu2 + rho * v1 * v2) / Math.pow(v2, 2)) *
                                CBND(eta * d3, phi * e3, rhoAdjusted)) -
                eta * Math.exp(-r * T2) * X *
                        (CBND(eta * d2, phi * e2, rhoAdjusted) -
                                Math.exp(2 * Math.log(H / S2) * mu2 / Math.pow(v2, 2)) *
                                        CBND(eta * d4, phi * e4, rhoAdjusted)));
        
        switch (TypeFlag) {
            case "cdo":
            case "cuo":
            case "pdo":
            case "puo":
                return OutBarrierValue;
            case "cui":
            case "cdi":
                return VanillaOptions.generalBlackScholes(S1, X, T2, r, b1, v1, "c") - OutBarrierValue;
            case "pui":
            case "pdi":
                return VanillaOptions.generalBlackScholes(S1, X, T2, r, b1, v1, "p") - OutBarrierValue;
            default:
                throw new IllegalArgumentException("Invalid TypeFlag value");
        }
    }
    
    public static double PartialFixedLB(String CallPutFlag, double S, double X, double t1, double T2, double r, double b, double v) {
        double d1 = (Math.log(S / X) + (b + Math.pow(v, 2) / 2) * T2) / (v * Math.sqrt(T2));
        double d2 = d1 - v * Math.sqrt(T2);
        double e1 = ((b + Math.pow(v, 2) / 2) * (T2 - t1)) / (v * Math.sqrt(T2 - t1));
        double e2 = e1 - v * Math.sqrt(T2 - t1);
        double f1 = (Math.log(S / X) + (b + Math.pow(v, 2) / 2) * t1) / (v * Math.sqrt(t1));
        double f2 = f1 - v * Math.sqrt(t1);

        double partialFixedLB;
        if (CallPutFlag.equals("c")) {
            partialFixedLB = (
                S * Math.exp((b - r) * T2) * norm.cumulativeProbability(d1) -
                Math.exp(-r * T2) * X * norm.cumulativeProbability(d2) +
                S * Math.exp(-r * T2) * (Math.pow(v, 2) / (2 * b)) * (
                    -Math.pow(S / X, -2 * b / Math.pow(v, 2)) * CBND(d1 - 2 * b * Math.sqrt(T2) / v, -f1 + 2 * b * Math.sqrt(t1) / v, -Math.sqrt(t1 / T2)) +
                    Math.exp(b * T2) * CBND(e1, d1, Math.sqrt(1 - t1 / T2))
                ) -
                S * Math.exp((b - r) * T2) * CBND(-e1, d1, -Math.sqrt(1 - t1 / T2)) -
                X * Math.exp(-r * T2) * CBND(f2, -d2, -Math.sqrt(t1 / T2)) +
                Math.exp(-b * (T2 - t1)) * (1 - Math.pow(v, 2) / (2 * b)) * S * Math.exp((b - r) * T2) * norm.cumulativeProbability(f1) * norm.cumulativeProbability(-e2)
            );
        } else if (CallPutFlag.equals("p")) {
            partialFixedLB = (
                X * Math.exp(-r * T2) * norm.cumulativeProbability(-d2) -
                S * Math.exp((b - r) * T2) * norm.cumulativeProbability(-d1) +
                S * Math.exp(-r * T2) * (Math.pow(v, 2) / (2 * b)) * (
                    Math.pow(S / X, -2 * b / Math.pow(v, 2)) * CBND(-d1 + 2 * b * Math.sqrt(T2) / v, f1 - 2 * b * Math.sqrt(t1) / v, -Math.sqrt(t1 / T2)) -
                    Math.exp(b * T2) * CBND(-e1, -d1, Math.sqrt(1 - t1 / T2))
                ) +
                S * Math.exp((b - r) * T2) * CBND(e1, -d1, -Math.sqrt(1 - t1 / T2)) +
                X * Math.exp(-r * T2) * CBND(-f2, d2, -Math.sqrt(t1 / T2)) -
                Math.exp(-b * (T2 - t1)) * (1 - Math.pow(v, 2) / (2 * b)) * S * Math.exp((b - r) * T2) * norm.cumulativeProbability(-f1) * norm.cumulativeProbability(e2)
            );
        } else {
            throw new IllegalArgumentException("Invalid CallPutFlag value");
        }

        return partialFixedLB;
    }

    public static double lookBarrier(String TypeFlag, double S, double X, double H, double t1, double T2, double r, double b, double v) {
        double hh = Math.log(H / S);
        double K = Math.log(X / S);
        double mu1 = b - Math.pow(v, 2) / 2;
        double mu2 = b + Math.pow(v, 2) / 2;
        double rho = Math.sqrt(t1 / T2);

        int eta;
        double m;

        switch (TypeFlag) {
            case "cuo":
            case "cui":
                eta = 1;
                m = Math.min(hh, K);
                break;
            case "pdo":
            case "pdi":
                eta = -1;
                m = Math.max(hh, K);
                break;
            default:
                throw new IllegalArgumentException("Invalid TypeFlag value");
        }

        double g1 = (norm.cumulativeProbability(eta * (hh - mu2 * t1) / (v * Math.sqrt(t1))) - Math.exp(2 * mu2 * hh / Math.pow(v, 2)) * norm.cumulativeProbability(eta * (-hh - mu2 * t1) / (v * Math.sqrt(t1)))) - 
                    (norm.cumulativeProbability(eta * (m - mu2 * t1) / (v * Math.sqrt(t1))) - Math.exp(2 * mu2 * hh / Math.pow(v, 2)) * norm.cumulativeProbability(eta * (m - 2 * hh - mu2 * t1) / (v * Math.sqrt(t1))));
        double g2 = (norm.cumulativeProbability(eta * (hh - mu1 * t1) / (v * Math.sqrt(t1))) - Math.exp(2 * mu1 * hh / Math.pow(v, 2)) * norm.cumulativeProbability(eta * (-hh - mu1 * t1) / (v * Math.sqrt(t1)))) - 
                    (norm.cumulativeProbability(eta * (m - mu1 * t1) / (v * Math.sqrt(t1))) - Math.exp(2 * mu1 * hh / Math.pow(v, 2)) * norm.cumulativeProbability(eta * (m - 2 * hh - mu1 * t1) / (v * Math.sqrt(t1))));

        double part1 = S * Math.exp((b - r) * T2) * (1 + Math.pow(v, 2) / (2 * b)) * 
                       (CBND(eta * (m - mu2 * t1) / (v * Math.sqrt(t1)), eta * (-K + mu2 * T2) / (v * Math.sqrt(T2)), -rho) - 
                        Math.exp(2 * mu2 * hh / Math.pow(v, 2)) * CBND(eta * (m - 2 * hh - mu2 * t1) / (v * Math.sqrt(t1)), eta * (2 * hh - K + mu2 * T2) / (v * Math.sqrt(T2)), -rho));
        double part2 = -Math.exp(-r * T2) * X * 
                       (CBND(eta * (m - mu1 * t1) / (v * Math.sqrt(t1)), eta * (-K + mu1 * T2) / (v * Math.sqrt(T2)), -rho) - 
                        Math.exp(2 * mu1 * hh / Math.pow(v, 2)) * CBND(eta * (m - 2 * hh - mu1 * t1) / (v * Math.sqrt(t1)), eta * (2 * hh - K + mu1 * T2) / (v * Math.sqrt(T2)), -rho));
        double part3 = -Math.exp(-r * T2) * Math.pow(v, 2) / (2 * b) * 
                       (S * Math.pow(S / X, -2 * b / Math.pow(v, 2)) * CBND(eta * (m + mu1 * t1) / (v * Math.sqrt(t1)), eta * (-K - mu1 * T2) / (v * Math.sqrt(T2)), -rho) - 
                        H * Math.pow(H / X, -2 * b / Math.pow(v, 2)) * CBND(eta * (m - 2 * hh + mu1 * t1) / (v * Math.sqrt(t1)), eta * (2 * hh - K - mu1 * T2) / (v * Math.sqrt(T2)), -rho));
        double part4 = S * Math.exp((b - r) * T2) * 
                       ((1 + Math.pow(v, 2) / (2 * b)) * norm.cumulativeProbability(eta * mu2 * (T2 - t1) / (v * Math.sqrt(T2 - t1))) + 
                        Math.exp(-b * (T2 - t1)) * (1 - Math.pow(v, 2) / (2 * b)) * norm.cumulativeProbability(eta * (-mu1 * (T2 - t1)) / (v * Math.sqrt(T2 - t1)))) * g1 - 
                       Math.exp(-r * T2) * X * g2;

        double OutValue = eta * (part1 + part2 + part3 + part4);

        switch (TypeFlag) {
            case "cuo":
            case "pdo":
                return OutValue;
            case "cui":
                return PartialFixedLB("c", S, X, t1, T2, r, b, v) - OutValue;
            case "pdi":
                return PartialFixedLB("p", S, X, t1, T2, r, b, v) - OutValue;
            default:
                throw new IllegalArgumentException("Invalid TypeFlag value");
        }
    }

    public static double SoftBarrier(String TypeFlag, double S, double X, double L, double U, double T, double r, double b, double v) {
        int eta;

        if (TypeFlag.equals("cdi") || TypeFlag.equals("cdo")) {
            eta = 1;
        } else if (TypeFlag.equals("pui") || TypeFlag.equals("puo")) {
            eta = -1;
        } else {
            throw new IllegalArgumentException("Invalid TypeFlag value");
        }

        double mu = (b + Math.pow(v, 2) / 2) / Math.pow(v, 2);
        double lambda1 = Math.exp(-0.5 * Math.pow(v, 2) * T * (mu + 0.5) * (mu - 0.5));
        double lambda2 = Math.exp(-0.5 * Math.pow(v, 2) * T * (mu - 0.5) * (mu - 1.5));
        double d1 = Math.log(Math.pow(U, 2) / (S * X)) / (v * Math.sqrt(T)) + mu * v * Math.sqrt(T);
        double d2 = d1 - (mu + 0.5) * v * Math.sqrt(T);
        double d3 = Math.log(Math.pow(U, 2) / (S * X)) / (v * Math.sqrt(T)) + (mu - 1) * v * Math.sqrt(T);
        double d4 = d3 - (mu - 0.5) * v * Math.sqrt(T);
        double e1 = Math.log(Math.pow(L, 2) / (S * X)) / (v * Math.sqrt(T)) + mu * v * Math.sqrt(T);
        double e2 = e1 - (mu + 0.5) * v * Math.sqrt(T);
        double e3 = Math.log(Math.pow(L, 2) / (S * X)) / (v * Math.sqrt(T)) + (mu - 1) * v * Math.sqrt(T);
        double e4 = e3 - (mu - 0.5) * v * Math.sqrt(T);

        double Value = (eta / (U - L) *
                (S * Math.exp((b - r) * T) * Math.pow(S, -2 * mu) *
                        Math.pow(S * X, mu + 0.5) / (2 * (mu + 0.5)) *
                        (Math.pow(U * U / (S * X), mu + 0.5) * norm.cumulativeProbability(eta * d1) -
                                lambda1 * norm.cumulativeProbability(eta * d2) -
                                Math.pow(L * L / (S * X), mu + 0.5) * norm.cumulativeProbability(eta * e1) +
                                lambda1 * norm.cumulativeProbability(eta * e2)) -
                        X * Math.exp(-r * T) * Math.pow(S, -2 * (mu - 1)) *
                        Math.pow(S * X, mu - 0.5) / (2 * (mu - 0.5)) *
                        (Math.pow(U * U / (S * X), mu - 0.5) * norm.cumulativeProbability(eta * d3) -
                                lambda2 * norm.cumulativeProbability(eta * d4) -
                                Math.pow(L * L / (S * X), mu - 0.5) * norm.cumulativeProbability(eta * e3) +
                                lambda2 * norm.cumulativeProbability(eta * e4))));

        switch (TypeFlag) {
            case "cdi":
            case "pui":
                return Value;
            case "cdo":
                return VanillaOptions.generalBlackScholes(S, X, T, r, b, v, "c") - Value;
            case "puo":
                return VanillaOptions.generalBlackScholes(S, X, T, r, b, v, "p") - Value;
            default:
                throw new IllegalArgumentException("Invalid TypeFlag value");
        }
    }

    
    public static void main(String[] args) {

    	double S = 100.0;
        double X = 100.0;
        double L = 95.0;
        double U = 85.0;
        double T = 0.5;
        double r = 0.1;
        double b = 0.05;
        double v = 0.3;
        System.out.println("Soft-Barrier: " + SoftBarrier("puo", S,X,L,U,T,r,b,v));

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
