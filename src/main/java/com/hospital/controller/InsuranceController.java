package com.hospital.controller;

import com.hospital.entity.Insurance;
import com.hospital.entity.InsuranceClaim;
import com.hospital.entity.PatientInsurance;
import com.hospital.service.InsuranceService;
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
@RequestMapping("/api/insurance")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Assurances", description = "API de gestion des assurances et réclamations")
public class InsuranceController {

    private final InsuranceService insuranceService;

    // Gestion des compagnies d'assurance
    @GetMapping("/companies")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Lister toutes les compagnies d'assurance")
    public ResponseEntity<Page<Insurance>> getAllInsuranceCompanies(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        
        log.info("Récupération des compagnies d'assurance avec filtres: name={}, type={}, active={}", 
                name, type, active);
        
        Page<Insurance> companies = insuranceService.findAllInsuranceCompanies(name, type, active, pageable);
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/companies/{id}")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer une compagnie d'assurance par ID")
    public ResponseEntity<Insurance> getInsuranceCompanyById(@PathVariable Long id) {
        log.info("Récupération de la compagnie d'assurance avec ID: {}", id);
        Insurance company = insuranceService.findInsuranceCompanyById(id);
        return ResponseEntity.ok(company);
    }

    @PostMapping("/companies")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Créer une nouvelle compagnie d'assurance")
    public ResponseEntity<Insurance> createInsuranceCompany(@Valid @RequestBody Insurance insurance) {
        log.info("Création d'une nouvelle compagnie d'assurance: {}", insurance.getName());
        Insurance createdCompany = insuranceService.createInsuranceCompany(insurance);
        return ResponseEntity.ok(createdCompany);
    }

    @PutMapping("/companies/{id}")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Modifier une compagnie d'assurance")
    public ResponseEntity<Insurance> updateInsuranceCompany(@PathVariable Long id, @Valid @RequestBody Insurance insurance) {
        log.info("Modification de la compagnie d'assurance ID: {}", id);
        Insurance updatedCompany = insuranceService.updateInsuranceCompany(id, insurance);
        return ResponseEntity.ok(updatedCompany);
    }

    @DeleteMapping("/companies/{id}")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Supprimer une compagnie d'assurance")
    public ResponseEntity<Void> deleteInsuranceCompany(@PathVariable Long id) {
        log.info("Suppression de la compagnie d'assurance ID: {}", id);
        insuranceService.deleteInsuranceCompany(id);
        return ResponseEntity.ok().build();
    }

