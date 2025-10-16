package com.pm.billingservice.mapper;

import billing.BillingRequest;
import billing.BillingResponse;
import com.pm.billingservice.dto.BillingResponseDTO;
import com.pm.billingservice.dto.BillingRequestDTO;
import com.pm.billingservice.model.Billing;

import java.time.LocalDateTime;
import java.util.UUID;

public class BillingMapper {

    // convert grpc to DTO
    public static BillingRequestDTO toDTO(BillingRequest billingRequest) {
        BillingRequestDTO billingRequestDTO = new BillingRequestDTO();
        billingRequestDTO.setPatientId(billingRequest.getPatientId());
        billingRequestDTO.setFirstName(billingRequest.getFirstName());
        billingRequestDTO.setLastName(billingRequest.getLastName());
        billingRequestDTO.setEmail(billingRequest.getEmail());
        billingRequestDTO.setStatus("ACTIVE");
        billingRequestDTO.setCreatedAt(LocalDateTime.now());
        return billingRequestDTO;
    }

    // convert DTO to Model
    public static Billing toModel(BillingRequestDTO billingRequestDTO) {
        Billing billingAccount = new Billing();
        // convert string to UUID
        billingAccount.setPatientId(UUID.fromString(billingRequestDTO.getPatientId()));
        billingAccount.setFirstName(billingRequestDTO.getFirstName());
        billingAccount.setLastName(billingRequestDTO.getLastName());
        billingAccount.setEmail(billingRequestDTO.getEmail());
        billingAccount.setStatus("ACTIVE");
        billingAccount.setCreatedAt(LocalDateTime.now());

        return billingAccount;
    }

    // convert entity to DTO
    public static BillingResponseDTO toDTO(Billing billing){
        BillingResponseDTO billingDTO = new BillingResponseDTO();
        billingDTO.setAccountId(billing.getId().toString());
        billingDTO.setStatus(billing.getStatus());

        return billingDTO;
    }

    // convert Model to grpc
    public static BillingResponse toGrpcResponse(Billing billingAccount) {
        return BillingResponse.newBuilder()
                .setAccountId(billingAccount.getId().toString())
                .setStatus(billingAccount.getStatus())
                .build();
    }



}
