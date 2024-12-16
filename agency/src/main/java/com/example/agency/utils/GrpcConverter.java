package com.example.agency.utils;

import agency_service.AgencyServiceOuterClass;
import com.example.agency.models.Offre;
import com.example.agency.models.Reservation;

public class GrpcConverter {

    /**
     * Convertit une entité JPA Offre en message gRPC Offer.
     */
    public static AgencyServiceOuterClass.Offer toGrpcOffer(Offre offre) {
        return AgencyServiceOuterClass.Offer.newBuilder()
                .setOfferId(String.valueOf(offre.getId()))
                .setPrice(offre.getPrixAgence())
                .setNumberOfBeds(offre.getNumberOfBeds())
                .setPictureUrl(offre.getPictureUrl())
                .build();
    }

    /**
     * Convertit une entité JPA Reservation en message gRPC ReservationResponse.
     */
    public static AgencyServiceOuterClass.ReservationResponse toGrpcReservationResponse(Reservation reservation) {
        return AgencyServiceOuterClass.ReservationResponse.newBuilder()
                .setReservationId(String.valueOf(reservation.getId()))
                .setStatus(reservation.getStatut())
                .build();
    }

    /**
     * Convertit un message gRPC Offer en entité JPA Offre.
     */
    public static Offre toEntityOffer(AgencyServiceOuterClass.Offer grpcOffer) {
        Offre offre = new Offre();
        offre.setId(Long.valueOf(grpcOffer.getOfferId()));
        offre.setPrixAgence(grpcOffer.getPrice());
        offre.setNumberOfBeds(grpcOffer.getNumberOfBeds());
        offre.setPictureUrl(grpcOffer.getPictureUrl());
        return offre;
    }
}
