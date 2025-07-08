package com.hospital.repository;

import com.hospital.entity.LabOrder;
import com.hospital.entity.Patient;
import com.hospital.entity.Doctor;
import com.hospital.entity.LabTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LabOrderRepository extends JpaRepository<LabOrder, Long> {

    // Commandes par patient
    List<LabOrder> findByPatientOrderByOrderDateDesc(Patient patient);

    // Commandes par médecin prescripteur
    List<LabOrder> findByDoctorOrderByOrderDate(Doctor doctor);

    // Commandes par test de laboratoire
    List<LabOrder> findByLabTest(LabTest labTest);

    // Commandes par statut
    List<LabOrder> findByStatus(String status);

    // Commandes d'aujourd'hui
    @Query("SELECT lo FROM LabOrder lo WHERE DATE(lo.orderDate) = CURRENT_DATE")
    List<LabOrder> findTodayOrders();

    // Commandes par période
    @Query("SELECT lo FROM LabOrder lo WHERE lo.orderDate BETWEEN :startDate AND :endDate")
    List<LabOrder> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);

    // Commandes urgentes
    List<LabOrder> findByIsUrgent(Boolean isUrgent);

    // Commandes en attente
    @Query("SELECT lo FROM LabOrder lo WHERE lo.status IN ('PENDING', 'COLLECTED', 'PROCESSING')")
    List<LabOrder> findPendingOrders();

    // Commandes prêtes pour collecte
    @Query("SELECT lo FROM LabOrder lo WHERE lo.status = 'READY_FOR_COLLECTION'")
    List<LabOrder> findReadyForCollection();

    // Recherche dans les commandes
    @Query("SELECT lo FROM LabOrder lo WHERE " +
           "LOWER(lo.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(lo.sampleId) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<LabOrder> searchLabOrders(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Statistiques par statut
    @Query("SELECT lo.status, COUNT(lo) FROM LabOrder lo GROUP BY lo.status")
    List<Object[]> countOrdersByStatus();

    // Commandes par test et période
    @Query("SELECT lo FROM LabOrder lo WHERE lo.labTest = :labTest AND lo.orderDate BETWEEN :startDate AND :endDate")
    List<LabOrder> findOrdersByTestAndPeriod(@Param("labTest") LabTest labTest, 
                                           @Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);

    // Temps d'attente moyen
    @Query("SELECT AVG(DATEDIFF(HOUR, lo.orderDate, lo.collectionDate)) FROM LabOrder lo WHERE lo.collectionDate IS NOT NULL")
    Double getAverageWaitingTime();

    // Commandes retardées
    @Query("SELECT lo FROM LabOrder lo WHERE lo.status != 'COMPLETED' AND " +
           "DATEDIFF(HOUR, lo.orderDate, CURRENT_TIMESTAMP) > (lo.labTest.processingTimeHours + 24)")
    List<LabOrder> findDelayedOrders();
}