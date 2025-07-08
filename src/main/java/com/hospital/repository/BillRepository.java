package com.hospital.repository;

import com.hospital.entity.Bill;
import com.hospital.entity.Patient;
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
public interface BillRepository extends JpaRepository<Bill, Long> {

    // Factures par patient
    List<Bill> findByPatientOrderByBillDateDesc(Patient patient);

    // Factures par numéro
    Optional<Bill> findByBillNumber(String billNumber);

    // Factures par statut
    List<Bill> findByStatus(String status);

    // Factures d'aujourd'hui
    @Query("SELECT b FROM Bill b WHERE DATE(b.billDate) = CURRENT_DATE")
    List<Bill> findTodayBills();

    // Factures par période
    @Query("SELECT b FROM Bill b WHERE b.billDate BETWEEN :startDate AND :endDate")
    List<Bill> findBillsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);

    // Factures impayées
    @Query("SELECT b FROM Bill b WHERE b.status IN ('PENDING', 'OVERDUE')")
    List<Bill> findUnpaidBills();

    // Factures en retard
    @Query("SELECT b FROM Bill b WHERE b.status = 'PENDING' AND b.dueDate < CURRENT_DATE")
    List<Bill> findOverdueBills();

    // Recherche dans les factures
    @Query("SELECT b FROM Bill b WHERE " +
           "LOWER(b.billNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Bill> searchBills(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Revenus par période
    @Query("SELECT SUM(b.totalAmount) FROM Bill b WHERE b.status = 'PAID' AND b.billDate BETWEEN :startDate AND :endDate")
    Double getRevenueByPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Revenus d'aujourd'hui
    @Query("SELECT SUM(b.totalAmount) FROM Bill b WHERE b.status = 'PAID' AND DATE(b.billDate) = CURRENT_DATE")
    Double getTodayRevenue();

    // Statistiques de paiement
    @Query("SELECT b.status, COUNT(b), SUM(b.totalAmount) FROM Bill b GROUP BY b.status")
    List<Object[]> getPaymentStatistics();

    // Factures par montant
    @Query("SELECT b FROM Bill b WHERE b.totalAmount BETWEEN :minAmount AND :maxAmount")
    List<Bill> findBillsByAmountRange(@Param("minAmount") Double minAmount, @Param("maxAmount") Double maxAmount);

    // Top patients par factures
    @Query("SELECT p.firstName, p.lastName, COUNT(b), SUM(b.totalAmount) " +
           "FROM Bill b JOIN b.patient p GROUP BY p.id ORDER BY SUM(b.totalAmount) DESC")
    List<Object[]> getTopPatientsByBilling(Pageable pageable);

    // Factures partiellement payées
    @Query("SELECT b FROM Bill b WHERE b.paidAmount > 0 AND b.paidAmount < b.totalAmount")
    List<Bill> findPartiallyPaidBills();

    // Montant total impayé
    @Query("SELECT SUM(b.totalAmount - COALESCE(b.paidAmount, 0)) FROM Bill b WHERE b.status IN ('PENDING', 'OVERDUE')")
    Double getTotalOutstandingAmount();

    // Factures par type
    List<Bill> findByBillType(String billType);

    // Revenus mensuels
    @Query("SELECT YEAR(b.billDate), MONTH(b.billDate), SUM(b.totalAmount) " +
           "FROM Bill b WHERE b.status = 'PAID' " +
           "GROUP BY YEAR(b.billDate), MONTH(b.billDate) " +
           "ORDER BY YEAR(b.billDate) DESC, MONTH(b.billDate) DESC")
    List<Object[]> getMonthlyRevenue(Pageable pageable);

    // Factures avec remise
    @Query("SELECT b FROM Bill b WHERE b.discountAmount > 0")
    List<Bill> findBillsWithDiscount();
}