package com.hospital.repository;

import com.hospital.entity.Insurance;
import com.hospital.entity.PatientInsurance;
import com.hospital.entity.InsuranceClaim;
import com.hospital.entity.Patient;
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
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {

    // Recherche par nom de compagnie
    Optional<Insurance> findByCompanyName(String companyName);

    // Recherche par code d'assurance
    Optional<Insurance> findByInsuranceCode(String insuranceCode);

    // Assurances actives
    List<Insurance> findByIsActive(Boolean isActive);

    // Recherche dans les assurances
    @Query("SELECT i FROM Insurance i WHERE " +
           "LOWER(i.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.insuranceCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.contactPerson) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Insurance> searchInsurances(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Assurances par type
    List<Insurance> findByInsuranceType(String insuranceType);

    // Assurances avec tiers payant
    @Query("SELECT i FROM Insurance i WHERE i.supportsThirdPartyPayment = true")
    List<Insurance> findInsurancesWithThirdPartyPayment();

    // Statistiques par assureur
    @Query("SELECT i.companyName, COUNT(pi) FROM Insurance i " +
           "LEFT JOIN PatientInsurance pi ON pi.insurance = i " +
           "GROUP BY i.companyName")
    List<Object[]> getInsuranceStatistics();
}

@Repository
interface PatientInsuranceRepository extends JpaRepository<PatientInsurance, Long> {

    // Assurances par patient
    List<PatientInsurance> findByPatient(Patient patient);

    // Assurances actives par patient
    @Query("SELECT pi FROM PatientInsurance pi WHERE pi.patient = :patient AND pi.isActive = true")
    List<PatientInsurance> findActiveInsurancesByPatient(@Param("patient") Patient patient);

    // Assurances par numéro de police
    Optional<PatientInsurance> findByPolicyNumber(String policyNumber);

    // Assurances expirées
    @Query("SELECT pi FROM PatientInsurance pi WHERE pi.expiryDate < :date")
    List<PatientInsurance> findExpiredInsurances(@Param("date") LocalDate date);

    // Assurances à expirer bientôt
    @Query("SELECT pi FROM PatientInsurance pi WHERE pi.expiryDate BETWEEN :today AND :futureDate")
    List<PatientInsurance> findInsurancesExpiringBetween(@Param("today") LocalDate today, @Param("futureDate") LocalDate futureDate);
}

@Repository
interface InsuranceClaimRepository extends JpaRepository<InsuranceClaim, Long> {

    // Réclamations par patient
    List<InsuranceClaim> findByPatientOrderByClaimDateDesc(Patient patient);

    // Réclamations par statut
    List<InsuranceClaim> findByStatus(String status);

    // Réclamations en attente
    @Query("SELECT ic FROM InsuranceClaim ic WHERE ic.status = 'PENDING'")
    List<InsuranceClaim> findPendingClaims();

    // Réclamations approuvées
    @Query("SELECT ic FROM InsuranceClaim ic WHERE ic.status = 'APPROVED'")
    List<InsuranceClaim> findApprovedClaims();

    // Réclamations rejetées
    @Query("SELECT ic FROM InsuranceClaim ic WHERE ic.status = 'REJECTED'")
    List<InsuranceClaim> findRejectedClaims();

    // Montant total réclamé
    @Query("SELECT SUM(ic.claimedAmount) FROM InsuranceClaim ic WHERE ic.status = 'APPROVED'")
    Double getTotalApprovedClaimAmount();
}