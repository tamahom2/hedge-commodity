package com.example.service;

import com.example.model.ContractPricingRequest;
import com.example.pricing.*;
import org.springframework.stereotype.Service;

@Service
public class ContractPricingService {

	public double priceContract(ContractPricingRequest request) {
        switch (request.getOptionType()) {
            case "vanilla":
                return priceVanillaOption(request);
            case "asian":
                return priceAsianOption(request);
            case "barrier":
                return priceBarrierOption(request);
            case "doubleBarrier":
                return priceDoubleBarrierOption(request);
            case "lookback":
                return priceLookbackOption(request);
            default:
                throw new IllegalArgumentException("Unsupported option type: " + request.getOptionType());
        }
    }

    private double priceVanillaOption(ContractPricingRequest request) {
        switch (request.getPricingFunction()) {
            case "blackScholes":
                return VanillaOptions.blackScholes(request.getS(), request.getK(), request.getR(), request.getT(), request.getV(), request.getCallPutFlag());
            case "generalBlackScholes":
                return VanillaOptions.generalBlackScholes(request.getS(), request.getK(), request.getT(), request.getR(), request.getB(), request.getV(), request.getCallPutFlag());
            case "monteCarlo":
                return VanillaOptions.monteCarloPrice(request.getS(), request.getK(), request.getR(), request.getT(), request.getV(), request.getCallPutFlag(), request.getNumSimulations(), request.getNumSteps());
            case "black76":
                return VanillaOptions.black76(request.getS(), request.getK(), request.getR(), request.getT(), request.getV(), request.getCallPutFlag());
            default:
                throw new IllegalArgumentException("Unsupported vanilla pricing function: " + request.getPricingFunction());
        }
    }

    private double priceAsianOption(ContractPricingRequest request) {
        switch (request.getPricingFunction()) {
            case "geometricAverageRate":
                return AsianOptions.GeometricAverageRateOption(request.getCallPutFlag(), request.getS(), request.getSA(), request.getK(), request.getT(), request.getT2(), request.getR(), request.getB(), request.getV());
            case "turnbullWakeman":
                return AsianOptions.TurnbullWakemanAsian(request.getCallPutFlag(), request.getS(), request.getSA(), request.getK(), request.getT(), request.getT2(), request.getTau(), request.getR(), request.getB(), request.getV());
            case "levyAsian":
                return AsianOptions.LevyAsian(request.getCallPutFlag(), request.getS(), request.getSA(), request.getK(), request.getT(), request.getT2(), request.getR(), request.getB(), request.getV());
            case "monteCarloGeometric":
                return AsianOptions.MonteCarloGeometricAsian(request.getCallPutFlag(), request.getS(), request.getK(), request.getT(), request.getR(), request.getB(), request.getV(), request.getNumSimulations(), request.getNumSteps());
            case "monteCarloArithmetic":
                return AsianOptions.MonteCarloArithmeticAsian(request.getCallPutFlag(), request.getS(), request.getK(), request.getT(), request.getR(), request.getB(), request.getV(), request.getNumSimulations(), request.getNumSteps());
            default:
                throw new IllegalArgumentException("Unsupported Asian pricing function: " + request.getPricingFunction());
        }
    }

    private double priceBarrierOption(ContractPricingRequest request) {
        if (request.getBarrier() == null) {
            throw new IllegalArgumentException("Barrier value is required for barrier options");
        }
        return BarrierOptions.standardBarrier(request.getBarrierType(), request.getS(), request.getK(), request.getBarrier(), 0, request.getT(), request.getR(), request.getB(), request.getV());
    }

    private double priceDoubleBarrierOption(ContractPricingRequest request) {
        if (request.getLowerBarrier() == null || request.getUpperBarrier() == null) {
            throw new IllegalArgumentException("Both lower and upper barrier values are required for double barrier options");
        }
        return BarrierOptions.doubleBarrier(request.getBarrierType(), request.getS(), request.getK(), request.getLowerBarrier(), request.getUpperBarrier(), request.getT(), request.getR(), request.getB(), request.getV(), 0, 0);
    }

    private double priceLookbackOption(ContractPricingRequest request) {
        double t1 = request.getT() / 2; // For simplicity, using T/2 as the lookback period
        return BarrierOptions.lookBarrier(request.getCallPutFlag() + "uo", request.getS(), request.getK(), request.getK(), t1, request.getT(), request.getR(), request.getB(), request.getV());
    }
}