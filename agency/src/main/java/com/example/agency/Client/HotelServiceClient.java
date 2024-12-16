package com.example.agency.Client;

import com.example.agency.models.Offre;
import hotel_service.HotelServiceGrpc;
import hotel_service.HotelServiceOuterClass;
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

    /**
     * Appel au service gRPC pour récupérer les disponibilités.
     */
    public List<Offre> getDisponibilites(String city, String startDate, String endDate, int guests) {
        HotelServiceOuterClass.AvailabilityRequest request = HotelServiceOuterClass.AvailabilityRequest.newBuilder()
                .setCity(city)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setGuests(guests)
                .build();

        HotelServiceOuterClass.AvailabilityResponse response = stub.getAvailability(request);

        // Conversion des réponses gRPC en objets `Offre`.
        return response.getOffersList().stream()
                .map(this::convertGrpcOfferToModel)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une offre gRPC en modèle local.
     */
    private Offre convertGrpcOfferToModel(HotelServiceOuterClass.Offer grpcOffer) {
        Offre offre = new Offre();
        offre.setId(Long.valueOf(grpcOffer.getId()));
        offre.setPrixAgence(grpcOffer.getPrixAgence());
        offre.setNumberOfBeds(grpcOffer.getNumberOfBeds());
        offre.setPictureUrl(grpcOffer.getPictureUrl());
        return offre;
    }
}
