package com.example.service;

import com.example.model.MonthlyStrategy;
import com.example.model.OptionStrategy;
import com.example.model.SimulationRequest;
import com.example.model.SimulationResult;
import com.example.request.ForwardCurve;
import com.example.request.ForwardCurve.OptionData;
import com.example.simulation.Scenario;
import com.example.strategy.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


import java.io.IOException;

@Service
public class HedgingSimulationService {

    private static final String FIXED_TICKER = "NYMEX:CL";

    public SimulationResult runSimulation(SimulationRequest request) throws IOException {
        List<OptionData> forwardCurve = ForwardCurve.forwardRequest(FIXED_TICKER);
        
        Scenario userScenario = new Scenario(
            request.getScenario().getName(),
            request.getScenario().getLowerBound(),
            request.getScenario().getUpperBound(),
            request.getScenario().getProbability()
        );
        
        double totalHedgedCost = 0;
        double totalUnhedgedCost = 0;
        double totalPremium = 0;

        for (MonthlyStrategy monthlyStrategy : request.getMonthlyStrategies()) {
            Strategies strategies = new Strategies();
            
            for (OptionStrategy optionStrategy : monthlyStrategy.getOptions()) {
                Strategy strategy = createStrategy(optionStrategy);
                strategies.addStrategy(strategy);
            }
            
            double[] monthlyHedgedCost = strategies.simulateOneVolume(
                request.getInitialPrice(),
                request.getInterestRate(),
                30, // Assuming 30 days per month
                userScenario,
                monthlyStrategy.getVolume(),
                forwardCurve,
                0 // We're not using a single volatility anymore
            );

            totalHedgedCost += monthlyHedgedCost[0];
            totalPremium += monthlyHedgedCost[1];
            totalUnhedgedCost += monthlyStrategy.getVolume() * userScenario.getPrice();
        }

        double pnl = totalUnhedgedCost - totalHedgedCost;

        return new SimulationResult(totalHedgedCost, totalPremium, pnl, totalUnhedgedCost);
    }

    private Strategy createStrategy(OptionStrategy optionStrategy) {
        if (optionStrategy.getType().equals("call")) {
            return new Call(optionStrategy.getPercentage(), optionStrategy.getStrikePrice(), optionStrategy.getExpiration() / 365.0, optionStrategy.getImpliedVolatility());
        } else if (optionStrategy.getType().equals("put")) {
            return new Put(optionStrategy.getPercentage(), optionStrategy.getStrikePrice(), optionStrategy.getExpiration() / 365.0, optionStrategy.getImpliedVolatility());
        } else {
            throw new IllegalArgumentException("Invalid option type");
        }
    }
}