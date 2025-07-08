package com.hospital.controller;

import com.hospital.entity.QueueManagement;
import com.hospital.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File d'Attente", description = "API de gestion intelligente de la file d'attente")
@CrossOrigin(origins = "*")
public class QueueController {

    private final QueueService queueService;

    @PostMapping("/add")
    @Operation(summary = "Ajouter un patient à la file d'attente", description = "Ajouter un patient à la file d'attente d'un département")
    @ApiResponse(responseCode = "201", description = "Patient ajouté à la file d'attente avec succès")
    @ApiResponse(responseCode = "400", description = "Erreur lors de l'ajout")
    public ResponseEntity<QueueManagement> addPatientToQueue(
            @Parameter(description = "ID du patient") @RequestParam Long patientId,
            @Parameter(description = "ID du département") @RequestParam Long departmentId,
            @Parameter(description = "Priorité (1-5)") @RequestParam(required = false) Integer priority,
            @Parameter(description = "Notes additionnelles") @RequestParam(required = false) String notes) {
        
        QueueManagement queueItem = queueService.addPatientToQueue(patientId, departmentId, priority, notes);
        return new ResponseEntity<>(queueItem, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un élément de la file d'attente", description = "Récupérer les détails d'un élément de la file d'attente")
    @ApiResponse(responseCode = "200", description = "Élément trouvé")
    @ApiResponse(responseCode = "404", description = "Élément non trouvé")
    public ResponseEntity<QueueManagement> getQueueItemById(
            @Parameter(description = "ID de l'élément") @PathVariable Long id) {
        QueueManagement queueItem = queueService.getQueueItemById(id);
        return ResponseEntity.ok(queueItem);
    }

    @GetMapping("/active")
    @Operation(summary = "Obtenir la file d'attente active", description = "Récupérer tous les patients en attente")
    @ApiResponse(responseCode = "200", description = "File d'attente active récupérée")
    public ResponseEntity<List<QueueManagement>> getActiveQueue() {
        List<QueueManagement> queue = queueService.getActiveQueue();
        return ResponseEntity.ok(queue);
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Obtenir la file d'attente par département", description = "Récupérer la file d'attente d'un département spécifique")
    @ApiResponse(responseCode = "200", description = "File d'attente du département récupérée")
    public ResponseEntity<List<QueueManagement>> getQueueByDepartment(
            @Parameter(description = "ID du département") @PathVariable Long departmentId) {
        List<QueueManagement> queue = queueService.getQueueByDepartment(departmentId);
        return ResponseEntity.ok(queue);
    }

    @GetMapping("/today")
    @Operation(summary = "Obtenir la file d'attente d'aujourd'hui", description = "Récupérer tous les patients de la file d'attente d'aujourd'hui")
    @ApiResponse(responseCode = "200", description = "File d'attente d'aujourd'hui récupérée")
    public ResponseEntity<List<QueueManagement>> getTodayQueue() {
        List<QueueManagement> queue = queueService.getTodayQueue();
        return ResponseEntity.ok(queue);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Obtenir la file d'attente par statut", description = "Récupérer les patients par statut")
    @ApiResponse(responseCode = "200", description = "File d'attente par statut récupérée")
    public ResponseEntity<List<QueueManagement>> getQueueByStatus(
            @Parameter(description = "Statut") @PathVariable String status) {
        List<QueueManagement> queue = queueService.getQueueByStatus(status);
        return ResponseEntity.ok(queue);
    }

    @GetMapping("/next")
    @Operation(summary = "Obtenir les prochains patients", description = "Récupérer les prochains patients à appeler")
    @ApiResponse(responseCode = "200", description = "Prochains patients récupérés")
    public ResponseEntity<List<QueueManagement>> getNextPatients(
            @Parameter(description = "Nombre de patients") @RequestParam(defaultValue = "5") int limit) {
        List<QueueManagement> patients = queueService.getNextPatients(limit);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/in-progress")
    @Operation(summary = "Obtenir les patients en cours de traitement", description = "Récupérer les patients actuellement en traitement")
    @ApiResponse(responseCode = "200", description = "Patients en cours récupérés")
    public ResponseEntity<List<QueueManagement>> getPatientsInProgress() {
        List<QueueManagement> patients = queueService.getPatientsInProgress();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/high-priority")
    @Operation(summary = "Obtenir les patients prioritaires", description = "Récupérer les patients avec priorité élevée")
    @ApiResponse(responseCode = "200", description = "Patients prioritaires récupérés")
    public ResponseEntity<List<QueueManagement>> getHighPriorityPatients(
            @Parameter(description = "Priorité minimale") @RequestParam(defaultValue = "4") Integer minPriority) {
        List<QueueManagement> patients = queueService.getHighPriorityPatients(minPriority);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/long-waiting")
    @Operation(summary = "Obtenir les patients en attente depuis longtemps", description = "Récupérer les patients qui attendent depuis trop longtemps")
    @ApiResponse(responseCode = "200", description = "Patients en longue attente récupérés")
    public ResponseEntity<List<QueueManagement>> getLongWaitingPatients(
            @Parameter(description = "Temps d'attente max en minutes") @RequestParam(defaultValue = "60") Integer maxWaitMinutes) {
        List<QueueManagement> patients = queueService.getLongWaitingPatients(maxWaitMinutes);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{id}/position")
    @Operation(summary = "Obtenir la position dans la file d'attente", description = "Récupérer la position d'un patient dans la file d'attente")
    @ApiResponse(responseCode = "200", description = "Position récupérée")
    public ResponseEntity<Map<String, Object>> getQueuePosition(
            @Parameter(description = "ID de l'élément") @PathVariable Long id) {
        Integer position = queueService.getQueuePosition(id);
        Map<String, Object> response = new HashMap<>();
        response.put("queuePosition", position);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/average-wait-time")
    @Operation(summary = "Obtenir le temps d'attente moyen", description = "Récupérer le temps d'attente moyen global")
    @ApiResponse(responseCode = "200", description = "Temps d'attente moyen récupéré")
    public ResponseEntity<Map<String, Object>> getAverageWaitingTime() {
        Double averageTime = queueService.getAverageWaitingTime();
        Map<String, Object> response = new HashMap<>();
        response.put("averageWaitingTimeMinutes", averageTime);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/department/{departmentId}/average-wait-time")
    @Operation(summary = "Obtenir le temps d'attente moyen par département", description = "Récupérer le temps d'attente moyen d'un département")
    @ApiResponse(responseCode = "200", description = "Temps d'attente moyen du département récupéré")
    public ResponseEntity<Map<String, Object>> getAverageWaitingTimeByDepartment(
            @Parameter(description = "ID du département") @PathVariable Long departmentId) {
        Double averageTime = queueService.getAverageWaitingTimeByDepartment(departmentId);
        Map<String, Object> response = new HashMap<>();
        response.put("averageWaitingTimeMinutes", averageTime);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher dans la file d'attente", description = "Rechercher des patients dans la file d'attente")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche récupérés")
    public ResponseEntity<Page<QueueManagement>> searchQueue(
            @Parameter(description = "Terme de recherche") @RequestParam String searchTerm,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<QueueManagement> results = queueService.searchQueue(searchTerm, pageable);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/department/{departmentId}/call-next")
    @Operation(summary = "Appeler le prochain patient", description = "Appeler le prochain patient en file d'attente")
    @ApiResponse(responseCode = "200", description = "Patient appelé avec succès")
    @ApiResponse(responseCode = "404", description = "Aucun patient en attente")
    public ResponseEntity<QueueManagement> callNextPatient(
            @Parameter(description = "ID du département") @PathVariable Long departmentId) {
        QueueManagement nextPatient = queueService.callNextPatient(departmentId);
        return ResponseEntity.ok(nextPatient);
    }

    @PatchMapping("/{id}/start-service")
    @Operation(summary = "Commencer le service", description = "Marquer le début du service pour un patient")
    @ApiResponse(responseCode = "200", description = "Service commencé")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    public ResponseEntity<QueueManagement> startService(
            @Parameter(description = "ID de l'élément") @PathVariable Long id) {
        QueueManagement queueItem = queueService.startService(id);
        return ResponseEntity.ok(queueItem);
    }

    @PatchMapping("/{id}/complete-service")
    @Operation(summary = "Terminer le service", description = "Marquer la fin du service pour un patient")
    @ApiResponse(responseCode = "200", description = "Service terminé")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    public ResponseEntity<QueueManagement> completeService(
            @Parameter(description = "ID de l'élément") @PathVariable Long id,
            @Parameter(description = "Notes du service") @RequestParam(required = false) String serviceNotes) {
        QueueManagement queueItem = queueService.completeService(id, serviceNotes);
        return ResponseEntity.ok(queueItem);
    }

    @PatchMapping("/{id}/no-show")
    @Operation(summary = "Marquer comme absent", description = "Marquer un patient comme absent")
    @ApiResponse(responseCode = "200", description = "Patient marqué comme absent")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    public ResponseEntity<QueueManagement> markAsNoShow(
            @Parameter(description = "ID de l'élément") @PathVariable Long id) {
        QueueManagement queueItem = queueService.markAsNoShow(id);
        return ResponseEntity.ok(queueItem);
    }

    @PatchMapping("/{id}/priority")
    @Operation(summary = "Mettre à jour la priorité", description = "Changer la priorité d'un patient dans la file d'attente")
    @ApiResponse(responseCode = "200", description = "Priorité mise à jour")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    public ResponseEntity<QueueManagement> updatePriority(
            @Parameter(description = "ID de l'élément") @PathVariable Long id,
            @Parameter(description = "Nouvelle priorité") @RequestParam Integer newPriority) {
        QueueManagement queueItem = queueService.updatePriority(id, newPriority);
        return ResponseEntity.ok(queueItem);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Retirer de la file d'attente", description = "Retirer un patient de la file d'attente")
    @ApiResponse(responseCode = "200", description = "Patient retiré avec succès")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    public ResponseEntity<Map<String, String>> removeFromQueue(
            @Parameter(description = "ID de l'élément") @PathVariable Long id,
            @Parameter(description = "Raison du retrait") @RequestParam String reason) {
        queueService.removeFromQueue(id, reason);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Patient retiré de la file d'attente avec succès");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Obtenir les statistiques de la file d'attente", description = "Récupérer les statistiques générales de la file d'attente")
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées")
    public ResponseEntity<Map<String, Object>> getQueueStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // Statistiques générales
        statistics.put("waitingPatients", queueService.countWaitingPatients());
        statistics.put("todayQueue", queueService.countTodayQueue());
        statistics.put("patientsInProgress", queueService.countPatientsInProgress());
        statistics.put("averageWaitingTime", queueService.getAverageWaitingTime());
        
        // Statistiques par département
        List<Object[]> departmentStats = queueService.getTodayQueueStatsByDepartment();
        Map<String, Long> departmentMap = new HashMap<>();
        departmentStats.forEach(stat -> departmentMap.put(stat[0].toString(), (Long) stat[1]));
        statistics.put("queueByDepartment", departmentMap);
        
        // Statistiques par statut
        List<Object[]> statusStats = queueService.getQueueStatsByStatus();
        Map<String, Long> statusMap = new HashMap<>();
        statusStats.forEach(stat -> statusMap.put(stat[0].toString(), (Long) stat[1]));
        statistics.put("queueByStatus", statusMap);
        
        return ResponseEntity.ok(statistics);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}