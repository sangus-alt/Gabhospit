package com.hospital.controller;

import com.hospital.entity.LabOrder;
import com.hospital.entity.LabResult;
import com.hospital.entity.LabTest;
import com.hospital.entity.LabTestParameter;
import com.hospital.service.LaboratoryService;
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
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/laboratory")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Laboratoire", description = "API de gestion du laboratoire (LIMS)")
public class LaboratoryController {

    private final LaboratoryService laboratoryService;

    // Gestion des tests de laboratoire
    @GetMapping("/tests")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Lister tous les tests de laboratoire")
    public ResponseEntity<Page<LabTest>> getAllLabTests(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        
        log.info("Récupération des tests de laboratoire avec filtres: name={}, category={}, active={}", 
                name, category, active);
        
        Page<LabTest> tests = laboratoryService.findAllLabTests(name, category, active, pageable);
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/tests/{id}")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer un test de laboratoire par ID")
    public ResponseEntity<LabTest> getLabTestById(@PathVariable Long id) {
        log.info("Récupération du test de laboratoire avec ID: {}", id);
        LabTest labTest = laboratoryService.findLabTestById(id);
        return ResponseEntity.ok(labTest);
    }

    @PostMapping("/tests")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Créer un nouveau test de laboratoire")
    public ResponseEntity<LabTest> createLabTest(@Valid @RequestBody LabTest labTest) {
        log.info("Création d'un nouveau test de laboratoire: {}", labTest.getName());
        LabTest createdTest = laboratoryService.createLabTest(labTest);
        return ResponseEntity.ok(createdTest);
    }

    @PutMapping("/tests/{id}")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Modifier un test de laboratoire")
    public ResponseEntity<LabTest> updateLabTest(@PathVariable Long id, @Valid @RequestBody LabTest labTest) {
        log.info("Modification du test de laboratoire ID: {}", id);
        LabTest updatedTest = laboratoryService.updateLabTest(id, labTest);
        return ResponseEntity.ok(updatedTest);
    }

    @DeleteMapping("/tests/{id}")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Supprimer un test de laboratoire")
    public ResponseEntity<Void> deleteLabTest(@PathVariable Long id) {
        log.info("Suppression du test de laboratoire ID: {}", id);
        laboratoryService.deleteLabTest(id);
        return ResponseEntity.ok().build();
    }

    // Gestion des paramètres de tests
    @PostMapping("/tests/{testId}/parameters")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Ajouter un paramètre à un test")
    public ResponseEntity<LabTestParameter> addTestParameter(@PathVariable Long testId, @Valid @RequestBody LabTestParameter parameter) {
        log.info("Ajout d'un paramètre au test ID: {}", testId);
        LabTestParameter addedParameter = laboratoryService.addTestParameter(testId, parameter);
        return ResponseEntity.ok(addedParameter);
    }

