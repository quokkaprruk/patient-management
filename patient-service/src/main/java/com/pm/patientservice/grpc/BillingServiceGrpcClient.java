package com.pm.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel; // communication channel to gRPC server
import io.grpc.ManagedChannelBuilder; // builder for creating channels
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    // var to hold grpc client
    // provide blocking => synchronous client calls
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    // localhost:9001/BillingService/CreatePatientAccount
    // aws.grpc:123123/BillingService/CreatePatientAccount
    public BillingServiceGrpcClient(
            @Value("${billing.service.address:localhost}") String serverAddress,
            @Value("${billing.service.grpc.port:9001}") int serverPort) {

        log.info("Connecting to Billing Service GRPC service at {}:{}", serverAddress, serverPort);

        // channel is the network connection to the gRPC server
        // creates a builder for connecting to gRPC server.
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort).usePlaintext().build();

        // local “proxy” object that represents the remote gRPC service.
        // call a method on the stub, it actually sends a request over the network to the other microservice.
        blockingStub = BillingServiceGrpc.newBlockingStub(channel);

    }

    public BillingResponse createBillingAccount(String patientId, String firstName, String lastName, String email){
        BillingRequest request = BillingRequest.newBuilder().setPatientId(patientId).setFirstName(firstName).setLastName(lastName).setEmail(email).build();
        BillingResponse response = blockingStub.createBillingAccount(request);
        log.info("Received billing response from billing service via GRPC: {}", response);
        return response;
    }


}
