package com.hospital.controller;

import com.hospital.entity.Doctor;
import com.hospital.service.DoctorService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Médecins", description = "API de gestion des médecins")
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @Operation(summary = "Créer un nouveau médecin", description = "Créer un nouveau médecin dans le système")
    @ApiResponse(responseCode = "201", description = "Médecin créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<Doctor> createDoctor(@Valid @RequestBody Doctor doctor) {
        log.info("Requête de création de médecin reçue: {} {}", doctor.getFirstName(), doctor.getLastName());
        Doctor createdDoctor = doctorService.createDoctor(doctor);
        return new ResponseEntity<>(createdDoctor, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un médecin par ID", description = "Récupérer les détails d'un médecin par son ID")
    @ApiResponse(responseCode = "200", description = "Médecin trouvé")
    @ApiResponse(responseCode = "404", description = "Médecin non trouvé")
    public ResponseEntity<Doctor> getDoctorById(
            @Parameter(description = "ID du médecin") @PathVariable Long id) {
        Doctor doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(doctor);
    }

    @GetMapping("/number/{doctorNumber}")
    @Operation(summary = "Obtenir un médecin par numéro", description = "Récupérer les détails d'un médecin par son numéro unique")
    @ApiResponse(responseCode = "200", description = "Médecin trouvé")
    @ApiResponse(responseCode = "404", description = "Médecin non trouvé")
    public ResponseEntity<Doctor> getDoctorByNumber(
            @Parameter(description = "Numéro unique du médecin") @PathVariable String doctorNumber) {
        Doctor doctor = doctorService.getDoctorByNumber(doctorNumber);
        return ResponseEntity.ok(doctor);
    }

    @GetMapping
    @Operation(summary = "Obtenir tous les médecins", description = "Récupérer la liste de tous les médecins avec pagination")
    @ApiResponse(responseCode = "200", description = "Liste des médecins récupérée")
    public ResponseEntity<Page<Doctor>> getAllDoctors(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "lastName") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Doctor> doctors = doctorService.getAllDoctors(pageable);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des médecins", description = "Rechercher des médecins par nom, spécialisation, email, etc.")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche récupérés")
    public ResponseEntity<Page<Doctor>> searchDoctors(
            @Parameter(description = "Terme de recherche") @RequestParam String searchTerm,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
        Page<Doctor> doctors = doctorService.searchDoctors(searchTerm, pageable);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/active")
    @Operation(summary = "Obtenir les médecins actifs", description = "Récupérer la liste de tous les médecins actifs")
    @ApiResponse(responseCode = "200", description = "Liste des médecins actifs récupérée")
    public ResponseEntity<List<Doctor>> getActiveDoctors() {
        List<Doctor> doctors = doctorService.getActiveDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/available")
    @Operation(summary = "Obtenir les médecins disponibles", description = "Récupérer la liste de tous les médecins disponibles")
    @ApiResponse(responseCode = "200", description = "Liste des médecins disponibles récupérée")
    public ResponseEntity<List<Doctor>> getAvailableDoctors() {
        List<Doctor> doctors = doctorService.getAvailableDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/specialization/{specialization}")
    @Operation(summary = "Obtenir les médecins par spécialisation", description = "Récupérer les médecins d'une spécialisation donnée")
    @ApiResponse(responseCode = "200", description = "Liste des médecins par spécialisation récupérée")
    public ResponseEntity<List<Doctor>> getDoctorsBySpecialization(
            @Parameter(description = "Spécialisation") @PathVariable String specialization) {
        List<Doctor> doctors = doctorService.getDoctorsBySpecialization(specialization);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Obtenir les médecins par département", description = "Récupérer les médecins d'un département donné")
    @ApiResponse(responseCode = "200", description = "Liste des médecins par département récupérée")
    public ResponseEntity<List<Doctor>> getDoctorsByDepartment(
            @Parameter(description = "ID du département") @PathVariable Long departmentId) {
        List<Doctor> doctors = doctorService.getDoctorsByDepartment(departmentId);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/today-consultations")
    @Operation(summary = "Obtenir les médecins avec consultations aujourd'hui", description = "Récupérer les médecins ayant des consultations aujourd'hui")
    @ApiResponse(responseCode = "200", description = "Liste des médecins avec consultations aujourd'hui récupérée")
    public ResponseEntity<List<Doctor>> getDoctorsWithTodayConsultations() {
        List<Doctor> doctors = doctorService.getDoctorsWithTodayConsultations();
        return ResponseEntity.ok(doctors);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un médecin", description = "Mettre à jour les informations d'un médecin existant")
    @ApiResponse(responseCode = "200", description = "Médecin mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Médecin non trouvé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<Doctor> updateDoctor(
            @Parameter(description = "ID du médecin") @PathVariable Long id,
            @Valid @RequestBody Doctor doctor) {
        Doctor updatedDoctor = doctorService.updateDoctor(id, doctor);
        return ResponseEntity.ok(updatedDoctor);
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Mettre à jour la disponibilité", description = "Mettre à jour la disponibilité d'un médecin")
    @ApiResponse(responseCode = "200", description = "Disponibilité mise à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Médecin non trouvé")
    public ResponseEntity<Map<String, String>> setDoctorAvailability(
            @Parameter(description = "ID du médecin") @PathVariable Long id,
            @Parameter(description = "Disponibilité") @RequestParam Boolean isAvailable) {
        doctorService.setDoctorAvailability(id, isAvailable);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Disponibilité mise à jour avec succès");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/consultation-fee")
    @Operation(summary = "Mettre à jour le tarif de consultation", description = "Mettre à jour le tarif de consultation d'un médecin")
    @ApiResponse(responseCode = "200", description = "Tarif mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Médecin non trouvé")
    public ResponseEntity<Doctor> updateConsultationFee(
            @Parameter(description = "ID du médecin") @PathVariable Long id,
            @Parameter(description = "Nouveau tarif") @RequestParam Double newFee) {
        Doctor doctor = doctorService.updateDoctorConsultationFee(id, newFee);
        return ResponseEntity.ok(doctor);
    }

    @PatchMapping("/{id}/assign-department")
    @Operation(summary = "Assigner à un département", description = "Assigner un médecin à un département")
    @ApiResponse(responseCode = "200", description = "Médecin assigné avec succès")
    @ApiResponse(responseCode = "404", description = "Médecin ou département non trouvé")
    public ResponseEntity<Doctor> assignDoctorToDepartment(
            @Parameter(description = "ID du médecin") @PathVariable Long id,
            @Parameter(description = "ID du département") @RequestParam Long departmentId) {
        Doctor doctor = doctorService.assignDoctorToDepartment(id, departmentId);
        return ResponseEntity.ok(doctor);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Désactiver un médecin", description = "Désactiver un médecin dans le système")
    @ApiResponse(responseCode = "200", description = "Médecin désactivé avec succès")
    @ApiResponse(responseCode = "404", description = "Médecin non trouvé")
    public ResponseEntity<Map<String, String>> deactivateDoctor(
            @Parameter(description = "ID du médecin") @PathVariable Long id) {
        doctorService.deactivateDoctor(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Médecin désactivé avec succès");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reactivate")
    @Operation(summary = "Réactiver un médecin", description = "Réactiver un médecin dans le système")
    @ApiResponse(responseCode = "200", description = "Médecin réactivé avec succès")
    @ApiResponse(responseCode = "404", description = "Médecin non trouvé")
    public ResponseEntity<Map<String, String>> reactivateDoctor(
            @Parameter(description = "ID du médecin") @PathVariable Long id) {
        doctorService.reactivateDoctor(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Médecin réactivé avec succès");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un médecin", description = "Supprimer définitivement un médecin du système")
    @ApiResponse(responseCode = "200", description = "Médecin supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Médecin non trouvé")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @Parameter(description = "ID du médecin") @PathVariable Long id) {
        doctorService.deleteDoctor(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Médecin supprimé avec succès");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Obtenir les statistiques des médecins", description = "Récupérer les statistiques générales des médecins")
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées")
    public ResponseEntity<Map<String, Object>> getDoctorStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // Statistiques générales
        statistics.put("totalActiveDoctors", doctorService.countActiveDoctors());
        statistics.put("availableDoctors", doctorService.getAvailableDoctors().size());
        statistics.put("doctorsWithTodayConsultations", doctorService.getDoctorsWithTodayConsultations().size());
        
        // Statistiques par spécialisation
        List<Object[]> specializationStats = doctorService.getDoctorStatsBySpecialization();
        Map<String, Long> specializationMap = new HashMap<>();
        specializationStats.forEach(stat -> specializationMap.put(stat[0].toString(), (Long) stat[1]));
        statistics.put("doctorsBySpecialization", specializationMap);
        
        return ResponseEntity.ok(statistics);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}