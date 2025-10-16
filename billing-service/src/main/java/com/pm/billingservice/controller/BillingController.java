package com.pm.billingservice.controller;

import com.pm.billingservice.dto.BillingResponseDTO;
import com.pm.billingservice.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/billings") // http://localhost:4000/patients
@Tag(name = "Billing", description = "API for managing Billings")
public class BillingController {
    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    //get billing accounts from Service and return the response to client
    @GetMapping
    @Operation(summary = "Get Billing Accounts")
    public ResponseEntity<List<BillingResponseDTO>> getBillingAccounts(){
        List<BillingResponseDTO> billingAccounts = billingService.getBillingAccounts();
        return ResponseEntity.ok().body(billingAccounts);
    }
}
