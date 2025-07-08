package com.hospital.repository;

import com.hospital.entity.MedicalCertificate;
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
import java.util.Optional;

@Repository
public interface MedicalCertificateRepository extends JpaRepository<MedicalCertificate, Long> {

    // Certificats par patient
    List<MedicalCertificate> findByPatientOrderByIssueDateDesc(Patient patient);

    // Certificats par médecin
    List<MedicalCertificate> findByDoctorOrderByIssueDate(Doctor doctor);

    // Certificats par numéro
    Optional<MedicalCertificate> findByCertificateNumber(String certificateNumber);

    // Certificats par type
    List<MedicalCertificate> findByCertificateType(String certificateType);

    // Certificats d'aujourd'hui
    @Query("SELECT mc FROM MedicalCertificate mc WHERE DATE(mc.issueDate) = CURRENT_DATE")
    List<MedicalCertificate> findTodayCertificates();

    // Certificats par période
    @Query("SELECT mc FROM MedicalCertificate mc WHERE mc.issueDate BETWEEN :startDate AND :endDate")
    List<MedicalCertificate> findCertificatesBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                                        @Param("endDate") LocalDateTime endDate);

    // Recherche dans les certificats
    @Query("SELECT mc FROM MedicalCertificate mc WHERE " +
           "LOWER(mc.certificateNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(mc.purpose) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(mc.diagnosis) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<MedicalCertificate> searchCertificates(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Certificats valides
    @Query("SELECT mc FROM MedicalCertificate mc WHERE mc.validUntil >= CURRENT_DATE")
    List<MedicalCertificate> findValidCertificates();

    // Certificats expirés
    @Query("SELECT mc FROM MedicalCertificate mc WHERE mc.validUntil < CURRENT_DATE")
    List<MedicalCertificate> findExpiredCertificates();

    // Certificats par durée
    @Query("SELECT mc FROM MedicalCertificate mc WHERE mc.durationDays = :days")
    List<MedicalCertificate> findCertificatesByDuration(@Param("days") Integer days);

    // Statistiques par type
    @Query("SELECT mc.certificateType, COUNT(mc) FROM MedicalCertificate mc GROUP BY mc.certificateType")
    List<Object[]> countCertificatesByType();

    // Certificats récents
    @Query("SELECT mc FROM MedicalCertificate mc ORDER BY mc.issueDate DESC")
    List<MedicalCertificate> findRecentCertificates(Pageable pageable);

    // Certificats par médecin et période
    @Query("SELECT mc FROM MedicalCertificate mc WHERE mc.doctor = :doctor AND mc.issueDate BETWEEN :startDate AND :endDate")
    List<MedicalCertificate> findCertificatesByDoctorAndPeriod(@Param("doctor") Doctor doctor, 
                                                             @Param("startDate") LocalDateTime startDate, 
                                                             @Param("endDate") LocalDateTime endDate);

    // Certificats d'arrêt de travail
    @Query("SELECT mc FROM MedicalCertificate mc WHERE mc.certificateType = 'SICK_LEAVE'")
    List<MedicalCertificate> findSickLeaveCertificates();

    // Certificats d'aptitude
    @Query("SELECT mc FROM MedicalCertificate mc WHERE mc.certificateType = 'FITNESS'")
    List<MedicalCertificate> findFitnessCertificates();

    // Compter certificats par médecin
    @Query("SELECT d.lastName, COUNT(mc) FROM MedicalCertificate mc JOIN mc.doctor d GROUP BY d.lastName")
    List<Object[]> countCertificatesByDoctor();
}