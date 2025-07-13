package com.hospital.controller;

import com.hospital.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tableau de Bord", description = "API du tableau de bord principal avec statistiques temps réel")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final ConsultationService consultationService;
    private final AppointmentService appointmentService;
    private final LaboratoryService laboratoryService;
    private final QueueService queueService;
    private final PharmacyService pharmacyService;
    private final BillingService billingService;

    @GetMapping("/overview")
    @Operation(summary = "Vue d'ensemble du système", description = "Récupérer les statistiques principales du système hospitalier")
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès")
    public ResponseEntity<Map<String, Object>> getSystemOverview() {
        log.info("Récupération des statistiques du tableau de bord");
        
        Map<String, Object> overview = new HashMap<>();
        
        // Statistiques des patients
        Map<String, Object> patientStats = new HashMap<>();
        patientStats.put("totalActive", patientService.countActivePatients());
        patientStats.put("registeredToday", patientService.countPatientsRegisteredSince(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0)));
        patientStats.put("registeredThisMonth", patientService.countPatientsRegisteredSince(LocalDateTime.now().minusMonths(1)));
        overview.put("patients", patientStats);
        
        // Statistiques des médecins
        Map<String, Object> doctorStats = new HashMap<>();
        doctorStats.put("totalActive", doctorService.countActiveDoctors());
        doctorStats.put("available", doctorService.getAvailableDoctors().size());
        doctorStats.put("withTodayConsultations", doctorService.getDoctorsWithTodayConsultations().size());
        overview.put("doctors", doctorStats);
        
        // Statistiques des consultations
        Map<String, Object> consultationStats = new HashMap<>();
        consultationStats.put("today", consultationService.countConsultationsToday());
        consultationStats.put("urgent", consultationService.getUrgentConsultations().size());
        consultationStats.put("completed", consultationService.countConsultationsByStatus("COMPLETED"));
        consultationStats.put("inProgress", consultationService.countConsultationsByStatus("IN_PROGRESS"));
        overview.put("consultations", consultationStats);
        
        // Statistiques des rendez-vous
        Map<String, Object> appointmentStats = new HashMap<>();
        appointmentStats.put("today", appointmentService.countTodayAppointments());
        appointmentStats.put("pending", appointmentService.countAppointmentsByStatus("SCHEDULED"));
        appointmentStats.put("completed", appointmentService.countAppointmentsByStatus("COMPLETED"));
        overview.put("appointments", appointmentStats);
        
        // Statistiques du laboratoire
        Map<String, Object> labStats = new HashMap<>();
        labStats.put("ordersToday", laboratoryService.countTodayOrders());
        labStats.put("pending", laboratoryService.countPendingOrders());
        labStats.put("completedToday", laboratoryService.countCompletedOrdersToday());
        overview.put("laboratory", labStats);
        
        // Statistiques de la file d'attente
        Map<String, Object> queueStats = new HashMap<>();
        queueStats.put("waiting", queueService.countWaitingPatients());
        queueStats.put("todayTotal", queueService.countTodayQueue());
        queueStats.put("inProgress", queueService.countPatientsInProgress());
        queueStats.put("averageWaitTime", queueService.getAverageWaitingTime());
        overview.put("queue", queueStats);
        
        // Statistiques de la pharmacie
        Map<String, Object> pharmacyStats = new HashMap<>();
        pharmacyStats.put("activeMedications", pharmacyService.countActiveMedications());
        pharmacyStats.put("lowStock", pharmacyService.countLowStockMedications());
        pharmacyStats.put("pendingPrescriptions", pharmacyService.countPendingPrescriptions());
        pharmacyStats.put("dispensedToday", pharmacyService.countTodayDispensedPrescriptions());
        overview.put("pharmacy", pharmacyStats);
        
        // Statistiques financières
        Map<String, Object> billingStats = new HashMap<>();
        billingStats.put("todayRevenue", billingService.getTodayRevenue());
        billingStats.put("billsToday", billingService.countTodayBills());
        billingStats.put("unpaidBills", billingService.countUnpaidBills());
        billingStats.put("overdueBills", billingService.countOverdueBills());
        billingStats.put("totalOutstanding", billingService.getTotalOutstandingAmount());
        overview.put("billing", billingStats);
        
        return ResponseEntity.ok(overview);
    }

    @GetMapping("/real-time")
    @Operation(summary = "Données temps réel", description = "Récupérer les données temps réel pour les tableaux de bord dynamiques")
    @ApiResponse(responseCode = "200", description = "Données temps réel récupérées")
    public ResponseEntity<Map<String, Object>> getRealTimeData() {
        Map<String, Object> realTime = new HashMap<>();
        
        // Activité en temps réel
        realTime.put("activeConsultations", consultationService.countConsultationsByStatus("IN_PROGRESS"));
        realTime.put("waitingPatients", queueService.countWaitingPatients());
        realTime.put("availableDoctors", doctorService.getAvailableDoctors().size());
        realTime.put("urgentCases", consultationService.getUrgentConsultations().size());
        realTime.put("pendingLabOrders", laboratoryService.countPendingOrders());
        realTime.put("pendingPrescriptions", pharmacyService.countPendingPrescriptions());
        realTime.put("todayRevenue", billingService.getTodayRevenue());
        
        // Alertes
        Map<String, Object> alerts = new HashMap<>();
        alerts.put("lowStockMedications", pharmacyService.countLowStockMedications());
        alerts.put("overdueBills", billingService.countOverdueBills());
        alerts.put("longWaitingPatients", queueService.getLongWaitingPatients(60).size());
        alerts.put("missedAppointments", appointmentService.getMissedAppointments().size());
        realTime.put("alerts", alerts);
        
        return ResponseEntity.ok(realTime);
    }

    @GetMapping("/quick-stats")
    @Operation(summary = "Statistiques rapides", description = "Récupérer les statistiques rapides pour les widgets du dashboard")
    @ApiResponse(responseCode = "200", description = "Statistiques rapides récupérées")
    public ResponseEntity<Map<String, Object>> getQuickStats() {
        Map<String, Object> quickStats = new HashMap<>();
        
        // Chiffres clés
        quickStats.put("totalPatients", patientService.countActivePatients());
        quickStats.put("totalDoctors", doctorService.countActiveDoctors());
        quickStats.put("todayConsultations", consultationService.countConsultationsToday());
        quickStats.put("todayRevenue", billingService.getTodayRevenue());
        quickStats.put("waitingPatients", queueService.countWaitingPatients());
        quickStats.put("pendingLabOrders", laboratoryService.countPendingOrders());
        
        // Taux d'occupation (exemple simplifié)
        double occupancyRate = (double) consultationService.countConsultationsByStatus("IN_PROGRESS") / doctorService.countActiveDoctors() * 100;
        quickStats.put("occupancyRate", Math.round(occupancyRate * 100.0) / 100.0);
        
        // Temps d'attente moyen
        quickStats.put("averageWaitTime", queueService.getAverageWaitingTime());
        
        return ResponseEntity.ok(quickStats);
    }

    @GetMapping("/department-overview")
    @Operation(summary = "Vue d'ensemble par département", description = "Récupérer les statistiques par département")
    @ApiResponse(responseCode = "200", description = "Statistiques par département récupérées")
    public ResponseEntity<Map<String, Object>> getDepartmentOverview() {
        Map<String, Object> departmentData = new HashMap<>();
        
        // Statistiques de la file d'attente par département
        departmentData.put("queueByDepartment", queueService.getTodayQueueStatsByDepartment());
        
        // Statistiques des médecins par spécialisation
        departmentData.put("doctorsBySpecialization", doctorService.getDoctorStatsBySpecialization());
        
        // Consultations par médecin
        departmentData.put("consultationsByDoctor", consultationService.getConsultationStatsByDoctor());
        
        return ResponseEntity.ok(departmentData);
    }

    @GetMapping("/financial-summary")
    @Operation(summary = "Résumé financier", description = "Récupérer le résumé financier du jour")
    @ApiResponse(responseCode = "200", description = "Résumé financier récupéré")
    public ResponseEntity<Map<String, Object>> getFinancialSummary() {
        Map<String, Object> financial = new HashMap<>();
        
        // Revenus
        financial.put("todayRevenue", billingService.getTodayRevenue());
        financial.put("totalOutstanding", billingService.getTotalOutstandingAmount());
        
        // Statistiques des factures
        financial.put("billsToday", billingService.countTodayBills());
        financial.put("unpaidBills", billingService.countUnpaidBills());
        financial.put("overdueBills", billingService.countOverdueBills());
        
        // Répartition par statut de paiement
        financial.put("paymentStatistics", billingService.getPaymentStatistics());
        
        return ResponseEntity.ok(financial);
    }

    @GetMapping("/health-check")
    @Operation(summary = "Vérification de l'état du système", description = "Vérifier l'état de santé du système")
    @ApiResponse(responseCode = "200", description = "État du système récupéré")
    public ResponseEntity<Map<String, Object>> getSystemHealthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Test de connectivité aux services
            health.put("patientService", patientService.countActivePatients() != 0 ? "UP" : "DOWN");
            health.put("doctorService", doctorService.countActiveDoctors() != 0 ? "UP" : "DOWN");
            health.put("queueService", queueService.countWaitingPatients() != 0 ? "UP" : "DOWN");
            health.put("labService", laboratoryService.countPendingOrders() != 0 ? "UP" : "DOWN");
            health.put("pharmacyService", pharmacyService.countActiveMedications() != 0 ? "UP" : "DOWN");
            health.put("billingService", billingService.getTodayRevenue() != 0.0 ? "UP" : "DOWN");
            
            health.put("overallStatus", "UP");
            health.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            health.put("overallStatus", "DOWN");
            health.put("error", e.getMessage());
            health.put("timestamp", LocalDateTime.now());
        }
        
        return ResponseEntity.ok(health);
    }
}