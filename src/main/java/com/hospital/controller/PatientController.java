package com.hospital.controller;

import com.hospital.entity.Patient;
import com.hospital.service.PatientService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Patients", description = "API de gestion des patients")
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @Operation(summary = "Créer un nouveau patient", description = "Créer un nouveau patient dans le système")
    @ApiResponse(responseCode = "201", description = "Patient créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient patient) {
        log.info("Requête de création de patient reçue: {} {}", patient.getFirstName(), patient.getLastName());
        Patient createdPatient = patientService.createPatient(patient);
        return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un patient par ID", description = "Récupérer les détails d'un patient par son ID")
    @ApiResponse(responseCode = "200", description = "Patient trouvé")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    public ResponseEntity<Patient> getPatientById(
            @Parameter(description = "ID du patient") @PathVariable Long id) {
        Patient patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    @GetMapping("/number/{patientNumber}")
    @Operation(summary = "Obtenir un patient par numéro", description = "Récupérer les détails d'un patient par son numéro unique")
    @ApiResponse(responseCode = "200", description = "Patient trouvé")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    public ResponseEntity<Patient> getPatientByNumber(
            @Parameter(description = "Numéro unique du patient") @PathVariable String patientNumber) {
        Patient patient = patientService.getPatientByNumber(patientNumber);
        return ResponseEntity.ok(patient);
    }

    @GetMapping
    @Operation(summary = "Obtenir tous les patients", description = "Récupérer la liste de tous les patients avec pagination")
    @ApiResponse(responseCode = "200", description = "Liste des patients récupérée")
    public ResponseEntity<Page<Patient>> getAllPatients(
            @Parameter(description = "Numéro de page (commence à 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "lastName") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Patient> patients = patientService.getAllPatients(pageable);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des patients", description = "Rechercher des patients par nom, email, téléphone ou numéro")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche récupérés")
    public ResponseEntity<Page<Patient>> searchPatients(
            @Parameter(description = "Terme de recherche") @RequestParam String searchTerm,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
        Page<Patient> patients = patientService.searchPatients(searchTerm, pageable);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/active")
    @Operation(summary = "Obtenir les patients actifs", description = "Récupérer la liste de tous les patients actifs")
    @ApiResponse(responseCode = "200", description = "Liste des patients actifs récupérée")
    public ResponseEntity<List<Patient>> getActivePatients() {
        List<Patient> patients = patientService.getActivePatients();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/gender/{gender}")
    @Operation(summary = "Obtenir les patients par genre", description = "Récupérer les patients d'un genre spécifique")
    @ApiResponse(responseCode = "200", description = "Liste des patients par genre récupérée")
    public ResponseEntity<List<Patient>> getPatientsByGender(
            @Parameter(description = "Genre du patient") @PathVariable Patient.Gender gender) {
        List<Patient> patients = patientService.getPatientsByGender(gender);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/blood-type/{bloodType}")
    @Operation(summary = "Obtenir les patients par groupe sanguin", description = "Récupérer les patients d'un groupe sanguin spécifique")
    @ApiResponse(responseCode = "200", description = "Liste des patients par groupe sanguin récupérée")
    public ResponseEntity<List<Patient>> getPatientsByBloodType(
            @Parameter(description = "Groupe sanguin") @PathVariable Patient.BloodType bloodType) {
        List<Patient> patients = patientService.getPatientsByBloodType(bloodType);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Obtenir les patients par ville", description = "Récupérer les patients d'une ville spécifique")
    @ApiResponse(responseCode = "200", description = "Liste des patients par ville récupérée")
    public ResponseEntity<List<Patient>> getPatientsByCity(
            @Parameter(description = "Ville") @PathVariable String city) {
        List<Patient> patients = patientService.getPatientsByCity(city);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/recent")
    @Operation(summary = "Obtenir les patients récents", description = "Récupérer les patients enregistrés récemment")
    @ApiResponse(responseCode = "200", description = "Liste des patients récents récupérée")
    public ResponseEntity<List<Patient>> getRecentlyRegisteredPatients(
            @Parameter(description = "Nombre de patients à récupérer") @RequestParam(defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Patient> patients = patientService.getRecentlyRegisteredPatients(pageable);
        return ResponseEntity.ok(patients);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un patient", description = "Mettre à jour les informations d'un patient existant")
    @ApiResponse(responseCode = "200", description = "Patient mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<Patient> updatePatient(
            @Parameter(description = "ID du patient") @PathVariable Long id,
            @Valid @RequestBody Patient patient) {
        Patient updatedPatient = patientService.updatePatient(id, patient);
        return ResponseEntity.ok(updatedPatient);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Désactiver un patient", description = "Désactiver un patient dans le système")
    @ApiResponse(responseCode = "200", description = "Patient désactivé avec succès")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    public ResponseEntity<Map<String, String>> deactivatePatient(
            @Parameter(description = "ID du patient") @PathVariable Long id) {
        patientService.deactivatePatient(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Patient désactivé avec succès");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reactivate")
    @Operation(summary = "Réactiver un patient", description = "Réactiver un patient dans le système")
    @ApiResponse(responseCode = "200", description = "Patient réactivé avec succès")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    public ResponseEntity<Map<String, String>> reactivatePatient(
            @Parameter(description = "ID du patient") @PathVariable Long id) {
        patientService.reactivatePatient(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Patient réactivé avec succès");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/visit")
    @Operation(summary = "Mettre à jour la dernière visite", description = "Mettre à jour la date de dernière visite d'un patient")
    @ApiResponse(responseCode = "200", description = "Date de visite mise à jour")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    public ResponseEntity<Map<String, String>> updateLastVisit(
            @Parameter(description = "ID du patient") @PathVariable Long id) {
        patientService.updateLastVisitDate(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Date de dernière visite mise à jour");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un patient", description = "Supprimer définitivement un patient du système")
    @ApiResponse(responseCode = "200", description = "Patient supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    public ResponseEntity<Map<String, String>> deletePatient(
            @Parameter(description = "ID du patient") @PathVariable Long id) {
        patientService.deletePatient(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Patient supprimé avec succès");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Obtenir les statistiques des patients", description = "Récupérer les statistiques générales des patients")
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées")
    public ResponseEntity<Map<String, Object>> getPatientStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // Statistiques générales
        statistics.put("totalActivePatients", patientService.countActivePatients());
        statistics.put("patientsRegisteredToday", 
                patientService.countPatientsRegisteredSince(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0)));
        statistics.put("patientsRegisteredThisWeek", 
                patientService.countPatientsRegisteredSince(LocalDateTime.now().minusWeeks(1)));
        statistics.put("patientsRegisteredThisMonth", 
                patientService.countPatientsRegisteredSince(LocalDateTime.now().minusMonths(1)));
        
        // Statistiques par genre
        List<Object[]> genderStats = patientService.getPatientStatsByGender();
        Map<String, Long> genderMap = new HashMap<>();
        genderStats.forEach(stat -> genderMap.put(stat[0].toString(), (Long) stat[1]));
        statistics.put("patientsByGender", genderMap);
        
        // Statistiques par groupe sanguin
        List<Object[]> bloodTypeStats = patientService.getPatientStatsByBloodType();
        Map<String, Long> bloodTypeMap = new HashMap<>();
        bloodTypeStats.forEach(stat -> bloodTypeMap.put(stat[0].toString(), (Long) stat[1]));
        statistics.put("patientsByBloodType", bloodTypeMap);
        
        return ResponseEntity.ok(statistics);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}