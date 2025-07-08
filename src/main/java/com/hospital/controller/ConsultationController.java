package com.hospital.controller;

import com.hospital.entity.Consultation;
import com.hospital.service.ConsultationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Consultations", description = "API de gestion des consultations spécialisées")
@CrossOrigin(origins = "*")
public class ConsultationController {

    private final ConsultationService consultationService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle consultation", description = "Créer une nouvelle consultation dans le système")
    @ApiResponse(responseCode = "201", description = "Consultation créée avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<Consultation> createConsultation(@Valid @RequestBody Consultation consultation) {
        log.info("Requête de création de consultation reçue pour le patient ID: {}", consultation.getPatient().getId());
        Consultation createdConsultation = consultationService.createConsultation(consultation);
        return new ResponseEntity<>(createdConsultation, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une consultation par ID", description = "Récupérer les détails d'une consultation par son ID")
    @ApiResponse(responseCode = "200", description = "Consultation trouvée")
    @ApiResponse(responseCode = "404", description = "Consultation non trouvée")
    public ResponseEntity<Consultation> getConsultationById(
            @Parameter(description = "ID de la consultation") @PathVariable Long id) {
        Consultation consultation = consultationService.getConsultationById(id);
        return ResponseEntity.ok(consultation);
    }

    @GetMapping
    @Operation(summary = "Obtenir toutes les consultations", description = "Récupérer la liste de toutes les consultations avec pagination")
    @ApiResponse(responseCode = "200", description = "Liste des consultations récupérée")
    public ResponseEntity<Page<Consultation>> getAllConsultations(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "consultationDate") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Consultation> consultations = consultationService.getAllConsultations(pageable);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Obtenir les consultations par patient", description = "Récupérer toutes les consultations d'un patient")
    @ApiResponse(responseCode = "200", description = "Consultations du patient récupérées")
    public ResponseEntity<List<Consultation>> getConsultationsByPatient(
            @Parameter(description = "ID du patient") @PathVariable Long patientId) {
        List<Consultation> consultations = consultationService.getConsultationsByPatient(patientId);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Obtenir les consultations par médecin", description = "Récupérer toutes les consultations d'un médecin")
    @ApiResponse(responseCode = "200", description = "Consultations du médecin récupérées")
    public ResponseEntity<List<Consultation>> getConsultationsByDoctor(
            @Parameter(description = "ID du médecin") @PathVariable Long doctorId) {
        List<Consultation> consultations = consultationService.getConsultationsByDoctor(doctorId);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/today")
    @Operation(summary = "Obtenir les consultations d'aujourd'hui", description = "Récupérer toutes les consultations d'aujourd'hui")
    @ApiResponse(responseCode = "200", description = "Consultations d'aujourd'hui récupérées")
    public ResponseEntity<List<Consultation>> getTodayConsultations() {
        List<Consultation> consultations = consultationService.getTodayConsultations();
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/doctor/{doctorId}/today")
    @Operation(summary = "Obtenir les consultations d'aujourd'hui pour un médecin", description = "Récupérer les consultations d'aujourd'hui d'un médecin spécifique")
    @ApiResponse(responseCode = "200", description = "Consultations d'aujourd'hui du médecin récupérées")
    public ResponseEntity<List<Consultation>> getTodayConsultationsByDoctor(
            @Parameter(description = "ID du médecin") @PathVariable Long doctorId) {
        List<Consultation> consultations = consultationService.getTodayConsultationsByDoctor(doctorId);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/between-dates")
    @Operation(summary = "Obtenir les consultations par période", description = "Récupérer les consultations entre deux dates")
    @ApiResponse(responseCode = "200", description = "Consultations de la période récupérées")
    public ResponseEntity<List<Consultation>> getConsultationsBetweenDates(
            @Parameter(description = "Date de début") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Date de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Consultation> consultations = consultationService.getConsultationsBetweenDates(startDate, endDate);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/type/{consultationType}")
    @Operation(summary = "Obtenir les consultations par type", description = "Récupérer les consultations d'un type donné")
    @ApiResponse(responseCode = "200", description = "Consultations par type récupérées")
    public ResponseEntity<List<Consultation>> getConsultationsByType(
            @Parameter(description = "Type de consultation") @PathVariable String consultationType) {
        List<Consultation> consultations = consultationService.getConsultationsByType(consultationType);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Obtenir les consultations par statut", description = "Récupérer les consultations d'un statut donné")
    @ApiResponse(responseCode = "200", description = "Consultations par statut récupérées")
    public ResponseEntity<List<Consultation>> getConsultationsByStatus(
            @Parameter(description = "Statut de la consultation") @PathVariable String status) {
        List<Consultation> consultations = consultationService.getConsultationsByStatus(status);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/urgent")
    @Operation(summary = "Obtenir les consultations urgentes", description = "Récupérer toutes les consultations urgentes")
    @ApiResponse(responseCode = "200", description = "Consultations urgentes récupérées")
    public ResponseEntity<List<Consultation>> getUrgentConsultations() {
        List<Consultation> consultations = consultationService.getUrgentConsultations();
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/completed")
    @Operation(summary = "Obtenir les consultations terminées", description = "Récupérer toutes les consultations terminées")
    @ApiResponse(responseCode = "200", description = "Consultations terminées récupérées")
    public ResponseEntity<List<Consultation>> getCompletedConsultations() {
        List<Consultation> consultations = consultationService.getCompletedConsultations();
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/doctor/{doctorId}/upcoming")
    @Operation(summary = "Obtenir les prochaines consultations d'un médecin", description = "Récupérer les prochaines consultations d'un médecin")
    @ApiResponse(responseCode = "200", description = "Prochaines consultations récupérées")
    public ResponseEntity<List<Consultation>> getUpcomingConsultationsByDoctor(
            @Parameter(description = "ID du médecin") @PathVariable Long doctorId) {
        List<Consultation> consultations = consultationService.getUpcomingConsultationsByDoctor(doctorId);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des consultations", description = "Rechercher des consultations par terme")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche récupérés")
    public ResponseEntity<Page<Consultation>> searchConsultations(
            @Parameter(description = "Terme de recherche") @RequestParam String searchTerm,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("consultationDate").descending());
        Page<Consultation> consultations = consultationService.searchConsultations(searchTerm, pageable);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/recent")
    @Operation(summary = "Obtenir les consultations récentes", description = "Récupérer les consultations récentes")
    @ApiResponse(responseCode = "200", description = "Consultations récentes récupérées")
    public ResponseEntity<List<Consultation>> getRecentConsultations(
            @Parameter(description = "Nombre de consultations") @RequestParam(defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Consultation> consultations = consultationService.getRecentConsultations(pageable);
        return ResponseEntity.ok(consultations);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une consultation", description = "Mettre à jour les informations d'une consultation")
    @ApiResponse(responseCode = "200", description = "Consultation mise à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Consultation non trouvée")
    public ResponseEntity<Consultation> updateConsultation(
            @Parameter(description = "ID de la consultation") @PathVariable Long id,
            @Valid @RequestBody Consultation consultation) {
        Consultation updatedConsultation = consultationService.updateConsultation(id, consultation);
        return ResponseEntity.ok(updatedConsultation);
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Terminer une consultation", description = "Marquer une consultation comme terminée")
    @ApiResponse(responseCode = "200", description = "Consultation terminée avec succès")
    @ApiResponse(responseCode = "404", description = "Consultation non trouvée")
    public ResponseEntity<Consultation> completeConsultation(
            @Parameter(description = "ID de la consultation") @PathVariable Long id,
            @Parameter(description = "Diagnostic") @RequestParam String diagnosis,
            @Parameter(description = "Plan de traitement") @RequestParam String treatmentPlan,
            @Parameter(description = "Notes") @RequestParam(required = false) String notes) {
        Consultation consultation = consultationService.completeConsultation(id, diagnosis, treatmentPlan, notes);
        return ResponseEntity.ok(consultation);
    }

    @PatchMapping("/{id}/urgent")
    @Operation(summary = "Marquer comme urgent", description = "Marquer une consultation comme urgente")
    @ApiResponse(responseCode = "200", description = "Consultation marquée comme urgente")
    @ApiResponse(responseCode = "404", description = "Consultation non trouvée")
    public ResponseEntity<Consultation> markAsUrgent(
            @Parameter(description = "ID de la consultation") @PathVariable Long id) {
        Consultation consultation = consultationService.markAsUrgent(id);
        return ResponseEntity.ok(consultation);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut", description = "Mettre à jour le statut d'une consultation")
    @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Consultation non trouvée")
    public ResponseEntity<Consultation> updateConsultationStatus(
            @Parameter(description = "ID de la consultation") @PathVariable Long id,
            @Parameter(description = "Nouveau statut") @RequestParam String status) {
        Consultation consultation = consultationService.updateConsultationStatus(id, status);
        return ResponseEntity.ok(consultation);
    }

    @PatchMapping("/{id}/follow-up")
    @Operation(summary = "Programmer un suivi", description = "Programmer un rendez-vous de suivi")
    @ApiResponse(responseCode = "200", description = "Suivi programmé avec succès")
    @ApiResponse(responseCode = "404", description = "Consultation non trouvée")
    public ResponseEntity<Consultation> scheduleFollowUp(
            @Parameter(description = "ID de la consultation") @PathVariable Long id,
            @Parameter(description = "Date de suivi") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime followUpDate) {
        Consultation consultation = consultationService.scheduleFollowUp(id, followUpDate);
        return ResponseEntity.ok(consultation);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une consultation", description = "Supprimer définitivement une consultation")
    @ApiResponse(responseCode = "200", description = "Consultation supprimée avec succès")
    @ApiResponse(responseCode = "404", description = "Consultation non trouvée")
    public ResponseEntity<Map<String, String>> deleteConsultation(
            @Parameter(description = "ID de la consultation") @PathVariable Long id) {
        consultationService.deleteConsultation(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Consultation supprimée avec succès");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Obtenir les statistiques des consultations", description = "Récupérer les statistiques générales des consultations")
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées")
    public ResponseEntity<Map<String, Object>> getConsultationStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // Statistiques générales
        statistics.put("consultationsToday", consultationService.countConsultationsToday());
        statistics.put("urgentConsultations", consultationService.getUrgentConsultations().size());
        statistics.put("completedConsultations", consultationService.countConsultationsByStatus("COMPLETED"));
        statistics.put("inProgressConsultations", consultationService.countConsultationsByStatus("IN_PROGRESS"));
        
        // Statistiques par médecin
        List<Object[]> doctorStats = consultationService.getConsultationStatsByDoctor();
        Map<String, Long> doctorMap = new HashMap<>();
        doctorStats.forEach(stat -> doctorMap.put(stat[0].toString(), (Long) stat[1]));
        statistics.put("consultationsByDoctor", doctorMap);
        
        return ResponseEntity.ok(statistics);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}