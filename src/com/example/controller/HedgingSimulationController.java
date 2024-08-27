package com.example.controller;

import com.example.model.SimulationRequest;
import com.example.model.SimulationResult;
import com.example.service.HedgingSimulationService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hedging")
public class HedgingSimulationController {

    @Autowired
    private HedgingSimulationService simulationService;

    @PostMapping(value = "/simulate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runSimulation(@RequestBody SimulationRequest request) {
        try {
            System.out.println("Received request: " + request);  // Add this line for debugging
            SimulationResult result = simulationService.runSimulation(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + e.getMessage());
        }
    }
}