    // Gestion des ordres de laboratoire
    @GetMapping("/orders")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Lister tous les ordres de laboratoire")
    public ResponseEntity<Page<LabOrder>> getAllLabOrders(
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Pageable pageable) {
        
        log.info("Récupération des ordres de laboratoire avec filtres: patient={}, status={}, priority={}", 
                patientName, status, priority);
        
        Page<LabOrder> orders = laboratoryService.findAllLabOrders(patientName, status, priority, fromDate, toDate, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer un ordre de laboratoire par ID")
    public ResponseEntity<LabOrder> getLabOrderById(@PathVariable Long id) {
        log.info("Récupération de l'ordre de laboratoire avec ID: {}", id);
        LabOrder labOrder = laboratoryService.findLabOrderById(id);
        return ResponseEntity.ok(labOrder);
    }

    @PostMapping("/orders")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('LAB_TECHNICIAN') or hasRole('ADMIN')")
    @Operation(summary = "Créer un nouvel ordre de laboratoire")
    public ResponseEntity<LabOrder> createLabOrder(@Valid @RequestBody LabOrder labOrder) {
        log.info("Création d'un nouvel ordre de laboratoire pour le patient ID: {}", 
                labOrder.getPatient().getId());
        LabOrder createdOrder = laboratoryService.createLabOrder(labOrder);
        return ResponseEntity.ok(createdOrder);
    }

    @PutMapping("/orders/{id}")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('ADMIN')")
    @Operation(summary = "Modifier un ordre de laboratoire")
    public ResponseEntity<LabOrder> updateLabOrder(@PathVariable Long id, @Valid @RequestBody LabOrder labOrder) {
        log.info("Modification de l'ordre de laboratoire ID: {}", id);
        LabOrder updatedOrder = laboratoryService.updateLabOrder(id, labOrder);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/orders/{id}/collect")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('ADMIN')")
    @Operation(summary = "Marquer un échantillon comme collecté")
    public ResponseEntity<LabOrder> collectSample(@PathVariable Long id, @RequestParam String collectedBy) {
        log.info("Collecte de l'échantillon pour l'ordre ID: {} par {}", id, collectedBy);
        LabOrder collectedOrder = laboratoryService.collectSample(id, collectedBy);
        return ResponseEntity.ok(collectedOrder);
    }

    @PostMapping("/orders/{id}/process")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('ADMIN')")
    @Operation(summary = "Commencer le traitement d'un ordre")
    public ResponseEntity<LabOrder> startProcessing(@PathVariable Long id) {
        log.info("Démarrage du traitement pour l'ordre ID: {}", id);
        LabOrder processingOrder = laboratoryService.startProcessing(id);
        return ResponseEntity.ok(processingOrder);
    }

    // Gestion des résultats
    @GetMapping("/results")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Lister tous les résultats de laboratoire")
    public ResponseEntity<Page<LabResult>> getAllLabResults(
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) String testName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Pageable pageable) {
        
        log.info("Récupération des résultats de laboratoire avec filtres: patient={}, test={}, status={}", 
                patientName, testName, status);
        
        Page<LabResult> results = laboratoryService.findAllLabResults(patientName, testName, status, fromDate, toDate, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/results/{id}")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer un résultat de laboratoire par ID")
    public ResponseEntity<LabResult> getLabResultById(@PathVariable Long id) {
        log.info("Récupération du résultat de laboratoire avec ID: {}", id);
        LabResult labResult = laboratoryService.findLabResultById(id);
        return ResponseEntity.ok(labResult);
    }

    @PostMapping("/results")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('ADMIN')")
    @Operation(summary = "Saisir un résultat de laboratoire")
    public ResponseEntity<LabResult> createLabResult(@Valid @RequestBody LabResult labResult) {
        log.info("Saisie d'un nouveau résultat de laboratoire pour l'ordre ID: {}", 
                labResult.getLabOrder().getId());
        LabResult createdResult = laboratoryService.createLabResult(labResult);
        return ResponseEntity.ok(createdResult);
    }

    @PutMapping("/results/{id}")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('ADMIN')")
    @Operation(summary = "Modifier un résultat de laboratoire")
    public ResponseEntity<LabResult> updateLabResult(@PathVariable Long id, @Valid @RequestBody LabResult labResult) {
        log.info("Modification du résultat de laboratoire ID: {}", id);
        LabResult updatedResult = laboratoryService.updateLabResult(id, labResult);
        return ResponseEntity.ok(updatedResult);
    }

    @PostMapping("/results/{id}/validate")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Valider un résultat de laboratoire")
    public ResponseEntity<LabResult> validateResult(@PathVariable Long id, @RequestParam String validatedBy) {
        log.info("Validation du résultat ID: {} par {}", id, validatedBy);
        LabResult validatedResult = laboratoryService.validateResult(id, validatedBy);
        return ResponseEntity.ok(validatedResult);
    }

    @PostMapping("/results/{id}/release")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Libérer un résultat de laboratoire")
    public ResponseEntity<LabResult> releaseResult(@PathVariable Long id) {
        log.info("Libération du résultat ID: {}", id);
        LabResult releasedResult = laboratoryService.releaseResult(id);
        return ResponseEntity.ok(releasedResult);
    }

    // Rapports et statistiques
    @GetMapping("/results/{id}/pdf")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Générer le PDF d'un résultat")
    public ResponseEntity<byte[]> generateResultPdf(@PathVariable Long id) {
        log.info("Génération du PDF pour le résultat ID: {}", id);
        byte[] pdfContent = laboratoryService.generateResultPdf(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "resultat_labo_" + id + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    @GetMapping("/patient/{patientId}/results")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les résultats d'un patient")
    public ResponseEntity<List<LabResult>> getPatientResults(@PathVariable Long patientId) {
        log.info("Récupération des résultats du patient ID: {}", patientId);
        List<LabResult> results = laboratoryService.findResultsByPatientId(patientId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/statistics/workload")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir les statistiques de charge de travail")
    public ResponseEntity<Object> getWorkloadStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        log.info("Calcul des statistiques de charge entre {} et {}", fromDate, toDate);
        Object stats = laboratoryService.getWorkloadStatistics(fromDate, toDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/orders/urgent")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les ordres urgents")
    public ResponseEntity<List<LabOrder>> getUrgentOrders() {
        log.info("Récupération des ordres urgents");
        List<LabOrder> urgentOrders = laboratoryService.findUrgentOrders();
        return ResponseEntity.ok(urgentOrders);
    }

    @GetMapping("/quality-control")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Rapport de contrôle qualité")
    public ResponseEntity<byte[]> generateQualityControlReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        log.info("Génération du rapport de contrôle qualité entre {} et {}", fromDate, toDate);
        byte[] report = laboratoryService.generateQualityControlReport(fromDate, toDate);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "controle_qualite_" + fromDate + "_" + toDate + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(report);
    }
}