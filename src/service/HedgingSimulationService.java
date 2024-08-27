package service;

import model.SimulationRequest;
import model.SimulationResult;
import request.ForwardCurve;
import request.ForwardCurve.OptionData;
import simulation.Scenario;
import strategy.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


import java.io.IOException;

@Service
public class HedgingSimulationService {

    private static final String FIXED_TICKER = "NYMEX:CL";
    private static final Scenario FIXED_SCENARIO = new Scenario("Fixed Scenario", 80, 120, 1.0);

    public SimulationResult runSimulation(SimulationRequest request) throws IOException {
        Strategy strategy = createStrategy(request);
        List<OptionData> forwardCurve = ForwardCurve.forwardRequest(FIXED_TICKER);

        double totalHedgedCost = 0;
        double totalPremium = 0;

        for (int i = 0; i < request.getHedgedVolumes().length; i++) {
            double result = strategy.simulateOneVolume(
                request.getInitialPrice(),
                request.getStrikePrice(),
                request.getInterestRate(),
                i + 1, // duration is the current month
                FIXED_SCENARIO,
                request.getHedgedVolumes()[i],
                forwardCurve.subList(0, Math.min(i + 2, forwardCurve.size())), // forward curve up to this month
                request.getVolatility()
            );

            totalHedgedCost += result;
            // Assuming premium is 5% of the result for each month
            totalPremium += result * 0.05;
        }

        double totalUnhedgedCost = calculateUnhedgedCost(request);
        double pnl = totalUnhedgedCost - totalHedgedCost - totalPremium;

        return new SimulationResult(totalHedgedCost, totalPremium, pnl, totalUnhedgedCost);
    }

    private double calculateUnhedgedCost(SimulationRequest request) {
        double totalVolume = 0;
        for (double volume : request.getHedgedVolumes()) {
            totalVolume += volume;
        }
        return totalVolume * FIXED_SCENARIO.getPrice();
    }

    private Strategy createStrategy(SimulationRequest request) {
        switch (request.getStrategyType()) {
            case "put":
                return new Put(request.getPutPercentage(), request.getStrikePrice(), request.getExpiration() / 365.0);
            case "call":
                return new Call(request.getCallPercentage(), request.getStrikePrice(), request.getExpiration() / 365.0);
            case "both":
                Strategies strategies = new Strategies();
                strategies.addStrategy(new Put(request.getPutPercentage(), request.getStrikePrice(), request.getExpiration() / 365.0));
                strategies.addStrategy(new Call(request.getCallPercentage(), request.getStrikePrice(), request.getExpiration() / 365.0));
                return strategies;
            default:
                throw new IllegalArgumentException("Invalid strategy type");
        }
    }
}
