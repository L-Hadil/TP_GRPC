package com.example.agency.Repositories;

import com.example.agency.models.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {

    @Modifying
    @Query("UPDATE Offre o SET o.prixAgence = :prixAgence WHERE o.id = :offreId")
    void updatePrixAgence(@Param("offreId") Long offreId, @Param("prixAgence") Double prixAgence);

    @Query("SELECT o.prixAgence FROM Offre o WHERE o.id = :offreId")
    Double findPrixAgenceById(@Param("offreId") Long offreId);
}
