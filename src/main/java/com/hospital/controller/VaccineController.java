package com.hospital.controller;

import com.hospital.entity.Vaccine;
import com.hospital.entity.VaccinationRecord;
import com.hospital.service.VaccineService;
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
@RequestMapping("/api/vaccines")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vaccination", description = "API de gestion des vaccins et carnets de vaccination")
public class VaccineController {

    private final VaccineService vaccineService;

    // Gestion des vaccins
    @GetMapping
    @PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Lister tous les vaccins")
    public ResponseEntity<Page<Vaccine>> getAllVaccines(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) String diseaseType,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        
        log.info("Récupération des vaccins avec filtres: name={}, manufacturer={}, diseaseType={}, active={}", 
                name, manufacturer, diseaseType, active);
        
        Page<Vaccine> vaccines = vaccineService.findAllVaccines(name, manufacturer, diseaseType, active, pageable);
        return ResponseEntity.ok(vaccines);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer un vaccin par ID")
    public ResponseEntity<Vaccine> getVaccineById(@PathVariable Long id) {
        log.info("Récupération du vaccin avec ID: {}", id);
        Vaccine vaccine = vaccineService.findVaccineById(id);
        return ResponseEntity.ok(vaccine);
    }

    @PostMapping
    @PreAuthorize("hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Ajouter un nouveau vaccin")
    public ResponseEntity<Vaccine> createVaccine(@Valid @RequestBody Vaccine vaccine) {
        log.info("Création d'un nouveau vaccin: {}", vaccine.getName());
        Vaccine createdVaccine = vaccineService.createVaccine(vaccine);
        return ResponseEntity.ok(createdVaccine);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Modifier un vaccin")
    public ResponseEntity<Vaccine> updateVaccine(@PathVariable Long id, @Valid @RequestBody Vaccine vaccine) {
        log.info("Modification du vaccin ID: {}", id);
        Vaccine updatedVaccine = vaccineService.updateVaccine(id, vaccine);
        return ResponseEntity.ok(updatedVaccine);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Supprimer un vaccin")
    public ResponseEntity<Void> deleteVaccine(@PathVariable Long id) {
        log.info("Suppression du vaccin ID: {}", id);
        vaccineService.deleteVaccine(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasRole('NURSE') or hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les vaccins qui expirent bientôt")
    public ResponseEntity<List<Vaccine>> getExpiringVaccines(@RequestParam(defaultValue = "30") int days) {
        log.info("Récupération des vaccins expirant dans {} jours", days);
        List<Vaccine> expiringVaccines = vaccineService.findExpiringVaccines(days);
        return ResponseEntity.ok(expiringVaccines);
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('NURSE') or hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les vaccins en rupture de stock")
    public ResponseEntity<List<Vaccine>> getLowStockVaccines() {
        log.info("Récupération des vaccins en rupture de stock");
        List<Vaccine> lowStockVaccines = vaccineService.findLowStockVaccines();
        return ResponseEntity.ok(lowStockVaccines);
    }

    // Gestion des enregistrements de vaccination
    @GetMapping("/records")
    @PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Lister tous les enregistrements de vaccination")
    public ResponseEntity<Page<VaccinationRecord>> getAllVaccinationRecords(
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) String vaccineName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Pageable pageable) {
        
        log.info("Récupération des enregistrements de vaccination avec filtres: patient={}, vaccine={}", 
                patientName, vaccineName);
        
        Page<VaccinationRecord> records = vaccineService.findAllVaccinationRecords(
                patientName, vaccineName, fromDate, toDate, pageable);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/records/{id}")
    @PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer un enregistrement de vaccination par ID")
    public ResponseEntity<VaccinationRecord> getVaccinationRecordById(@PathVariable Long id) {
        log.info("Récupération de l'enregistrement de vaccination avec ID: {}", id);
        VaccinationRecord record = vaccineService.findVaccinationRecordById(id);
        return ResponseEntity.ok(record);
    }

    @PostMapping("/records")
    @PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Enregistrer une vaccination")
    public ResponseEntity<VaccinationRecord> recordVaccination(@Valid @RequestBody VaccinationRecord record) {
        log.info("Enregistrement d'une vaccination pour le patient ID: {}", record.getPatient().getId());
        VaccinationRecord createdRecord = vaccineService.recordVaccination(record);
        return ResponseEntity.ok(createdRecord);
    }

    @PutMapping("/records/{id}")
    @PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Modifier un enregistrement de vaccination")
    public ResponseEntity<VaccinationRecord> updateVaccinationRecord(
            @PathVariable Long id, @Valid @RequestBody VaccinationRecord record) {
        log.info("Modification de l'enregistrement de vaccination ID: {}", id);
        VaccinationRecord updatedRecord = vaccineService.updateVaccinationRecord(id, record);
        return ResponseEntity.ok(updatedRecord);
    }

    @DeleteMapping("/records/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Supprimer un enregistrement de vaccination")
    public ResponseEntity<Void> deleteVaccinationRecord(@PathVariable Long id) {
        log.info("Suppression de l'enregistrement de vaccination ID: {}", id);
        vaccineService.deleteVaccinationRecord(id);
        return ResponseEntity.ok().build();
    }

    // Carnets de vaccination par patient
    @GetMapping("/patient/{patientId}/records")
    @PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer le carnet de vaccination d'un patient")
    public ResponseEntity<List<VaccinationRecord>> getPatientVaccinationRecords(@PathVariable Long patientId) {
        log.info("Récupération du carnet de vaccination du patient ID: {}", patientId);
        List<VaccinationRecord> records = vaccineService.findVaccinationRecordsByPatientId(patientId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/patient/{patientId}/vaccination-card/pdf")
    @PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Générer le carnet de vaccination PDF d'un patient")
    public ResponseEntity<byte[]> generateVaccinationCardPdf(@PathVariable Long patientId) {
        log.info("Génération du carnet de vaccination PDF pour le patient ID: {}", patientId);
        byte[] pdfContent = vaccineService.generateVaccinationCardPdf(patientId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "carnet_vaccination_patient_" + patientId + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    @GetMapping("/patient/{patientId}/due-vaccines")
    @PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les vaccins dus pour un patient")
    public ResponseEntity<List<Vaccine>> getDueVaccines(@PathVariable Long patientId) {
        log.info("Récupération des vaccins dus pour le patient ID: {}", patientId);
        List<Vaccine> dueVaccines = vaccineService.findDueVaccinesForPatient(patientId);
        return ResponseEntity.ok(dueVaccines);
    }

    @GetMapping("/patient/{patientId}/next-appointments")
    @PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les prochains rappels de vaccination pour un patient")
    public ResponseEntity<List<VaccinationRecord>> getUpcomingVaccinations(@PathVariable Long patientId) {
        log.info("Récupération des prochains rappels pour le patient ID: {}", patientId);
        List<VaccinationRecord> upcomingVaccinations = vaccineService.findUpcomingVaccinationsForPatient(patientId);
        return ResponseEntity.ok(upcomingVaccinations);
    }

    // Rappels et notifications
    @GetMapping("/reminders/due-today")
    @PreAuthorize("hasRole('NURSE') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les rappels de vaccination dus aujourd'hui")
    public ResponseEntity<List<VaccinationRecord>> getTodayReminders() {
        log.info("Récupération des rappels de vaccination dus aujourd'hui");
        List<VaccinationRecord> todayReminders = vaccineService.findTodayVaccinationReminders();
        return ResponseEntity.ok(todayReminders);
    }

    @GetMapping("/reminders/upcoming")
    @PreAuthorize("hasRole('NURSE') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les prochains rappels de vaccination")
    public ResponseEntity<List<VaccinationRecord>> getUpcomingReminders(@RequestParam(defaultValue = "7") int days) {
        log.info("Récupération des rappels de vaccination dans les {} prochains jours", days);
        List<VaccinationRecord> upcomingReminders = vaccineService.findUpcomingVaccinationReminders(days);
        return ResponseEntity.ok(upcomingReminders);
    }

    // Statistiques et rapports
    @GetMapping("/statistics/coverage")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir les statistiques de couverture vaccinale")
    public ResponseEntity<Object> getVaccinationCoverage(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String ageGroup) {
        
        log.info("Calcul de la couverture vaccinale entre {} et {} pour le groupe d'âge: {}", 
                fromDate, toDate, ageGroup);
        Object coverage = vaccineService.getVaccinationCoverage(fromDate, toDate, ageGroup);
        return ResponseEntity.ok(coverage);
    }

    @GetMapping("/statistics/administered")
    @PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir les statistiques des vaccins administrés")
    public ResponseEntity<Object> getAdministeredStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        log.info("Calcul des statistiques des vaccins administrés entre {} et {}", fromDate, toDate);
        Object stats = vaccineService.getAdministeredVaccinesStatistics(fromDate, toDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/reports/vaccination-schedule")
    @PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Générer le rapport du calendrier vaccinal")
    public ResponseEntity<byte[]> generateVaccinationScheduleReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        log.info("Génération du rapport du calendrier vaccinal entre {} et {}", fromDate, toDate);
        byte[] report = vaccineService.generateVaccinationScheduleReport(fromDate, toDate);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "calendrier_vaccinal_" + fromDate + "_" + toDate + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(report);
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasRole('NURSE') or hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir l'inventaire des vaccins")
    public ResponseEntity<List<Vaccine>> getVaccineInventory() {
        log.info("Récupération de l'inventaire des vaccins");
        List<Vaccine> inventory = vaccineService.getVaccineInventory();
        return ResponseEntity.ok(inventory);
    }

    @PostMapping("/{vaccineId}/restock")
    @PreAuthorize("hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Réapprovisionner un vaccin")
    public ResponseEntity<Vaccine> restockVaccine(@PathVariable Long vaccineId, @RequestParam int quantity) {
        log.info("Réapprovisionnement du vaccin ID: {} avec {} unités", vaccineId, quantity);
        Vaccine restockedVaccine = vaccineService.restockVaccine(vaccineId, quantity);
        return ResponseEntity.ok(restockedVaccine);
    }

    // Validations et contrôles
    @PostMapping("/validate-batch/{batchNumber}")
    @PreAuthorize("hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Valider un lot de vaccins")
    public ResponseEntity<Object> validateVaccineBatch(@PathVariable String batchNumber) {
        log.info("Validation du lot de vaccin: {}", batchNumber);
        Object validation = vaccineService.validateVaccineBatch(batchNumber);
        return ResponseEntity.ok(validation);
    }

    @GetMapping("/cold-chain-monitoring")
    @PreAuthorize("hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Monitoring de la chaîne du froid")
    public ResponseEntity<Object> getColdChainMonitoring() {
        log.info("Récupération du monitoring de la chaîne du froid");
        Object monitoring = vaccineService.getColdChainMonitoring();
        return ResponseEntity.ok(monitoring);
    }
}