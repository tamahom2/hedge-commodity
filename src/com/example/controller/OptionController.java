package com.example.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.OptionPriceRequest;
import com.example.pricing.VanillaOptions;

@RestController
@RequestMapping("/api")
public class OptionController {

    @PostMapping("/option-price")
    public ResponseEntity<Map<String, Double>> calculateOptionPrice(@RequestBody OptionPriceRequest request) {
        double price = VanillaOptions.generalBlackScholes(
            request.getSpotPrice(),
            request.getStrikePrice(),
            request.getExpiration() / 365.0, // Convert days to years
            request.getInterestRate(),
            0, // Assuming b = 0 for simplicity
            request.getImpliedVolatility(),
            request.getType().equals("call") ? "c" : "p"
        );

        Map<String, Double> response = new HashMap<>();
        response.put("price", price);
        return ResponseEntity.ok(response);
    }
}