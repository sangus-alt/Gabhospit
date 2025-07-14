package com.hospital.controller;

import com.hospital.entity.Bill;
import com.hospital.entity.BillItem;
import com.hospital.entity.Payment;
import com.hospital.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Facturation", description = "API de gestion de la facturation et des paiements")
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/bills")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Lister toutes les factures")
    public ResponseEntity<Page<Bill>> getAllBills(
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Pageable pageable) {
        
        log.info("Récupération des factures avec filtres: patient={}, status={}, fromDate={}, toDate={}", 
                patientName, status, fromDate, toDate);
        
        Page<Bill> bills = billingService.findAllBills(patientName, status, fromDate, toDate, pageable);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/bills/{id}")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer une facture par ID")
    public ResponseEntity<Bill> getBillById(@PathVariable Long id) {
        log.info("Récupération de la facture avec ID: {}", id);
        Bill bill = billingService.findBillById(id);
        return ResponseEntity.ok(bill);
    }

    @PostMapping("/bills")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Créer une nouvelle facture")
    public ResponseEntity<Bill> createBill(@Valid @RequestBody Bill bill) {
        log.info("Création d'une nouvelle facture pour le patient ID: {}", bill.getPatient().getId());
        Bill createdBill = billingService.createBill(bill);
        return ResponseEntity.ok(createdBill);
    }

    @PutMapping("/bills/{id}")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Modifier une facture")
    public ResponseEntity<Bill> updateBill(@PathVariable Long id, @Valid @RequestBody Bill bill) {
        log.info("Modification de la facture ID: {}", id);
        Bill updatedBill = billingService.updateBill(id, bill);
        return ResponseEntity.ok(updatedBill);
    }

    @PostMapping("/bills/{billId}/items")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Ajouter un élément à une facture")
    public ResponseEntity<BillItem> addBillItem(@PathVariable Long billId, @Valid @RequestBody BillItem billItem) {
        log.info("Ajout d'un élément à la facture ID: {}", billId);
        BillItem addedItem = billingService.addBillItem(billId, billItem);
        return ResponseEntity.ok(addedItem);
    }

    @DeleteMapping("/bills/{billId}/items/{itemId}")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Supprimer un élément d'une facture")
    public ResponseEntity<Void> removeBillItem(@PathVariable Long billId, @PathVariable Long itemId) {
        log.info("Suppression de l'élément ID: {} de la facture ID: {}", itemId, billId);
        billingService.removeBillItem(billId, itemId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bills/{id}/finalize")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Finaliser une facture")
    public ResponseEntity<Bill> finalizeBill(@PathVariable Long id) {
        log.info("Finalisation de la facture ID: {}", id);
        Bill finalizedBill = billingService.finalizeBill(id);
        return ResponseEntity.ok(finalizedBill);
    }

    @PostMapping("/bills/{id}/cancel")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Annuler une facture")
    public ResponseEntity<Bill> cancelBill(@PathVariable Long id, @RequestParam String reason) {
        log.info("Annulation de la facture ID: {} pour raison: {}", id, reason);
        Bill cancelledBill = billingService.cancelBill(id, reason);
        return ResponseEntity.ok(cancelledBill);
    }

    @GetMapping("/bills/{id}/pdf")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Générer le PDF d'une facture")
    public ResponseEntity<byte[]> generateBillPdf(@PathVariable Long id) {
        log.info("Génération du PDF pour la facture ID: {}", id);
        byte[] pdfContent = billingService.generateBillPdf(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "facture_" + id + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    // Gestion des paiements
    @GetMapping("/payments")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Lister tous les paiements")
    public ResponseEntity<Page<Payment>> getAllPayments(
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Pageable pageable) {
        
        log.info("Récupération des paiements avec filtres: patient={}, method={}, fromDate={}, toDate={}", 
                patientName, paymentMethod, fromDate, toDate);
        
        Page<Payment> payments = billingService.findAllPayments(patientName, paymentMethod, fromDate, toDate, pageable);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/payments")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Enregistrer un paiement")
    public ResponseEntity<Payment> recordPayment(@Valid @RequestBody Payment payment) {
        log.info("Enregistrement d'un paiement de {} pour la facture ID: {}", 
                payment.getAmount(), payment.getBill().getId());
        Payment recordedPayment = billingService.recordPayment(payment);
        return ResponseEntity.ok(recordedPayment);
    }

    @GetMapping("/payments/{id}")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer un paiement par ID")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        log.info("Récupération du paiement avec ID: {}", id);
        Payment payment = billingService.findPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @DeleteMapping("/payments/{id}")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Annuler un paiement")
    public ResponseEntity<Void> cancelPayment(@PathVariable Long id, @RequestParam String reason) {
        log.info("Annulation du paiement ID: {} pour raison: {}", id, reason);
        billingService.cancelPayment(id, reason);
        return ResponseEntity.ok().build();
    }

    // Statistiques et rapports
    @GetMapping("/statistics/revenue")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir les statistiques de revenus")
    public ResponseEntity<BigDecimal> getRevenueStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        log.info("Calcul des revenus entre {} et {}", fromDate, toDate);
        BigDecimal revenue = billingService.calculateRevenue(fromDate, toDate);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/statistics/outstanding")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir le montant des factures impayées")
    public ResponseEntity<BigDecimal> getOutstandingAmount() {
        log.info("Calcul du montant des factures impayées");
        BigDecimal outstanding = billingService.calculateOutstandingAmount();
        return ResponseEntity.ok(outstanding);
    }

    @GetMapping("/reports/daily")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Rapport quotidien de facturation")
    public ResponseEntity<byte[]> generateDailyReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Génération du rapport quotidien pour: {}", date);
        byte[] report = billingService.generateDailyBillingReport(date);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "rapport_facturation_" + date + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(report);
    }

    @GetMapping("/patient/{patientId}/bills")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les factures d'un patient")
    public ResponseEntity<List<Bill>> getPatientBills(@PathVariable Long patientId) {
        log.info("Récupération des factures du patient ID: {}", patientId);
        List<Bill> bills = billingService.findBillsByPatientId(patientId);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/patient/{patientId}/balance")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir le solde d'un patient")
    public ResponseEntity<BigDecimal> getPatientBalance(@PathVariable Long patientId) {
        log.info("Calcul du solde pour le patient ID: {}", patientId);
        BigDecimal balance = billingService.calculatePatientBalance(patientId);
        return ResponseEntity.ok(balance);
    }
}