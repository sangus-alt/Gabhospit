package com.hospital.service;

import com.hospital.entity.Bill;
import com.hospital.entity.BillItem;
import com.hospital.entity.Patient;
import com.hospital.entity.Payment;
import com.hospital.repository.BillRepository;
import com.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BillingService {

    private final BillRepository billRepository;
    private final PatientRepository patientRepository;

    public Bill createBill(Bill bill) {
        log.info("Création d'une nouvelle facture pour le patient ID: {}", bill.getPatient().getId());
        
        bill.setBillNumber(generateBillNumber());
        bill.setBillDate(LocalDateTime.now());
        bill.setStatus("PENDING");
        
        // Calculer le montant total
        calculateBillTotal(bill);
        
        // Date d'échéance par défaut (30 jours)
        if (bill.getDueDate() == null) {
            bill.setDueDate(LocalDate.now().plusDays(30));
        }
        
        Bill savedBill = billRepository.save(bill);
        log.info("Facture créée avec le numéro: {}", savedBill.getBillNumber());
        return savedBill;
    }

    @Transactional(readOnly = true)
    public Bill getBillById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée avec l'ID: " + id));
    }

    @Transactional(readOnly = true)
    public Bill getBillByNumber(String billNumber) {
        return billRepository.findByBillNumber(billNumber)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée avec le numéro: " + billNumber));
    }

    @Transactional(readOnly = true)
    public Page<Bill> getAllBills(Pageable pageable) {
        return billRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Bill> getBillsByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé avec l'ID: " + patientId));
        return billRepository.findByPatientOrderByBillDateDesc(patient);
    }

    @Transactional(readOnly = true)
    public List<Bill> getBillsByStatus(String status) {
        return billRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Bill> getTodayBills() {
        return billRepository.findTodayBills();
    }

    @Transactional(readOnly = true)
    public List<Bill> getBillsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return billRepository.findBillsBetweenDates(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Bill> getUnpaidBills() {
        return billRepository.findUnpaidBills();
    }

    @Transactional(readOnly = true)
    public List<Bill> getOverdueBills() {
        return billRepository.findOverdueBills();
    }

    @Transactional(readOnly = true)
    public List<Bill> getPartiallyPaidBills() {
        return billRepository.findPartiallyPaidBills();
    }

    @Transactional(readOnly = true)
    public List<Bill> getBillsByType(String billType) {
        return billRepository.findByBillType(billType);
    }

    @Transactional(readOnly = true)
    public Page<Bill> searchBills(String searchTerm, Pageable pageable) {
        return billRepository.searchBills(searchTerm, pageable);
    }

    @Transactional(readOnly = true)
    public Double getRevenueByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        Double revenue = billRepository.getRevenueByPeriod(startDate, endDate);
        return revenue != null ? revenue : 0.0;
    }

    @Transactional(readOnly = true)
    public Double getTodayRevenue() {
        Double revenue = billRepository.getTodayRevenue();
        return revenue != null ? revenue : 0.0;
    }

    @Transactional(readOnly = true)
    public Double getTotalOutstandingAmount() {
        Double outstanding = billRepository.getTotalOutstandingAmount();
        return outstanding != null ? outstanding : 0.0;
    }

    @Transactional(readOnly = true)
    public List<Object[]> getPaymentStatistics() {
        return billRepository.getPaymentStatistics();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getTopPatientsByBilling(Pageable pageable) {
        return billRepository.getTopPatientsByBilling(pageable);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getMonthlyRevenue(Pageable pageable) {
        return billRepository.getMonthlyRevenue(pageable);
    }

    public Bill updateBill(Long id, Bill billDetails) {
        Bill bill = getBillById(id);
        
        if ("PAID".equals(bill.getStatus())) {
            throw new RuntimeException("Impossible de modifier une facture payée");
        }
        
        bill.setDescription(billDetails.getDescription());
        bill.setBillType(billDetails.getBillType());
        bill.setItems(billDetails.getItems());
        bill.setDueDate(billDetails.getDueDate());
        bill.setNotes(billDetails.getNotes());
        
        // Recalculer le montant total
        calculateBillTotal(bill);
        
        return billRepository.save(bill);
    }

    public Bill addBillItem(Long billId, BillItem item) {
        Bill bill = getBillById(billId);
        
        if ("PAID".equals(bill.getStatus())) {
            throw new RuntimeException("Impossible d'ajouter des éléments à une facture payée");
        }
        
        bill.getItems().add(item);
        item.setBill(bill);
        
        // Recalculer le montant total
        calculateBillTotal(bill);
        
        return billRepository.save(bill);
    }

    public Bill applyDiscount(Long billId, Double discountAmount, String discountReason) {
        Bill bill = getBillById(billId);
        
        if ("PAID".equals(bill.getStatus())) {
            throw new RuntimeException("Impossible d'appliquer une remise à une facture payée");
        }
        
        bill.setDiscountAmount(discountAmount);
        bill.setDiscountReason(discountReason);
        
        // Recalculer le montant total avec la remise
        calculateBillTotal(bill);
        
        log.info("Remise de {} appliquée à la facture {}", discountAmount, bill.getBillNumber());
        return billRepository.save(bill);
    }

    public Bill processPayment(Long billId, Double paymentAmount, String paymentMethod, String paymentReference) {
        Bill bill = getBillById(billId);
        
        if ("PAID".equals(bill.getStatus())) {
            throw new RuntimeException("Cette facture est déjà payée");
        }
        
        Double currentPaidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : 0.0;
        Double newPaidAmount = currentPaidAmount + paymentAmount;
        
        if (newPaidAmount > bill.getTotalAmount()) {
            throw new RuntimeException("Le montant du paiement dépasse le solde de la facture");
        }
        
        bill.setPaidAmount(newPaidAmount);
        
        // Créer l'enregistrement de paiement
        Payment payment = new Payment();
        payment.setBill(bill);
        payment.setAmount(paymentAmount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentReference(paymentReference);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("COMPLETED");
        
        bill.getPayments().add(payment);
        
        // Mettre à jour le statut de la facture
        if (newPaidAmount.equals(bill.getTotalAmount())) {
            bill.setStatus("PAID");
            bill.setPaidDate(LocalDateTime.now());
        } else {
            bill.setStatus("PARTIALLY_PAID");
        }
        
        Bill savedBill = billRepository.save(bill);
        log.info("Paiement de {} traité pour la facture {}", paymentAmount, bill.getBillNumber());
        return savedBill;
    }

    public Bill markAsPaid(Long billId) {
        Bill bill = getBillById(billId);
        bill.setStatus("PAID");
        bill.setPaidAmount(bill.getTotalAmount());
        bill.setPaidDate(LocalDateTime.now());
        return billRepository.save(bill);
    }

    public Bill markAsOverdue(Long billId) {
        Bill bill = getBillById(billId);
        
        if (bill.getDueDate().isBefore(LocalDate.now()) && "PENDING".equals(bill.getStatus())) {
            bill.setStatus("OVERDUE");
            return billRepository.save(bill);
        }
        
        throw new RuntimeException("La facture ne peut pas être marquée comme en retard");
    }

    public Bill cancelBill(Long billId, String reason) {
        Bill bill = getBillById(billId);
        
        if ("PAID".equals(bill.getStatus())) {
            throw new RuntimeException("Impossible d'annuler une facture payée");
        }
        
        bill.setStatus("CANCELLED");
        bill.setCancellationReason(reason);
        bill.setCancelledDate(LocalDateTime.now());
        
        Bill savedBill = billRepository.save(bill);
        log.info("Facture annulée: {} - Raison: {}", bill.getBillNumber(), reason);
        return savedBill;
    }

    public void deleteBill(Long id) {
        Bill bill = getBillById(id);
        
        if ("PAID".equals(bill.getStatus())) {
            throw new RuntimeException("Impossible de supprimer une facture payée");
        }
        
        billRepository.delete(bill);
        log.info("Facture supprimée: {}", bill.getBillNumber());
    }

    private void calculateBillTotal(Bill bill) {
        Double subtotal = bill.getItems().stream()
                .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                .sum();
        
        bill.setSubtotalAmount(subtotal);
        
        // Appliquer la TVA si définie
        Double taxAmount = 0.0;
        if (bill.getTaxRate() != null && bill.getTaxRate() > 0) {
            taxAmount = subtotal * bill.getTaxRate() / 100;
        }
        bill.setTaxAmount(taxAmount);
        
        // Appliquer la remise si définie
        Double discountAmount = bill.getDiscountAmount() != null ? bill.getDiscountAmount() : 0.0;
        
        // Calculer le montant total
        Double totalAmount = subtotal + taxAmount - discountAmount;
        bill.setTotalAmount(Math.max(0, totalAmount)); // S'assurer que le total n'est pas négatif
    }

    private String generateBillNumber() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "BILL" + timestamp.substring(timestamp.length() - 8) + uuid;
    }

    @Transactional(readOnly = true)
    public Long countTodayBills() {
        return (long) billRepository.findTodayBills().size();
    }

    @Transactional(readOnly = true)
    public Long countUnpaidBills() {
        return (long) billRepository.findUnpaidBills().size();
    }

    @Transactional(readOnly = true)
    public Long countOverdueBills() {
        return (long) billRepository.findOverdueBills().size();
    }
}