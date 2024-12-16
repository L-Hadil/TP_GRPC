package com.example.agency.services;

import com.example.agency.Repositories.ClientRepository;
import com.example.agency.Repositories.ReservationRepository;
import com.example.agency.models.Client;
import com.example.agency.models.Reservation;

import hotel_service.HotelServiceGrpc;
import hotel_service.HotelServiceOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ClientRepository clientRepository;

    private final HotelServiceGrpc.HotelServiceBlockingStub stub;

    public ReservationService() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        this.stub = HotelServiceGrpc.newBlockingStub(channel);
    }

    /**
     * Effectue une réservation via le service gRPC et enregistre localement.
     */
    public Reservation makeReservation(String offerId, String clientName, String clientEmail, String paymentCard, String expiryDate) {
        // Trouver ou créer le client.
        Client client = clientRepository.findByEmail(clientEmail).orElseGet(() -> {
            Client newClient = new Client();
            String[] nameParts = clientName.split(" ");
            newClient.setNom(nameParts[0]);
            newClient.setPrenom(nameParts.length > 1 ? nameParts[1] : "");
            newClient.setEmail(clientEmail);
            return clientRepository.save(newClient);
        });

        // Construire la requête gRPC pour effectuer la réservation.
        HotelServiceOuterClass.ReservationRequest grpcRequest = HotelServiceOuterClass.ReservationRequest.newBuilder()
                .setOfferId(Long.parseLong(offerId))
                .setClientName(clientName)
                .setClientEmail(clientEmail)
                .setPaymentCard(paymentCard)
                .setExpiryDate(expiryDate)
                .build();

        HotelServiceOuterClass.ReservationResponse grpcResponse = stub.makeReservation(grpcRequest);

        // Enregistrer la réservation localement.
        Reservation reservation = new Reservation();
        reservation.setId(Long.valueOf(grpcResponse.getReservationId()));
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setStatut(grpcResponse.getStatus());
        reservation.setClient(client);
        reservationRepository.save(reservation);

        return reservation;
    }
}
