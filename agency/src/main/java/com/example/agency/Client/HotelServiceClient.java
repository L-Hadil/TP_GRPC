package com.example.agency.Client;


import com.example.agency.models.Offre;
import hotel_service.HotelServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelServiceClient {

    private final HotelServiceGrpc.HotelServiceBlockingStub stub;

    public HotelServiceClient() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        this.stub = HotelServiceGrpc.newBlockingStub(channel);
    }

    public List<Offre> getDisponibilites(String city, String startDate, String endDate, int guests) {
        HotelServiceOuterClass.AvailabilityRequest request = HotelServiceOuterClass.AvailabilityRequest.newBuilder()
                .setCity(city)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setNumberOfGuests(guests)
                .build();

        HotelServiceOuterClass.AvailabilityResponse response = stub.checkAvailability(request);

        return response.getOffersList().stream()
                .map(this::convertGrpcOfferToModel)
                .collect(Collectors.toList());
    }

    private Offre convertGrpcOfferToModel(HotelServiceOuterClass.Offer grpcOffer) {
        Offre offre = new Offre();
        offre.setId(Long.valueOf(grpcOffer.getOfferId()));
        offre.setPrixAgence(grpcOffer.getPrice());
        offre.setNumberOfBeds(grpcOffer.getNumberOfBeds());
        offre.setPictureUrl(grpcOffer.getPictureUrl());
        return offre;
    }
}
