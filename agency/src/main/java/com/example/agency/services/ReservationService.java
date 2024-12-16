package com.example.agency.services;


import com.example.agency.models.Client;
import com.example.agency.models.Reservation;
import com.example.agency.models.Offre;
import com.example.agency.Repositories.ReservationRepository;
import com.example.agency.Repositories.ClientRepository;
import hotel_service.HotelServiceGrpc;
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

    public Reservation makeReservation(String offerId, String clientName, String clientEmail, String paymentCard, String expiryDate) {
        // Vérifier ou créer un client
        Client client = clientRepository.findByEmail(clientEmail).orElseGet(() -> {
            Client newClient = new Client();
            newClient.setNom(clientName.split(" ")[0]);
            newClient.setPrenom(clientName.split(" ")[1]);
            newClient.setEmail(clientEmail);
            return clientRepository.save(newClient);
        });

        // Appel gRPC pour effectuer la réservation
        HotelServiceOuterClass.ReservationRequest grpcRequest = HotelServiceOuterClass.ReservationRequest.newBuilder()
                .setOfferId(offerId)
                .setClientName(clientName)
                .setClientEmail(clientEmail)
                .setPaymentCard(paymentCard)
                .setExpiryDate(expiryDate)
                .build();

        HotelServiceOuterClass.ReservationResponse grpcResponse = stub.makeReservation(grpcRequest);

        // Enregistrer localement la réservation
        Reservation reservation = new Reservation();
        reservation.setId(Long.valueOf(grpcResponse.getReservationId()));
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setStatut(grpcResponse.getStatus());
        reservation.setClient(client);
        reservationRepository.save(reservation);

        return reservation;
    }
}
