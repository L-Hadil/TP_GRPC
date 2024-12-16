package com.example.agency.Client;

import hotel_service.AvailabilityRequest;
import hotel_service.AvailabilityResponse;
import hotel_service.HotelServiceGrpc;
import hotel_service.ReservationRequest;
import hotel_service.ReservationResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class HotelServiceClient {

    private final HotelServiceGrpc.HotelServiceBlockingStub blockingStub;

    public HotelServiceClient(String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = HotelServiceGrpc.newBlockingStub(channel);
    }

    public AvailabilityResponse getAvailability(AvailabilityRequest request) {
        return blockingStub.getAvailability(request);
    }

    public ReservationResponse makeReservation(ReservationRequest request) {
        return blockingStub.makeReservation(request);
    }
}
