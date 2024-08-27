package com.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContractPricingRequest {
	 private String optionType;
	    private String pricingFunction;
	    
	    @JsonProperty("S")
	    private double s;
	    
	    @JsonProperty("K")
	    private double k;
	    
	    @JsonProperty("T")
	    private double t;
	    
	    private double r;
	    private double b;
	    private double v;
	    private String callPutFlag;
	    private String barrierType;
	    private Double barrier;
	    private Double lowerBarrier;
	    private Double upperBarrier;
	    @JsonProperty("SA")
	    private Double sa;
	    
	    @JsonProperty("T2")
	    private Double t2;
	    private Double tau;
	    private Integer numSimulations;
	    private Integer numSteps;

    // Getters and setters
    public String getOptionType() { return optionType; }
    public void setOptionType(String optionType) { this.optionType = optionType; }
    
    public double getS() { return s; }
    public void setS(double s) { this.s = s; }
    
    public double getK() { return k; }
    public void setK(double k) { this.k = k; }
    
    public double getT() { return t; }
    public void setT(double t) { this.t = t; }
    
    public double getR() { return r; }
    public void setR(double r) { this.r = r; }
    
    public double getB() { return b; }
    public void setB(double b) { this.b = b; }
    
    public double getV() { return v; }
    public void setV(double v) { this.v = v; }
    
    public String getPricingFunction() {
        return pricingFunction;
    }

    public void setPricingFunction(String pricingFunction) {
        this.pricingFunction = pricingFunction;
    }
    
    public String getCallPutFlag() { return callPutFlag; }
    public void setCallPutFlag(String callPutFlag) { this.callPutFlag = callPutFlag; }
    
    public Double getBarrier() { return barrier; }
    public void setBarrier(Double barrier) { this.barrier = barrier; }
    
    public Double getLowerBarrier() { return lowerBarrier; }
    public void setLowerBarrier(Double lowerBarrier) { this.lowerBarrier = lowerBarrier; }
    
    public Double getUpperBarrier() { return upperBarrier; }
    public void setUpperBarrier(Double upperBarrier) { this.upperBarrier = upperBarrier; }
	public Double getSA() {
		return sa;
	}
	public void setSA(Double sA) {
		sa = sA;
	}
	public Double getT2() {
		return t2;
	}
	public void setT2(Double t2) {
		this.t2 = t2;
	}
	public Double getTau() {
		return tau;
	}
	public void setTau(Double tau) {
		this.tau = tau;
	}
	public Integer getNumSimulations() {
		return numSimulations;
	}
	public void setNumSimulations(Integer numSimulations) {
		this.numSimulations = numSimulations;
	}
	public Integer getNumSteps() {
		return numSteps;
	}
	public void setNumSteps(Integer numSteps) {
		this.numSteps = numSteps;
	}
	
	public String getBarrierType() {
		return barrierType;
	}
	public void setBarrierType(String barrierType) {
		this.barrierType = barrierType;
	}
}