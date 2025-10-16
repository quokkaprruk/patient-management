package com.pm.billingservice.grpc;

import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import com.pm.billingservice.dto.BillingRequestDTO;
import com.pm.billingservice.mapper.BillingMapper;
import com.pm.billingservice.model.Billing;
import com.pm.billingservice.service.BillingService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// acts like a Controller
@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);
    private final BillingService billingService;


    public BillingGrpcService(BillingService billingService) {
        this.billingService = billingService;
    }
    // StreamObserver return real-time data when sth update
    // Even though the method is declared void, it returns data to the client via the StreamObserver
    @Override
    public void createBillingAccount(billing.BillingRequest billingRequest, StreamObserver<billing.BillingResponse> responseObserver)
    {
        log.info("createBillingAccount request received : {}", billingRequest);

        // convert before passing to service (Service only handle domain logic and returns entities.)
        // it should only deal with domain-friendly objects (BillingService doesn’t need to know anything about gRPC.)
        BillingRequestDTO billingRequestDTO = BillingMapper.toDTO(billingRequest);
        Billing savedAccount = billingService.createAccount(billingRequestDTO);



        BillingResponse response = BillingMapper.toGrpcResponse(savedAccount);
        // GRPC can return multiple response - REST can return only 1 response



        responseObserver.onNext(response); // send res from serverGrpcService(billing-service) back to clientGrpcBillingService(patient-service)
        responseObserver.onCompleted(); // end the cycle
    }
}
