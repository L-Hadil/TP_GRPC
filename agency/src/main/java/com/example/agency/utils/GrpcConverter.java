package com.example.agency.utils;


import com.example.agency.models.Offre;
import com.example.agency.models.Reservation;

import java.time.LocalDate;

public class GrpcConverter {

    // Convertit une entité locale Offre en message gRPC Offer
    public static HotelServiceOuterClass.Offer toGrpcOffer(Offre offre) {
        return HotelServiceOuterClass.Offer.newBuilder()
                .setId(offre.getId())
                .setPrixAgence(offre.getPrixAgence())
                .setNumberOfBeds(offre.getNumberOfBeds())
                .setPictureUrl(offre.getPictureUrl() != null ? offre.getPictureUrl() : "")
                .build();
    }

    // Convertit un message gRPC Offer en entité locale Offre
    public static Offre toEntityOffer(HotelServiceOuterClass.Offer grpcOffer) {
        Offre offre = new Offre();
        offre.setId(grpcOffer.getId());
        offre.setPrixAgence(grpcOffer.getPrixAgence());
        offre.setNumberOfBeds(grpcOffer.getNumberOfBeds());
        offre.setPictureUrl(grpcOffer.getPictureUrl());
        return offre;
    }

    // Convertit une entité locale Reservation en message gRPC Reservation
    public static HotelServiceOuterClass.Reservation toGrpcReservation(Reservation reservation) {
        return HotelServiceOuterClass.Reservation.newBuilder()
                .setId(reservation.getId())
                .setNomClient(reservation.getNomClient())
                .setPrenomClient(reservation.getPrenomClient())
                .setDateDebut(reservation.getDateDebut().toString())
                .setDateFin(reservation.getDateFin().toString())
                .setStatus(reservation.getStatus())
                .build();
    }

    // Convertit un message gRPC Reservation en entité locale Reservation
    public static Reservation toEntityReservation(HotelServiceOuterClass.Reservation grpcReservation) {
        Reservation reservation = new Reservation();
        reservation.setId(grpcReservation.getId());
        reservation.setNomClient(grpcReservation.getNomClient());
        reservation.setPrenomClient(grpcReservation.getPrenomClient());
        reservation.setDateDebut(LocalDate.parse(grpcReservation.getDateDebut()));
        reservation.setDateFin(LocalDate.parse(grpcReservation.getDateFin()));
        reservation.setStatus(grpcReservation.getStatus());
        return reservation;
    }
}
