package com.example.controller;

import com.example.model.ContractPricingRequest;
import com.example.service.ContractPricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ContractPricingController {

    @Autowired
    private ContractPricingService contractPricingService;

    @PostMapping("/api/price-contract")
    public ResponseEntity<?> priceContract(@RequestBody ContractPricingRequest request) {
        try {
            double price = contractPricingService.priceContract(request);

            return ResponseEntity.ok(Map.of("price", price));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}