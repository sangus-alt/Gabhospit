package com.hospital.controller;

import com.hospital.entity.ImagingResult;
import com.hospital.service.ImagingService;
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
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/imaging")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Imagerie", description = "API de gestion de l'imagerie médicale (RIS)")
public class ImagingController {

    private final ImagingService imagingService;

    @GetMapping("/results")
    @PreAuthorize("hasRole('RADIOLOGIST') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Lister tous les résultats d'imagerie")
    public ResponseEntity<Page<ImagingResult>> getAllImagingResults(
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) String examType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Pageable pageable) {
        
        log.info("Récupération des résultats d'imagerie avec filtres: patient={}, examType={}, status={}, priority={}", 
                patientName, examType, status, priority);
        
        Page<ImagingResult> results = imagingService.findAllImagingResults(
                patientName, examType, status, priority, fromDate, toDate, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/results/{id}")
    @PreAuthorize("hasRole('RADIOLOGIST') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer un résultat d'imagerie par ID")
    public ResponseEntity<ImagingResult> getImagingResultById(@PathVariable Long id) {
        log.info("Récupération du résultat d'imagerie avec ID: {}", id);
        ImagingResult result = imagingService.findImagingResultById(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/results")
    @PreAuthorize("hasRole('RADIOLOGIST') or hasRole('IMAGING_TECHNICIAN') or hasRole('ADMIN')")
    @Operation(summary = "Créer un nouveau résultat d'imagerie")
    public ResponseEntity<ImagingResult> createImagingResult(@Valid @RequestBody ImagingResult imagingResult) {
        log.info("Création d'un nouveau résultat d'imagerie pour le patient ID: {}", 
                imagingResult.getPatient().getId());
        ImagingResult createdResult = imagingService.createImagingResult(imagingResult);
        return ResponseEntity.ok(createdResult);
    }

    @PutMapping("/results/{id}")
    @PreAuthorize("hasRole('RADIOLOGIST') or hasRole('IMAGING_TECHNICIAN') or hasRole('ADMIN')")
    @Operation(summary = "Modifier un résultat d'imagerie")
    public ResponseEntity<ImagingResult> updateImagingResult(@PathVariable Long id, @Valid @RequestBody ImagingResult imagingResult) {
        log.info("Modification du résultat d'imagerie ID: {}", id);
        ImagingResult updatedResult = imagingService.updateImagingResult(id, imagingResult);
        return ResponseEntity.ok(updatedResult);
    }

    @DeleteMapping("/results/{id}")
    @PreAuthorize("hasRole('RADIOLOGIST') or hasRole('ADMIN')")
    @Operation(summary = "Supprimer un résultat d'imagerie")
    public ResponseEntity<Void> deleteImagingResult(@PathVariable Long id) {
        log.info("Suppression du résultat d'imagerie ID: {}", id);
        imagingService.deleteImagingResult(id);
        return ResponseEntity.ok().build();
    }

    // Gestion des ordres d'imagerie
    @PostMapping("/orders")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Créer un ordre d'imagerie")
    public ResponseEntity<ImagingResult> createImagingOrder(@Valid @RequestBody ImagingResult order) {
        log.info("Création d'un ordre d'imagerie pour le patient ID: {}", order.getPatient().getId());
        ImagingResult createdOrder = imagingService.createImagingOrder(order);
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping("/orders/pending")
    @PreAuthorize("hasRole('IMAGING_TECHNICIAN') or hasRole('RADIOLOGIST') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les ordres d'imagerie en attente")
    public ResponseEntity<List<ImagingResult>> getPendingOrders() {
        log.info("Récupération des ordres d'imagerie en attente");
        List<ImagingResult> pendingOrders = imagingService.findPendingOrders();
        return ResponseEntity.ok(pendingOrders);
    }

    @GetMapping("/orders/urgent")
    @PreAuthorize("hasRole('IMAGING_TECHNICIAN') or hasRole('RADIOLOGIST') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les ordres d'imagerie urgents")
    public ResponseEntity<List<ImagingResult>> getUrgentOrders() {
        log.info("Récupération des ordres d'imagerie urgents");
        List<ImagingResult> urgentOrders = imagingService.findUrgentOrders();
        return ResponseEntity.ok(urgentOrders);
    }

    @PostMapping("/results/{id}/start-exam")
    @PreAuthorize("hasRole('IMAGING_TECHNICIAN') or hasRole('ADMIN')")
    @Operation(summary = "Démarrer un examen d'imagerie")
    public ResponseEntity<ImagingResult> startExam(@PathVariable Long id, @RequestParam String technician) {
        log.info("Démarrage de l'examen d'imagerie ID: {} par {}", id, technician);
        ImagingResult startedExam = imagingService.startExam(id, technician);
        return ResponseEntity.ok(startedExam);
    }

    @PostMapping("/results/{id}/complete-exam")
    @PreAuthorize("hasRole('IMAGING_TECHNICIAN') or hasRole('ADMIN')")
    @Operation(summary = "Terminer un examen d'imagerie")
    public ResponseEntity<ImagingResult> completeExam(@PathVariable Long id) {
        log.info("Finalisation de l'examen d'imagerie ID: {}", id);
        ImagingResult completedExam = imagingService.completeExam(id);
        return ResponseEntity.ok(completedExam);
    }

    // Upload d'images DICOM
    @PostMapping("/results/{id}/upload-images")
    @PreAuthorize("hasRole('IMAGING_TECHNICIAN') or hasRole('RADIOLOGIST') or hasRole('ADMIN')")
    @Operation(summary = "Télécharger des images DICOM")
    public ResponseEntity<ImagingResult> uploadDicomImages(
            @PathVariable Long id,
            @RequestParam("files") MultipartFile[] files) {
        
        log.info("Téléchargement de {} images DICOM pour le résultat ID: {}", files.length, id);
        ImagingResult updatedResult = imagingService.uploadDicomImages(id, files);
        return ResponseEntity.ok(updatedResult);
    }

    @GetMapping("/results/{id}/images")
    @PreAuthorize("hasRole('RADIOLOGIST') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les images d'un examen")
    public ResponseEntity<List<String>> getExamImages(@PathVariable Long id) {
        log.info("Récupération des images pour l'examen ID: {}", id);
        List<String> imageUrls = imagingService.getExamImages(id);
        return ResponseEntity.ok(imageUrls);
    }

    @GetMapping("/results/{id}/dicom/{imageId}")
    @PreAuthorize("hasRole('RADIOLOGIST') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Télécharger une image DICOM")
    public ResponseEntity<byte[]> downloadDicomImage(@PathVariable Long id, @PathVariable String imageId) {
        log.info("Téléchargement de l'image DICOM {} pour l'examen ID: {}", imageId, id);
        byte[] imageData = imagingService.downloadDicomImage(id, imageId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("filename", "image_" + imageId + ".dcm");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(imageData);
    }

    // Interprétation radiologique
    @PostMapping("/results/{id}/interpret")
    @PreAuthorize("hasRole('RADIOLOGIST') or hasRole('ADMIN')")
    @Operation(summary = "Ajouter une interprétation radiologique")
    public ResponseEntity<ImagingResult> addInterpretation(
            @PathVariable Long id, 
            @RequestParam String interpretation,
            @RequestParam String radiologist) {
        
        log.info("Ajout d'interprétation pour l'examen ID: {} par {}", id, radiologist);
        ImagingResult interpretedResult = imagingService.addInterpretation(id, interpretation, radiologist);
        return ResponseEntity.ok(interpretedResult);
    }

    @PostMapping("/results/{id}/validate")
    @PreAuthorize("hasRole('RADIOLOGIST') or hasRole('ADMIN')")
    @Operation(summary = "Valider un résultat d'imagerie")
    public ResponseEntity<ImagingResult> validateResult(@PathVariable Long id, @RequestParam String validatedBy) {
        log.info("Validation du résultat d'imagerie ID: {} par {}", id, validatedBy);
        ImagingResult validatedResult = imagingService.validateResult(id, validatedBy);
        return ResponseEntity.ok(validatedResult);
    }

    @PostMapping("/results/{id}/release")
    @PreAuthorize("hasRole('RADIOLOGIST') or hasRole('ADMIN')")
    @Operation(summary = "Libérer un résultat d'imagerie")
    public ResponseEntity<ImagingResult> releaseResult(@PathVariable Long id) {
        log.info("Libération du résultat d'imagerie ID: {}", id);
        ImagingResult releasedResult = imagingService.releaseResult(id);
        return ResponseEntity.ok(releasedResult);
    }

    // Rapports et documents
    @GetMapping("/results/{id}/report/pdf")
    @PreAuthorize("hasRole('RADIOLOGIST') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Générer le rapport d'imagerie PDF")
    public ResponseEntity<byte[]> generateImagingReportPdf(@PathVariable Long id) {
        log.info("Génération du rapport d'imagerie PDF pour l'examen ID: {}", id);
        byte[] pdfContent = imagingService.generateImagingReportPdf(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "rapport_imagerie_" + id + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    // Gestion par patient
    @GetMapping("/patient/{patientId}/results")
    @PreAuthorize("hasRole('RADIOLOGIST') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les résultats d'imagerie d'un patient")
    public ResponseEntity<List<ImagingResult>> getPatientImagingResults(@PathVariable Long patientId) {
        log.info("Récupération des résultats d'imagerie du patient ID: {}", patientId);
        List<ImagingResult> results = imagingService.findResultsByPatientId(patientId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/patient/{patientId}/history")
    @PreAuthorize("hasRole('RADIOLOGIST') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer l'historique d'imagerie d'un patient")
    public ResponseEntity<Object> getPatientImagingHistory(@PathVariable Long patientId) {
        log.info("Récupération de l'historique d'imagerie du patient ID: {}", patientId);
        Object history = imagingService.getPatientImagingHistory(patientId);
        return ResponseEntity.ok(history);
    }

    // Gestion des équipements
    @GetMapping("/equipment")
    @PreAuthorize("hasRole('IMAGING_TECHNICIAN') or hasRole('ADMIN')")
    @Operation(summary = "Lister les équipements d'imagerie")
    public ResponseEntity<Object> getImagingEquipment() {
        log.info("Récupération de la liste des équipements d'imagerie");
        Object equipment = imagingService.getImagingEquipment();
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/equipment/{equipmentId}/schedule")
    @PreAuthorize("hasRole('IMAGING_TECHNICIAN') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer le planning d'un équipement")
    public ResponseEntity<Object> getEquipmentSchedule(
            @PathVariable String equipmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.info("Récupération du planning de l'équipement {} pour le {}", equipmentId, date);
        Object schedule = imagingService.getEquipmentSchedule(equipmentId, date);
        return ResponseEntity.ok(schedule);
    }

    @PostMapping("/equipment/{equipmentId}/maintenance")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Programmer une maintenance d'équipement")
    public ResponseEntity<Object> scheduleEquipmentMaintenance(
            @PathVariable String equipmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate maintenanceDate,
            @RequestParam String description) {
        
        log.info("Programmation d'une maintenance pour l'équipement {} le {}", equipmentId, maintenanceDate);
        Object maintenance = imagingService.scheduleEquipmentMaintenance(equipmentId, maintenanceDate, description);
        return ResponseEntity.ok(maintenance);
    }

    // Statistiques et indicateurs
    @GetMapping("/statistics/workload")
    @PreAuthorize("hasRole('RADIOLOGIST') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir les statistiques de charge de travail")
    public ResponseEntity<Object> getWorkloadStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String examType) {
        
        log.info("Calcul des statistiques de charge entre {} et {} pour le type: {}", fromDate, toDate, examType);
        Object stats = imagingService.getWorkloadStatistics(fromDate, toDate, examType);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistics/equipment-usage")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir les statistiques d'utilisation des équipements")
    public ResponseEntity<Object> getEquipmentUsageStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        log.info("Calcul des statistiques d'utilisation des équipements entre {} et {}", fromDate, toDate);
        Object stats = imagingService.getEquipmentUsageStatistics(fromDate, toDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/quality-metrics")
    @PreAuthorize("hasRole('RADIOLOGIST') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir les métriques de qualité")
    public ResponseEntity<Object> getQualityMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        log.info("Calcul des métriques de qualité entre {} et {}", fromDate, toDate);
        Object metrics = imagingService.getQualityMetrics(fromDate, toDate);
        return ResponseEntity.ok(metrics);
    }

    // Gestion PACS (Picture Archiving and Communication System)
    @PostMapping("/pacs/store")
    @PreAuthorize("hasRole('IMAGING_TECHNICIAN') or hasRole('ADMIN')")
    @Operation(summary = "Stocker des images dans PACS")
    public ResponseEntity<Object> storeToPacs(@PathVariable Long resultId) {
        log.info("Stockage des images de l'examen ID: {} dans PACS", resultId);
        Object pacsResponse = imagingService.storeToPacs(resultId);
        return ResponseEntity.ok(pacsResponse);
    }

    @GetMapping("/pacs/retrieve/{studyId}")
    @PreAuthorize("hasRole('RADIOLOGIST') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer une étude depuis PACS")
    public ResponseEntity<Object> retrieveFromPacs(@PathVariable String studyId) {
        log.info("Récupération de l'étude {} depuis PACS", studyId);
        Object study = imagingService.retrieveFromPacs(studyId);
        return ResponseEntity.ok(study);
    }
}