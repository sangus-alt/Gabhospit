package com.hospital.repository;

import com.hospital.entity.Consultation;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    // Consultations par patient
    List<Consultation> findByPatientOrderByConsultationDateDesc(Patient patient);

    // Consultations par médecin
    List<Consultation> findByDoctorOrderByConsultationDateDesc(Doctor doctor);

    // Consultations d'aujourd'hui
    @Query("SELECT c FROM Consultation c WHERE DATE(c.consultationDate) = CURRENT_DATE")
    List<Consultation> findTodayConsultations();

    // Consultations par période
    @Query("SELECT c FROM Consultation c WHERE c.consultationDate BETWEEN :startDate AND :endDate")
    List<Consultation> findConsultationsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);

    // Consultations par médecin et date
    @Query("SELECT c FROM Consultation c WHERE c.doctor = :doctor AND DATE(c.consultationDate) = CURRENT_DATE")
    List<Consultation> findTodayConsultationsByDoctor(@Param("doctor") Doctor doctor);

    // Consultations par type
    List<Consultation> findByConsultationType(String consultationType);

    // Recherche dans les consultations
    @Query("SELECT c FROM Consultation c WHERE " +
           "LOWER(c.chiefComplaint) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.diagnosis) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.treatmentPlan) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Consultation> searchConsultations(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Consultations récentes
    @Query("SELECT c FROM Consultation c ORDER BY c.consultationDate DESC")
    List<Consultation> findRecentConsultations(Pageable pageable);

    // Compter consultations par médecin
    @Query("SELECT d.lastName, COUNT(c) FROM Consultation c JOIN c.doctor d GROUP BY d.lastName")
    List<Object[]> countConsultationsByDoctor();

    // Consultations par statut
    List<Consultation> findByStatus(String status);

    // Urgences
    List<Consultation> findByIsUrgent(Boolean isUrgent);

    // Prochaines consultations pour un médecin
    @Query("SELECT c FROM Consultation c WHERE c.doctor = :doctor AND c.consultationDate > :now ORDER BY c.consultationDate")
    List<Consultation> findUpcomingConsultationsByDoctor(@Param("doctor") Doctor doctor, @Param("now") LocalDateTime now);

    // Consultations terminées
    @Query("SELECT c FROM Consultation c WHERE c.status = 'COMPLETED'")
    List<Consultation> findCompletedConsultations();
}