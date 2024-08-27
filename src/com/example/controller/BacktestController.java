package com.example.controller;

import com.example.model.BacktestRequest;
import com.example.model.BacktestResult;
import com.example.service.BacktestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BacktestController {

    private final BacktestService backtestService;

    @Autowired
    public BacktestController(BacktestService backtestService) {
        this.backtestService = backtestService;
    }

    @PostMapping("/backtest")
    public ResponseEntity<?> runBacktest(@RequestBody BacktestRequest request) {
        try {
            BacktestResult result = backtestService.runBacktest(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error running backtest: " + e.getMessage());
        }
    }
}