package org.example.hotel_service.services;

import hotel_service.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.hotel_service.models.Offre;
import org.example.hotel_service.models.Reservation;

import org.example.hotel_service.repositories.AgenceRepository;
import org.example.hotel_service.repositories.OffreRepository;
import org.example.hotel_service.repositories.ReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class HotelServiceImpl extends HotelServiceGrpc.HotelServiceImplBase {

    private final OffreRepository offreRepository;
    private final AgenceRepository agenceRepository;
    private final ReservationRepository reservationRepository;

    public HotelServiceImpl(OffreRepository offreRepository, AgenceRepository agenceRepository, ReservationRepository reservationRepository) {
        this.offreRepository = offreRepository;
        this.agenceRepository = agenceRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void getAvailability(AvailabilityRequest request, StreamObserver<AvailabilityResponse> responseObserver) {
        try {
            // Vérification des identifiants de l'agence
            if (!agenceRepository.existsByLoginAndMotDePasse(request.getAgencyUsername(), request.getAgencyPassword())) {
                responseObserver.onError(
                        Status.PERMISSION_DENIED
                                .withDescription("Invalid agency credentials.")
                                .asRuntimeException()
                );
                return;
            }

            // Conversion des dates
            LocalDate startDate = LocalDate.parse(request.getStartDate());
            LocalDate endDate = LocalDate.parse(request.getEndDate());

            if (startDate.isAfter(endDate)) {
                responseObserver.onError(
                        Status.INVALID_ARGUMENT
                                .withDescription("Start date cannot be after end date.")
                                .asRuntimeException()
                );
                return;
            }

            // Filtrage des offres disponibles
            List<Offer> offers = offreRepository.findAll().stream()
                    .filter(offre -> offre.getHotel().getAdresse().getCity().equalsIgnoreCase(request.getCity()))
                    .filter(offre -> offre.getHotel().getNombreEtoiles() >= request.getStars())
                    .filter(offre -> offre.getNumberOfBeds() >= request.getGuests())
                    .filter(offre -> !offre.getAvailabilityStart().isAfter(endDate) &&
                            !offre.getAvailabilityEnd().isBefore(startDate))
                    .map(this::convertToProtoOffer)
                    .collect(Collectors.toList());

            if (offers.isEmpty()) {
                responseObserver.onError(
                        Status.NOT_FOUND
                                .withDescription("No offers available for the given criteria.")
                                .asRuntimeException()
                );
                return;
            }

            // Construction de la réponse
            AvailabilityResponse response = AvailabilityResponse.newBuilder()
                    .addAllOffers(offers)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("An internal server error occurred.")
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void makeReservation(ReservationRequest request, StreamObserver<ReservationResponse> responseObserver) {
        try {
            // Vérification des identifiants de l'agence
            if (!agenceRepository.existsByLoginAndMotDePasse(request.getAgencyUsername(), request.getAgencyPassword())) {
                responseObserver.onError(
                        Status.PERMISSION_DENIED
                                .withDescription("Invalid agency credentials.")
                                .asRuntimeException()
                );
                return;
            }

            // Vérification de l'offre
            Offre offre = offreRepository.findById(request.getOfferId())
                    .orElseThrow(() -> Status.NOT_FOUND
                            .withDescription("Offer not found.")
                            .asRuntimeException());

            // Vérification si l'offre est déjà réservée
            if (reservationRepository.existsByOffreId(offre.getId())) {
                responseObserver.onError(
                        Status.ALREADY_EXISTS
                                .withDescription("The offer is already reserved.")
                                .asRuntimeException()
                );
                return;
            }

            // Création de la réservation
            Reservation reservation = new Reservation();
            reservation.setOffre(offre);
            reservation.setNomClient(request.getClientName());
            reservation.setPrenomClient(request.getClientSurname());
            reservation.setDateDebut(offre.getAvailabilityStart());
            reservation.setDateFin(offre.getAvailabilityEnd());
            reservation.setStatus("Confirmed");

            reservation = reservationRepository.save(reservation);

            // Conversion en message Protobuf
            hotel_service.Reservation protoReservation = convertToProtoReservation(reservation);

            // Construction de la réponse
            ReservationResponse response = ReservationResponse.newBuilder()
                    .setStatus("Confirmed")
                    .setReservation(protoReservation)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("An error occurred while processing the request.")
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    // Méthode utilitaire pour convertir une entité Offre en message Protobuf Offer
    private Offer convertToProtoOffer(Offre offre) {
        return Offer.newBuilder()
                .setId(offre.getId())
                .setRoomType("Chambre Double") // Exemple, ajustez selon vos données
                .setAgencyUsername(offre.getAgencyUsername())
                .setAgencyPassword(offre.getAgencyPassword())
                .setAvailabilityStart(offre.getAvailabilityStart().toString())
                .setAvailabilityEnd(offre.getAvailabilityEnd().toString())
                .setPrixAgence(offre.getPrixAgence())
                .setNumberOfBeds(offre.getNumberOfBeds())
                .setPictureUrl(offre.getPictureUrl())
                .build();
    }

    // Méthode utilitaire pour convertir une entité Reservation en message Protobuf Reservation
    private hotel_service.Reservation convertToProtoReservation(Reservation reservation) {
        return hotel_service.Reservation.newBuilder()
                .setId(reservation.getId())
                .setNomClient(reservation.getNomClient())
                .setPrenomClient(reservation.getPrenomClient())
                .setDateDebut(reservation.getDateDebut().toString())
                .setDateFin(reservation.getDateFin().toString())
                .setStatus(reservation.getStatus())
                .setOffre(convertToProtoOffer(reservation.getOffre()))
                .build();
    }
}
