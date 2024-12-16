package com.example.agency.services;

import com.example.agency.Client.HotelServiceClient;
import com.example.agency.utils.GrpcConverter;
import com.example.agency.models.Offre;
import com.example.agency.models.Reservation;

import hotel_service.AvailabilityRequest;
import hotel_service.AvailabilityResponse;
import hotel_service.ReservationRequest;
import hotel_service.ReservationResponse;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final HotelServiceClient hotelServiceClient;

    public ReservationService(HotelServiceClient hotelServiceClient) {
        this.hotelServiceClient = hotelServiceClient;
    }

    /**
     * Recherche des offres d'hôtels disponibles selon les critères donnés.
     *
     * @param city       Ville du séjour
     * @param startDate  Date d'arrivée
     * @param endDate    Date de départ
     * @param stars      Nombre minimum d'étoiles
     * @param guests     Nombre de personnes à héberger
     * @return Une liste d'offres correspondant aux critères
     */
    public List<Offre> findAvailableOffers(String city, String startDate, String endDate, int stars, int guests) {
        AvailabilityRequest request = AvailabilityRequest.newBuilder()
                .setCity(city)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStars(stars)
                .setGuests(guests)
                .build();

        AvailabilityResponse response = hotelServiceClient.getAvailability(request);

        return response.getOffersList().stream()
                .map(GrpcConverter::toEntityOffer) // Conversion du message gRPC en entité locale
                .collect(Collectors.toList());
    }

    /**
     * Réalise une réservation pour un hôtel donné.
     *
     * @param offerId       ID de l'offre choisie
     * @param clientName    Nom du client
     * @param clientSurname Prénom du client
     * @return Les détails de la réservation effectuée
     */
    public Reservation makeReservation(long offerId, String clientName, String clientSurname) {
        ReservationRequest request = ReservationRequest.newBuilder()
                .setOfferId(offerId)
                .setClientName(clientName)
                .setClientSurname(clientSurname)
                .build();

        ReservationResponse response = hotelServiceClient.makeReservation(request);

        return GrpcConverter.toEntityReservation(response.getReservation());
    }
}
