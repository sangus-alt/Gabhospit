package com.hospital.repository;

import com.hospital.entity.Medication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<Medication, Long> {

    // Recherche par nom commercial
    Optional<Medication> findByBrandName(String brandName);

    // Recherche par nom générique
    List<Medication> findByGenericNameContainingIgnoreCase(String genericName);

    // Médicaments par catégorie
    List<Medication> findByCategory(String category);

    // Médicaments actifs
    List<Medication> findByIsActive(Boolean isActive);

    // Médicaments avec prescription obligatoire
    List<Medication> findByRequiresPrescription(Boolean requiresPrescription);

    // Recherche dans les médicaments
    @Query("SELECT m FROM Medication m WHERE " +
           "LOWER(m.brandName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.genericName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.manufacturer) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.category) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Medication> searchMedications(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Médicaments par prix
    @Query("SELECT m FROM Medication m WHERE m.price BETWEEN :minPrice AND :maxPrice")
    List<Medication> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    // Stock faible
    @Query("SELECT m FROM Medication m WHERE m.currentStock <= m.minimumStock")
    List<Medication> findLowStockMedications();

    // Médicaments périmés
    @Query("SELECT m FROM Medication m WHERE m.expiryDate <= :date")
    List<Medication> findExpiredMedications(@Param("date") LocalDate date);

    // Médicaments à expirer bientôt
    @Query("SELECT m FROM Medication m WHERE m.expiryDate BETWEEN :today AND :futureDate")
    List<Medication> findMedicationsExpiringBetween(@Param("today") LocalDate today, @Param("futureDate") LocalDate futureDate);

    // Compter par catégorie
    @Query("SELECT m.category, COUNT(m) FROM Medication m GROUP BY m.category")
    List<Object[]> countMedicationsByCategory();

    // Médicaments les plus prescrits
    @Query("SELECT m, COUNT(pi) as prescriptionCount FROM Medication m " +
           "LEFT JOIN PrescriptionItem pi ON pi.medication = m " +
           "GROUP BY m ORDER BY prescriptionCount DESC")
    List<Object[]> findMostPrescribedMedications(Pageable pageable);

    // Médicaments par fabricant
    List<Medication> findByManufacturer(String manufacturer);

    // Alertes stock
    @Query("SELECT m FROM Medication m WHERE " +
           "m.currentStock <= m.minimumStock OR " +
           "m.expiryDate <= :alertDate")
    List<Medication> findMedicationsWithStockAlerts(@Param("alertDate") LocalDate alertDate);

    // Valeur totale du stock
    @Query("SELECT SUM(m.price * m.currentStock) FROM Medication m WHERE m.isActive = true")
    Double getTotalStockValue();
}