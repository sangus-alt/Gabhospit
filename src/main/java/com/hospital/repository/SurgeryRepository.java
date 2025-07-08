package com.hospital.repository;

import com.hospital.entity.Surgery;
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
public interface SurgeryRepository extends JpaRepository<Surgery, Long> {

    // Chirurgies par patient
    List<Surgery> findByPatientOrderByScheduledDateDesc(Patient patient);

    // Chirurgies par chirurgien principal
    List<Surgery> findByPrimarySurgeonOrderByScheduledDate(Doctor primarySurgeon);

    // Chirurgies par type
    List<Surgery> findBySurgeryType(String surgeryType);

    // Chirurgies par statut
    List<Surgery> findByStatus(String status);

    // Chirurgies d'aujourd'hui
    @Query("SELECT s FROM Surgery s WHERE DATE(s.scheduledDate) = CURRENT_DATE")
    List<Surgery> findTodaySurgeries();

    // Chirurgies par période
    @Query("SELECT s FROM Surgery s WHERE s.scheduledDate BETWEEN :startDate AND :endDate")
    List<Surgery> findSurgeriesBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);

    // Chirurgies programmées
    @Query("SELECT s FROM Surgery s WHERE s.status = 'SCHEDULED' AND s.scheduledDate > CURRENT_TIMESTAMP")
    List<Surgery> findScheduledSurgeries();

    // Chirurgies en cours
    @Query("SELECT s FROM Surgery s WHERE s.status = 'IN_PROGRESS'")
    List<Surgery> findOngoingSurgeries();

    // Chirurgies terminées
    @Query("SELECT s FROM Surgery s WHERE s.status = 'COMPLETED'")
    List<Surgery> findCompletedSurgeries();

    // Recherche dans les chirurgies
    @Query("SELECT s FROM Surgery s WHERE " +
           "LOWER(s.surgeryType) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Surgery> searchSurgeries(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Chirurgies d'urgence
    List<Surgery> findByIsEmergency(Boolean isEmergency);

    // Statistiques par type de chirurgie
    @Query("SELECT s.surgeryType, COUNT(s) FROM Surgery s GROUP BY s.surgeryType")
    List<Object[]> countSurgeriesByType();

    // Chirurgies par chirurgien et période
    @Query("SELECT s FROM Surgery s WHERE s.primarySurgeon = :surgeon AND s.scheduledDate BETWEEN :startDate AND :endDate")
    List<Surgery> findSurgeriesBySurgeonAndPeriod(@Param("surgeon") Doctor surgeon, 
                                                @Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);

    // Durée moyenne des chirurgies
    @Query("SELECT AVG(s.actualDurationMinutes) FROM Surgery s WHERE s.status = 'COMPLETED' AND s.actualDurationMinutes IS NOT NULL")
    Double getAverageSurgeryDuration();

    // Chirurgies retardées
    @Query("SELECT s FROM Surgery s WHERE s.status = 'SCHEDULED' AND s.scheduledDate < CURRENT_TIMESTAMP")
    List<Surgery> findDelayedSurgeries();

    // Compter chirurgies par chirurgien
    @Query("SELECT d.lastName, COUNT(s) FROM Surgery s JOIN s.primarySurgeon d GROUP BY d.lastName")
    List<Object[]> countSurgeriesByDoctor();

    // Prochaines chirurgies
    @Query("SELECT s FROM Surgery s WHERE s.status = 'SCHEDULED' AND s.scheduledDate > CURRENT_TIMESTAMP ORDER BY s.scheduledDate")
    List<Surgery> findUpcomingSurgeries(Pageable pageable);

    // Taux de réussite par chirurgien
    @Query("SELECT d.lastName, " +
           "COUNT(s) as totalSurgeries, " +
           "SUM(CASE WHEN s.outcome = 'SUCCESS' THEN 1 ELSE 0 END) as successfulSurgeries " +
           "FROM Surgery s JOIN s.primarySurgeon d " +
           "WHERE s.status = 'COMPLETED' " +
           "GROUP BY d.lastName")
    List<Object[]> getSurgerySuccessRateByDoctor();
}