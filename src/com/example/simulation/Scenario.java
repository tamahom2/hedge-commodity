package com.example.simulation;

public class Scenario {
        String name;
        double lowerBound;
        double higherBound;
        double probability;

        public Scenario(String name, double lowerBound, double higherBound, double probability) {
            this.name = name;
            this.lowerBound = lowerBound;
            this.higherBound = higherBound;
            this.probability = probability;
        }
        
        public double getPrice() {
        	return (lowerBound+higherBound)/2;
        }
}
