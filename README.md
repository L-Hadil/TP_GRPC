### README - TP_GRPC: Application de Réservation d’Hôtels

---

#### **Description**
Ce projet met en œuvre une application de réservation d’hôtels basée sur l’architecture **gRPC**, avec des interactions client-serveur. L’application est composée de trois modules :
1. **Common** : Contient les fichiers `proto` et les classes générées.
2. **HotelService** (Serveur) : Fournit des services gRPC pour rechercher des offres et effectuer des réservations.
3. **Agency** (Client) : Consomme les services gRPC et propose une interface CLI pour interagir avec les utilisateurs.

---

### **Prérequis**
1. **Java 17** installé.
2. **Maven** configuré.
3. **MySQL** installé avec deux bases de données :
    - **Base HotelService** : Contient les hôtels, offres, et réservations.
    - **Base Agency** : Contient les informations des agences.
4. **Protoc Compiler** : Pour générer les classes à partir des fichiers `proto`.

---


### **Étapes pour Exécuter le Projet**

1. **Clonez le dépôt GitHub :**
   ```bash
   git clone https://github.com/L-Hadil/TP_GRPC.git
   cd TP_GRPC
   ```

2. **Build et Installation des Modules :**

    - **Common** :
      ```bash
      cd common
      mvn clean install
      ```

    - **HotelService** (Serveur) :
      ```bash
      cd ../hotel_service
      mvn clean install
      mvn spring-boot:run
      ```

    - **Agency** (Client) :
      ```bash
      cd ../agency
      mvn clean install
      java -jar target/agency-0.0.1-SNAPSHOT.jar
      ```

3. **Utilisation de l’Application** :
    - Lancer **Agency** pour utiliser l’interface CLI.
    - Suivre les instructions pour rechercher des hôtels ou effectuer une réservation.

---

### **Fonctionnalités**

1. **HotelService** :
    - Rechercher des offres disponibles en fonction de critères.
    - Enregistrer une réservation.

2. **Agency** :
    - Récupère les offres d’hôtels via `getAvailability`.
    - Applique des marges spécifiques pour proposer un prix personnalisé.
    - Réalise des réservations via `makeReservation`.

---

### **Structure des Modules**

#### **Common**
- **Fichier `hotel_service.proto`** :
  Définit les messages et services gRPC (`AvailabilityRequest`, `ReservationRequest`, etc.).

#### **HotelService**
- **Implémentation** :
    - `HotelServiceImpl` gère la recherche d’offres et les réservations.
    - Connecté à une base MySQL via JPA.

#### **Agency**
- **Interface CLI** :
    - Permet à l’utilisateur de chercher des offres et réserver un hôtel.
- **HotelServiceClient** :
    - Communique avec le serveur via gRPC.
- **GrpcConverter** :
    - Facilite la conversion des objets entre les formats locaux et gRPC.

