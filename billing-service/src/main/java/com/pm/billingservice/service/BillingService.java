package com.pm.billingservice.service;

import com.pm.billingservice.dto.BillingRequestDTO;
import com.pm.billingservice.dto.BillingResponseDTO;
import com.pm.billingservice.mapper.BillingMapper;
import com.pm.billingservice.model.Billing;
import org.springframework.stereotype.Service;
import com.pm.billingservice.repo.BillingRepository;

import java.util.List;

@Service
public class BillingService {

    private final BillingRepository billingRepository;

    public BillingService(BillingRepository billingRepository) {
        this.billingRepository = billingRepository;
    }

    public Billing createAccount(BillingRequestDTO billingDTO) {

      /* *
      * Contains all core logic:
        Converts gRPC → DTO

        Converts DTO → Entity

        Saves to database

        Converts Entity → gRPC response X no because should handle domain logic and returns entities.
      *
      * */
        Billing entity = BillingMapper.toModel(billingDTO);
        return billingRepository.save(entity);
    }

    public List<BillingResponseDTO> getBillingAccounts(){
        List<Billing> billingAccounts = billingRepository.findAll();
        // before using lambda expression ex. billing-> BillingMapper.toDTO(billing)
        return billingAccounts.stream().map(BillingMapper::toDTO).toList();
    }
}
