package com.example.agency.utils;

import com.example.agency.models.Offre;
import com.example.agency.models.Reservation;
import hotel_service.HotelServiceOuterClass;

/**
 * Classe utilitaire pour convertir entre les modèles locaux et les messages gRPC.
 */
public class GrpcConverter {

    /**
     * Convertit un objet Offre local en message gRPC Offer.
     */
    public static HotelServiceOuterClass.Offer toGrpcOffer(Offre offre) {
        return HotelServiceOuterClass.Offer.newBuilder()
                .setId(offre.getId())
                .setPrixAgence(offre.getPrixAgence())
                .setNumberOfBeds(offre.getNumberOfBeds())
                .setPictureUrl(offre.getPictureUrl() != null ? offre.getPictureUrl() : "")
                .build();
    }

    /**
     * Convertit un message gRPC Offer en objet Offre local.
     */
    public static Offre toEntityOffer(HotelServiceOuterClass.Offer grpcOffer) {
        Offre offre = new Offre();
        offre.setId(grpcOffer.getId());
        offre.setPrixAgence(grpcOffer.getPrixAgence());
        offre.setNumberOfBeds(grpcOffer.getNumberOfBeds());
        offre.setPictureUrl(grpcOffer.getPictureUrl());
        return offre;
    }

    /**
     * Convertit un objet Reservation local en message gRPC ReservationResponse.
     */
    public static HotelServiceOuterClass.ReservationResponse toGrpcReservationResponse(Reservation reservation) {
        return HotelServiceOuterClass.ReservationResponse.newBuilder()
                .setReservationId(reservation.getId())
                .setStatus(reservation.getStatut())
                .build();
    }

    /**
     * Convertit un message gRPC ReservationResponse en objet Reservation local.
     */
    public static Reservation toEntityReservation(HotelServiceOuterClass.ReservationResponse grpcResponse) {
        Reservation reservation = new Reservation();
        reservation.setId(Long.valueOf(grpcResponse.getReservationId()));
        reservation.setStatut(grpcResponse.getStatus());
        return reservation;
    }
}
