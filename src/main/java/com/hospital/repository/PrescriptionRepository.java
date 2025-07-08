package com.hospital.repository;

import com.hospital.entity.Prescription;
import com.hospital.entity.Patient;
import com.hospital.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // Prescriptions par patient
    List<Prescription> findByPatientOrderByPrescriptionDateDesc(Patient patient);

    // Prescriptions par médecin
    List<Prescription> findByDoctorOrderByPrescriptionDate(Doctor doctor);

    // Prescriptions par statut
    List<Prescription> findByStatus(String status);

    // Prescriptions d'aujourd'hui
    @Query("SELECT p FROM Prescription p WHERE DATE(p.prescriptionDate) = CURRENT_DATE")
    List<Prescription> findTodayPrescriptions();

    // Prescriptions par période
    @Query("SELECT p FROM Prescription p WHERE p.prescriptionDate BETWEEN :startDate AND :endDate")
    List<Prescription> findPrescriptionsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);

    // Prescriptions actives
    @Query("SELECT p FROM Prescription p WHERE p.status = 'ACTIVE' AND p.validUntil >= CURRENT_DATE")
    List<Prescription> findActivePrescriptions();

    // Prescriptions expirées
    @Query("SELECT p FROM Prescription p WHERE p.validUntil < CURRENT_DATE")
    List<Prescription> findExpiredPrescriptions();

    // Recherche dans les prescriptions
    @Query("SELECT p FROM Prescription p WHERE " +
           "LOWER(p.instructions) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Prescription> searchPrescriptions(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Prescriptions en attente de dispensation
    @Query("SELECT p FROM Prescription p WHERE p.status = 'PENDING'")
    List<Prescription> findPendingPrescriptions();

    // Prescriptions dispensées aujourd'hui
    @Query("SELECT p FROM Prescription p WHERE p.status = 'DISPENSED' AND DATE(p.dispensedDate) = CURRENT_DATE")
    List<Prescription> findTodayDispensedPrescriptions();

    // Statistiques par médecin
    @Query("SELECT d.lastName, COUNT(p) FROM Prescription p JOIN p.doctor d GROUP BY d.lastName")
    List<Object[]> countPrescriptionsByDoctor();

    // Prescriptions par patient et statut
    List<Prescription> findByPatientAndStatus(Patient patient, String status);

    // Prescriptions nécessitant suivi
    @Query("SELECT p FROM Prescription p WHERE p.requiresFollowUp = true AND p.status = 'DISPENSED'")
    List<Prescription> findPrescriptionsRequiringFollowUp();

    // Prescriptions récurrentes
    @Query("SELECT p FROM Prescription p WHERE p.isRecurrent = true AND p.status = 'ACTIVE'")
    List<Prescription> findRecurrentPrescriptions();

    // Compter prescriptions par statut
    @Query("SELECT p.status, COUNT(p) FROM Prescription p GROUP BY p.status")
    List<Object[]> countPrescriptionsByStatus();
}