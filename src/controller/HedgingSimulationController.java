package controller;

import model.SimulationRequest;
import model.SimulationResult;
import service.HedgingSimulationService;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hedging")
public class HedgingSimulationController {

    @Autowired
    private HedgingSimulationService simulationService;

    @PostMapping("/simulate")
    public ResponseEntity<?> runSimulation(@RequestBody SimulationRequest request) {
        try {
            SimulationResult result = simulationService.runSimulation(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace(); // This will print the stack trace to your console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + e.getMessage());
        }
    }
}