    // Gestion des assurances patients
    @GetMapping("/patient-insurance")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Lister toutes les assurances patients")
    public ResponseEntity<Page<PatientInsurance>> getAllPatientInsurance(
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) String insuranceName,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Boolean expired,
            Pageable pageable) {
        
        log.info("Récupération des assurances patients avec filtres: patient={}, insurance={}, active={}, expired={}", 
                patientName, insuranceName, active, expired);
        
        Page<PatientInsurance> patientInsurances = insuranceService.findAllPatientInsurance(
                patientName, insuranceName, active, expired, pageable);
        return ResponseEntity.ok(patientInsurances);
    }

    @GetMapping("/patient-insurance/{id}")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer une assurance patient par ID")
    public ResponseEntity<PatientInsurance> getPatientInsuranceById(@PathVariable Long id) {
        log.info("Récupération de l'assurance patient avec ID: {}", id);
        PatientInsurance patientInsurance = insuranceService.findPatientInsuranceById(id);
        return ResponseEntity.ok(patientInsurance);
    }

    @PostMapping("/patient-insurance")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Créer une nouvelle assurance patient")
    public ResponseEntity<PatientInsurance> createPatientInsurance(@Valid @RequestBody PatientInsurance patientInsurance) {
        log.info("Création d'une nouvelle assurance pour le patient ID: {}", 
                patientInsurance.getPatient().getId());
        PatientInsurance createdInsurance = insuranceService.createPatientInsurance(patientInsurance);
        return ResponseEntity.ok(createdInsurance);
    }

    @PutMapping("/patient-insurance/{id}")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Modifier une assurance patient")
    public ResponseEntity<PatientInsurance> updatePatientInsurance(@PathVariable Long id, @Valid @RequestBody PatientInsurance patientInsurance) {
        log.info("Modification de l'assurance patient ID: {}", id);
        PatientInsurance updatedInsurance = insuranceService.updatePatientInsurance(id, patientInsurance);
        return ResponseEntity.ok(updatedInsurance);
    }

    @DeleteMapping("/patient-insurance/{id}")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Supprimer une assurance patient")
    public ResponseEntity<Void> deletePatientInsurance(@PathVariable Long id) {
        log.info("Suppression de l'assurance patient ID: {}", id);
        insuranceService.deletePatientInsurance(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/patient/{patientId}/insurance")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les assurances d'un patient")
    public ResponseEntity<List<PatientInsurance>> getPatientInsurances(@PathVariable Long patientId) {
        log.info("Récupération des assurances du patient ID: {}", patientId);
        List<PatientInsurance> insurances = insuranceService.findInsurancesByPatientId(patientId);
        return ResponseEntity.ok(insurances);
    }

    // Vérification d'éligibilité
    @PostMapping("/verify-eligibility")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Vérifier l'éligibilité d'un patient")
    public ResponseEntity<Object> verifyEligibility(
            @RequestParam Long patientId,
            @RequestParam Long insuranceId,
            @RequestParam String serviceCode) {
        
        log.info("Vérification d'éligibilité pour le patient ID: {} avec l'assurance ID: {} pour le service: {}", 
                patientId, insuranceId, serviceCode);
        Object eligibility = insuranceService.verifyEligibility(patientId, insuranceId, serviceCode);
        return ResponseEntity.ok(eligibility);
    }

    @PostMapping("/check-coverage")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Vérifier la couverture pour un service")
    public ResponseEntity<Object> checkCoverage(
            @RequestParam Long patientInsuranceId,
            @RequestParam String serviceCode,
            @RequestParam BigDecimal amount) {
        
        log.info("Vérification de couverture pour l'assurance patient ID: {} pour le service: {} montant: {}", 
                patientInsuranceId, serviceCode, amount);
        Object coverage = insuranceService.checkCoverage(patientInsuranceId, serviceCode, amount);
        return ResponseEntity.ok(coverage);
    }

    // Gestion des réclamations
    @GetMapping("/claims")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Lister toutes les réclamations")
    public ResponseEntity<Page<InsuranceClaim>> getAllClaims(
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) String insuranceName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Pageable pageable) {
        
        log.info("Récupération des réclamations avec filtres: patient={}, insurance={}, status={}", 
                patientName, insuranceName, status);
        
        Page<InsuranceClaim> claims = insuranceService.findAllClaims(
                patientName, insuranceName, status, fromDate, toDate, pageable);
        return ResponseEntity.ok(claims);
    }

    @GetMapping("/claims/{id}")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer une réclamation par ID")
    public ResponseEntity<InsuranceClaim> getClaimById(@PathVariable Long id) {
        log.info("Récupération de la réclamation avec ID: {}", id);
        InsuranceClaim claim = insuranceService.findClaimById(id);
        return ResponseEntity.ok(claim);
    }

    @PostMapping("/claims")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Créer une nouvelle réclamation")
    public ResponseEntity<InsuranceClaim> createClaim(@Valid @RequestBody InsuranceClaim claim) {
        log.info("Création d'une nouvelle réclamation pour le patient ID: {}", 
                claim.getPatientInsurance().getPatient().getId());
        InsuranceClaim createdClaim = insuranceService.createClaim(claim);
        return ResponseEntity.ok(createdClaim);
    }

    @PutMapping("/claims/{id}")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Modifier une réclamation")
    public ResponseEntity<InsuranceClaim> updateClaim(@PathVariable Long id, @Valid @RequestBody InsuranceClaim claim) {
        log.info("Modification de la réclamation ID: {}", id);
        InsuranceClaim updatedClaim = insuranceService.updateClaim(id, claim);
        return ResponseEntity.ok(updatedClaim);
    }

    @PostMapping("/claims/{id}/submit")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Soumettre une réclamation")
    public ResponseEntity<InsuranceClaim> submitClaim(@PathVariable Long id) {
        log.info("Soumission de la réclamation ID: {}", id);
        InsuranceClaim submittedClaim = insuranceService.submitClaim(id);
        return ResponseEntity.ok(submittedClaim);
    }

    @PostMapping("/claims/{id}/approve")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Approuver une réclamation")
    public ResponseEntity<InsuranceClaim> approveClaim(@PathVariable Long id, @RequestParam BigDecimal approvedAmount) {
        log.info("Approbation de la réclamation ID: {} pour un montant de: {}", id, approvedAmount);
        InsuranceClaim approvedClaim = insuranceService.approveClaim(id, approvedAmount);
        return ResponseEntity.ok(approvedClaim);
    }

    @PostMapping("/claims/{id}/reject")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Rejeter une réclamation")
    public ResponseEntity<InsuranceClaim> rejectClaim(@PathVariable Long id, @RequestParam String reason) {
        log.info("Rejet de la réclamation ID: {} pour raison: {}", id, reason);
        InsuranceClaim rejectedClaim = insuranceService.rejectClaim(id, reason);
        return ResponseEntity.ok(rejectedClaim);
    }

    @PostMapping("/claims/{id}/resubmit")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Resoumettre une réclamation")
    public ResponseEntity<InsuranceClaim> resubmitClaim(@PathVariable Long id, @RequestParam String additionalInfo) {
        log.info("Resoumission de la réclamation ID: {} avec info additionnelle: {}", id, additionalInfo);
        InsuranceClaim resubmittedClaim = insuranceService.resubmitClaim(id, additionalInfo);
        return ResponseEntity.ok(resubmittedClaim);
    }

    // Traitement automatique des réclamations
    @PostMapping("/claims/auto-process")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Traitement automatique des réclamations en attente")
    public ResponseEntity<Object> autoProcessClaims() {
        log.info("Démarrage du traitement automatique des réclamations");
        Object result = insuranceService.autoProcessPendingClaims();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/claims/batch-submit")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Soumission en lot des réclamations")
    public ResponseEntity<Object> batchSubmitClaims(@RequestParam Long insuranceCompanyId) {
        log.info("Soumission en lot des réclamations pour la compagnie ID: {}", insuranceCompanyId);
        Object result = insuranceService.batchSubmitClaims(insuranceCompanyId);
        return ResponseEntity.ok(result);
    }

    // Rapports et documents
    @GetMapping("/claims/{id}/form/pdf")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Générer le formulaire de réclamation PDF")
    public ResponseEntity<byte[]> generateClaimFormPdf(@PathVariable Long id) {
        log.info("Génération du formulaire de réclamation PDF pour ID: {}", id);
        byte[] pdfContent = insuranceService.generateClaimFormPdf(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "formulaire_reclamation_" + id + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    @GetMapping("/patient/{patientId}/insurance-card/pdf")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Générer la carte d'assurance PDF d'un patient")
    public ResponseEntity<byte[]> generateInsuranceCardPdf(@PathVariable Long patientId) {
        log.info("Génération de la carte d'assurance PDF pour le patient ID: {}", patientId);
        byte[] pdfContent = insuranceService.generateInsuranceCardPdf(patientId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "carte_assurance_patient_" + patientId + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    // Statistiques et rapports
    @GetMapping("/statistics/claims")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Statistiques des réclamations")
    public ResponseEntity<Object> getClaimsStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Long insuranceCompanyId) {
        
        log.info("Calcul des statistiques des réclamations entre {} et {} pour la compagnie ID: {}", 
                fromDate, toDate, insuranceCompanyId);
        Object stats = insuranceService.getClaimsStatistics(fromDate, toDate, insuranceCompanyId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistics/reimbursements")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Statistiques des remboursements")
    public ResponseEntity<Object> getReimbursementStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        log.info("Calcul des statistiques des remboursements entre {} et {}", fromDate, toDate);
        Object stats = insuranceService.getReimbursementStatistics(fromDate, toDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/reports/claims-aging")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Rapport d'ancienneté des réclamations")
    public ResponseEntity<byte[]> generateClaimsAgingReport() {
        log.info("Génération du rapport d'ancienneté des réclamations");
        byte[] report = insuranceService.generateClaimsAgingReport();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "anciennete_reclamations.pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(report);
    }

    @GetMapping("/reports/denials-analysis")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Rapport d'analyse des refus")
    public ResponseEntity<byte[]> generateDenialsAnalysisReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        log.info("Génération du rapport d'analyse des refus entre {} et {}", fromDate, toDate);
        byte[] report = insuranceService.generateDenialsAnalysisReport(fromDate, toDate);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "analyse_refus_" + fromDate + "_" + toDate + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(report);
    }

    // Intégration avec systèmes externes
    @PostMapping("/companies/{id}/test-connection")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Tester la connexion avec une compagnie d'assurance")
    public ResponseEntity<Object> testInsuranceConnection(@PathVariable Long id) {
        log.info("Test de connexion avec la compagnie d'assurance ID: {}", id);
        Object connectionResult = insuranceService.testInsuranceConnection(id);
        return ResponseEntity.ok(connectionResult);
    }

    @PostMapping("/sync-benefits")
    @PreAuthorize("hasRole('BILLING_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Synchroniser les avantages avec les systèmes externes")
    public ResponseEntity<Object> syncBenefits(@RequestParam Long insuranceCompanyId) {
        log.info("Synchronisation des avantages pour la compagnie ID: {}", insuranceCompanyId);
        Object syncResult = insuranceService.syncBenefitsWithExternalSystem(insuranceCompanyId);
        return ResponseEntity.ok(syncResult);
    }

    // Notifications et alertes
    @GetMapping("/alerts/expired-policies")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les polices d'assurance expirées")
    public ResponseEntity<List<PatientInsurance>> getExpiredPolicies() {
        log.info("Récupération des polices d'assurance expirées");
        List<PatientInsurance> expiredPolicies = insuranceService.findExpiredPolicies();
        return ResponseEntity.ok(expiredPolicies);
    }

    @GetMapping("/alerts/expiring-soon")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les polices qui expirent bientôt")
    public ResponseEntity<List<PatientInsurance>> getExpiringSoonPolicies(@RequestParam(defaultValue = "30") int days) {
        log.info("Récupération des polices expirant dans {} jours", days);
        List<PatientInsurance> expiringSoonPolicies = insuranceService.findExpiringSoonPolicies(days);
        return ResponseEntity.ok(expiringSoonPolicies);
    }

    @GetMapping("/alerts/pending-claims")
    @PreAuthorize("hasRole('BILLING_STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les réclamations en attente depuis trop longtemps")
    public ResponseEntity<List<InsuranceClaim>> getPendingClaims(@RequestParam(defaultValue = "14") int days) {
        log.info("Récupération des réclamations en attente depuis {} jours", days);
        List<InsuranceClaim> pendingClaims = insuranceService.findLongPendingClaims(days);
        return ResponseEntity.ok(pendingClaims);
    }
}