package com.example.service;

import com.example.model.BacktestRequest;
import com.example.model.BacktestResult;
import com.example.strategy.Collar;
import com.example.strategy.Put;
import com.example.strategy.Strategy;
import com.example.strategy.Swap;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
@Service
public class BacktestService {

	public BacktestResult runBacktest(BacktestRequest request) {
        Strategy strategy = createStrategy(request);
        double[] slopePrices = generateSlopePrices(request);
        double[] maturities = extractMaturities(request.getMonthlyData());
        double[] monthlyPrices = extractMonthlyPrices(request.getMonthlyData());
        double[] hedgedVolumes = generateHedgedVolumes(request);

        // Capture System.out to get the printed results
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        strategy.backtest(
            request.getEprms(),
            request.getPdd(),
            request.getSpread(),
            request.getInterestRate(),
            request.getVolatility(),
            maturities,
            slopePrices,
            hedgedVolumes,
            monthlyPrices
        );

        System.out.flush();
        System.setOut(old);

        // Parse the output to get the results
        String output = baos.toString();
        return parseBacktestResults(output);
    }


	    private double[] generateSlopePrices(BacktestRequest request) {
	        return slopeCalc(request.getInitialPrice(), request.getSlope(), request.getMonthlyData().length);
	    }


    private BacktestResult parseBacktestResults(String output) {
        double totalHedgedCost = 0;
        double totalUnhedgedCost = 0;
        double totalPremium = 0;
        double pnl = 0;
    	System.out.println(output);

        String[] lines = output.split("\n");
        for (String line : lines) {
            if (line.contains("Total Hedged Cost :")) {
                totalHedgedCost = extractValue(line);
            } else if (line.contains("Total Cost without Hedging :")) {
                totalUnhedgedCost = extractValue(line);
            } else if (line.contains("Total Premium is :")) {
                totalPremium = extractValue(line);
            } else if (line.contains("P&L :")) {
                pnl = extractValue(line);
            }
        }

        return new BacktestResult(totalHedgedCost, totalUnhedgedCost, totalPremium, pnl);
    }

    private double extractValue(String line) {
        return Double.parseDouble(line.split(":")[1].trim());
    }

    private Strategy createStrategy(BacktestRequest request) {
        switch (request.getStrategy()) {
            case "put":
                return new Put(request.getPutPercentage(), request.getSwapRate());
            case "swap":
                return new Swap(0, 0, request.getSwapRate());
            case "collar":
                return new Collar(request.getCollarLowerBound(), request.getCollarUpperBound());
            default:
                throw new IllegalArgumentException("Invalid strategy: " + request.getStrategy());
        }
    }


    private double[] extractMaturities(BacktestRequest.MonthlyData[] monthlyData) {
        return Arrays.stream(monthlyData)
                     .mapToDouble(data -> data.getMaturity())
                     .toArray();
    }

    private double[] extractMonthlyPrices(BacktestRequest.MonthlyData[] monthlyData) {
        return Arrays.stream(monthlyData)
                     .mapToDouble(data -> data.getPrice())
                     .toArray();
    }

    private double[] generateHedgedVolumes(BacktestRequest request) {
        double[] hedgedVolumes = new double[request.getMonthlyData().length];
        Arrays.fill(hedgedVolumes, request.getHedgedVolume() / request.getMonthlyData().length);
        return hedgedVolumes;
    }

    private double[] slopeCalc(double start, double slope, int total) {
        double[] res = new double[total];
        res[0] = start;
        for (int i = 1; i < total; i++) {
            res[i] = res[i-1] * (1 + slope / total);
        }
        return res;
    }
}