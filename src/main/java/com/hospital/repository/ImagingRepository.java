package com.hospital.repository;

import com.hospital.entity.ImagingResult;
import com.hospital.entity.Patient;
import com.hospital.entity.Doctor;
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
public interface ImagingRepository extends JpaRepository<ImagingResult, Long> {

    /**
     * Trouver par numéro d'ordre
     */
    Optional<ImagingResult> findByOrderNumber(String orderNumber);

    /**
     * Trouver par numéro de résultat
     */
    Optional<ImagingResult> findByResultNumber(String resultNumber);

    /**
     * Trouver par patient
     */
    List<ImagingResult> findByPatient(Patient patient);

    /**
     * Trouver par médecin prescripteur
     */
    List<ImagingResult> findByOrderingPhysician(Doctor doctor);

    /**
     * Trouver par médecin
     */
    List<ImagingResult> findByDoctor(Doctor doctor);

    /**
     * Trouver par type d'imagerie
     */
    List<ImagingResult> findByImagingType(ImagingResult.ImagingType type);

    /**
     * Trouver par partie du corps
     */
    List<ImagingResult> findByBodyPart(String bodyPart);

    /**
     * Trouver par statut
     */
    List<ImagingResult> findByResultStatus(ImagingResult.ResultStatus status);

    /**
     * Trouver les examens programmés pour une date
     */
    @Query("SELECT i FROM ImagingResult i WHERE DATE(i.studyDate) = :date AND i.resultStatus = 'PENDING'")
    List<ImagingResult> findScheduledForDate(@Param("date") LocalDate date);

    /**
     * Trouver les examens urgents en attente
     */
    @Query("SELECT i FROM ImagingResult i WHERE i.isUrgent = true AND i.resultStatus IN ('PENDING', 'IN_PROGRESS')")
    List<ImagingResult> findUrgentPendingExams();

    /**
     * Recherche générale
     */
    @Query("SELECT i FROM ImagingResult i WHERE " +
           "LOWER(i.resultNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.studyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.patient.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.patient.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.doctor.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.doctor.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.findings) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<ImagingResult> searchImagingResults(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Compter par statut
     */
    @Query("SELECT COUNT(i) FROM ImagingResult i WHERE i.resultStatus = :status")
    long countByStatus(@Param("status") ImagingResult.ResultStatus status);

    /**
     * Statistiques par type d'imagerie
     */
    @Query("SELECT i.imagingType, COUNT(i) FROM ImagingResult i GROUP BY i.imagingType")
    List<Object[]> getImagingCountByType();

    /**
     * Statistiques par statut
     */
    @Query("SELECT i.resultStatus, COUNT(i) FROM ImagingResult i GROUP BY i.resultStatus")
    List<Object[]> getImagingCountByStatus();

    /**
     * Trouver les résultats non vérifiés
     */
    @Query("SELECT i FROM ImagingResult i WHERE i.isVerified = false AND i.resultStatus = 'COMPLETED'")
    List<ImagingResult> findUnverifiedResults();

    /**
     * Trouver les résultats anormaux
     */
    @Query("SELECT i FROM ImagingResult i WHERE i.isAbnormal = true")
    List<ImagingResult> findAbnormalResults();

    /**
     * Trouver par radiologue
     */
    List<ImagingResult> findByRadiologistName(String radiologistName);

    /**
     * Trouver par technicien
     */
    List<ImagingResult> findByTechnicianName(String technicianName);

    /**
     * Trouver par centre d'imagerie
     */
    List<ImagingResult> findByImagingCenter(String imagingCenter);
}