package com.hospital.repository;

import com.hospital.entity.QueueManagement;
import com.hospital.entity.Patient;
import com.hospital.entity.Department;
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
public interface QueueRepository extends JpaRepository<QueueManagement, Long> {

    // File d'attente active
    @Query("SELECT q FROM QueueManagement q WHERE q.status = 'WAITING' ORDER BY q.priority DESC, q.arrivalTime ASC")
    List<QueueManagement> findActiveQueue();

    // File d'attente par département
    @Query("SELECT q FROM QueueManagement q WHERE q.department = :department AND q.status = 'WAITING' ORDER BY q.priority DESC, q.arrivalTime ASC")
    List<QueueManagement> findQueueByDepartment(@Param("department") Department department);

    // Position dans la file d'attente
    @Query("SELECT COUNT(q) + 1 FROM QueueManagement q WHERE q.department = :department AND q.status = 'WAITING' AND " +
           "(q.priority > :priority OR (q.priority = :priority AND q.arrivalTime < :arrivalTime))")
    Integer getQueuePosition(@Param("department") Department department, 
                           @Param("priority") Integer priority, 
                           @Param("arrivalTime") LocalDateTime arrivalTime);

    // Numéro de ticket par patient
    Optional<QueueManagement> findByPatientAndStatus(Patient patient, String status);

    // File d'attente aujourd'hui
    @Query("SELECT q FROM QueueManagement q WHERE DATE(q.arrivalTime) = CURRENT_DATE")
    List<QueueManagement> findTodayQueue();

    // File d'attente par statut
    List<QueueManagement> findByStatus(String status);

    // Temps d'attente moyen
    @Query("SELECT AVG(DATEDIFF(MINUTE, q.arrivalTime, q.calledTime)) FROM QueueManagement q WHERE q.calledTime IS NOT NULL")
    Double getAverageWaitingTime();

    // Temps d'attente par département
    @Query("SELECT AVG(DATEDIFF(MINUTE, q.arrivalTime, q.calledTime)) FROM QueueManagement q " +
           "WHERE q.department = :department AND q.calledTime IS NOT NULL")
    Double getAverageWaitingTimeByDepartment(@Param("department") Department department);

    // Prochains patients en attente
    @Query("SELECT q FROM QueueManagement q WHERE q.status = 'WAITING' " +
           "ORDER BY q.priority DESC, q.arrivalTime ASC")
    List<QueueManagement> findNextPatients(Pageable pageable);

    // Patients en cours de traitement
    @Query("SELECT q FROM QueueManagement q WHERE q.status = 'IN_PROGRESS'")
    List<QueueManagement> findPatientsInProgress();

    // Recherche dans la file d'attente
    @Query("SELECT q FROM QueueManagement q WHERE " +
           "LOWER(q.queueNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(q.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<QueueManagement> searchQueue(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Statistiques par département
    @Query("SELECT d.name, COUNT(q) FROM QueueManagement q JOIN q.department d " +
           "WHERE DATE(q.arrivalTime) = CURRENT_DATE GROUP BY d.name")
    List<Object[]> getTodayQueueStatsByDepartment();

    // Patients avec priorité élevée
    @Query("SELECT q FROM QueueManagement q WHERE q.priority >= :minPriority AND q.status = 'WAITING' " +
           "ORDER BY q.priority DESC, q.arrivalTime ASC")
    List<QueueManagement> findHighPriorityPatients(@Param("minPriority") Integer minPriority);

    // Patients en attente depuis longtemps
    @Query("SELECT q FROM QueueManagement q WHERE q.status = 'WAITING' AND " +
           "DATEDIFF(MINUTE, q.arrivalTime, CURRENT_TIMESTAMP) > :maxWaitMinutes")
    List<QueueManagement> findLongWaitingPatients(@Param("maxWaitMinutes") Integer maxWaitMinutes);

    // Compter patients par statut aujourd'hui
    @Query("SELECT q.status, COUNT(q) FROM QueueManagement q " +
           "WHERE DATE(q.arrivalTime) = CURRENT_DATE GROUP BY q.status")
    List<Object[]> countTodayQueueByStatus();
}