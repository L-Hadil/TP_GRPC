package com.example.agency.CLI;

import com.example.agency.Client.HotelServiceClient;
import com.example.agency.utils.GrpcConverter;
import hotel_service.AvailabilityRequest;
import hotel_service.AvailabilityResponse;
import hotel_service.ReservationRequest;
import hotel_service.ReservationResponse;

import java.util.InputMismatchException;
import java.util.Scanner;

public class AgencyCLI {

    private final HotelServiceClient hotelServiceClient;

    public AgencyCLI(HotelServiceClient hotelServiceClient) {
        this.hotelServiceClient = hotelServiceClient;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Bienvenue dans l'interface de réservation d'hôtel ---");

        try {
            while (true) {
                System.out.println("\n1. Chercher des offres d'hôtels");
                System.out.println("2. Effectuer une réservation");
                System.out.println("3. Quitter");
                System.out.print("Votre choix : ");

                int choice = getValidatedIntInput(scanner);

                switch (choice) {
                    case 1:
                        searchOffers(scanner);
                        break;
                    case 2:
                        makeReservation(scanner);
                        break;
                    case 3:
                        System.out.println("Merci d'avoir utilisé notre application. À bientôt !");
                        return;
                    default:
                        System.out.println("Choix invalide. Veuillez réessayer.");
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private void searchOffers(Scanner scanner) {
        try {
            System.out.println("\n--- Recherche d'offres d'hôtel ---");

            System.out.print("Ville : ");
            String city = scanner.nextLine();

            System.out.print("Date d'arrivée (YYYY-MM-DD) : ");
            String startDate = scanner.nextLine();

            System.out.print("Date de départ (YYYY-MM-DD) : ");
            String endDate = scanner.nextLine();

            int stars = getValidatedIntInput(scanner, "Nombre d'étoiles minimum : ");
            int guests = getValidatedIntInput(scanner, "Nombre de personnes : ");

            AvailabilityRequest request = AvailabilityRequest.newBuilder()
                    .setCity(city)
                    .setStartDate(startDate)
                    .setEndDate(endDate)
                    .setStars(stars)
                    .setGuests(guests)
                    .build();

            AvailabilityResponse response = hotelServiceClient.getAvailability(request);

            if (response.getOffersList().isEmpty()) {
                System.out.println("Aucune offre trouvée pour les critères donnés.");
            } else {
                System.out.println("\n--- Offres disponibles ---");
                response.getOffersList().forEach(offer -> {
                    System.out.println(GrpcConverter.toReadableOffer(offer));
                    System.out.println("-----------------------------------------");
                });
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche d'offres : " + e.getMessage());
        }
    }

    private void makeReservation(Scanner scanner) {
        try {
            System.out.println("\n--- Réservation d'un hôtel ---");

            long offerId = getValidatedLongInput(scanner, "ID de l'offre choisie : ");

            System.out.print("Nom du client : ");
            String clientName = scanner.nextLine();

            System.out.print("Prénom du client : ");
            String clientSurname = scanner.nextLine();

            ReservationRequest reservationRequest = ReservationRequest.newBuilder()
                    .setOfferId(offerId)
                    .setClientName(clientName)
                    .setClientSurname(clientSurname)
                    .build();

            ReservationResponse reservationResponse = hotelServiceClient.makeReservation(reservationRequest);

            System.out.println("\n--- Réservation confirmée ---");
            System.out.println(GrpcConverter.toReadableReservation(reservationResponse.getReservation()));
        } catch (Exception e) {
            System.err.println("Erreur lors de la réservation : " + e.getMessage());
        }
    }

    private int getValidatedIntInput(Scanner scanner, String message) {
        while (true) {
            try {
                System.out.print(message);
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Veuillez entrer un nombre valide.");
                scanner.nextLine(); // Consommer l'entrée invalide
            }
        }
    }

    private int getValidatedIntInput(Scanner scanner) {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Veuillez entrer un choix valide.");
                scanner.nextLine(); // Consommer l'entrée invalide
            }
        }
    }

    private long getValidatedLongInput(Scanner scanner, String message) {
        while (true) {
            try {
                System.out.print(message);
                return scanner.nextLong();
            } catch (InputMismatchException e) {
                System.out.println("Veuillez entrer un ID valide.");
                scanner.nextLine(); // Consommer l'entrée invalide
            }
        }
    }
}
