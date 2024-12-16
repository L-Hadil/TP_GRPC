package com.example.agency.services;

import com.example.agency.Repositories.ClientRepository;
import com.example.agency.Repositories.CarteBancaireRepository;
import com.example.agency.models.Client;
import com.example.agency.models.CarteBancaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CarteBancaireRepository carteBancaireRepository;

    /**
     * Trouver un client existant par email ou le créer.
     * @param email Email du client.
     * @param clientData Informations supplémentaires du client (nom, prénom).
     * @return L'identifiant du client existant ou nouvellement créé.
     */
    public Long findOrCreateClient(String email, Map<String, Object> clientData) {
        validateClientData(email, clientData);

        return clientRepository.findByEmail(email)
                .map(Client::getId) // Si le client existe, retourne son ID.
                .orElseGet(() -> {
                    // Sinon, crée un nouveau client.
                    Client client = new Client();
                    client.setEmail(email);
                    client.setNom(clientData.get("clientName").toString());
                    client.setPrenom(clientData.get("clientSurname").toString());
                    return clientRepository.save(client).getId();
                });
    }

    /**
     * Ajouter ou mettre à jour une carte bancaire pour un client donné.
     * @param clientId Identifiant du client.
     * @param cardNumber Numéro de carte bancaire.
     * @param expiryDate Date d'expiration de la carte.
     * @return L'identifiant de la carte bancaire ajoutée ou mise à jour.
     */
    public Long addOrUpdateCard(Long clientId, String cardNumber, String expiryDate) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("No client found with ID " + clientId));

        // Recherche d'une carte existante pour ce client ou création d'une nouvelle.
        CarteBancaire card = carteBancaireRepository.findByClient(client)
                .orElse(new CarteBancaire());
        card.setClient(client);
        card.setMaskedCardNumber(maskCardNumber(cardNumber));
        card.setExpiryDate(expiryDate);

        return carteBancaireRepository.save(card).getId();
    }

    /**
     * Validation des données client avant traitement.
     * @param email Email du client.
     * @param clientData Données supplémentaires comme le nom et prénom.
     */
    private void validateClientData(String email, Map<String, Object> clientData) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (!clientData.containsKey("clientName") || !clientData.containsKey("clientSurname")) {
            throw new IllegalArgumentException("Client name and surname are required.");
        }
    }

    /**
     * Masquer le numéro de carte bancaire pour des raisons de sécurité.
     * @param cardNumber Numéro de carte bancaire complet.
     * @return Numéro de carte masqué au format : **** **** **** 1234.
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            throw new IllegalArgumentException("Invalid card number.");
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
