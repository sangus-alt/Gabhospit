package com.hospital.controller;

import com.hospital.entity.Patient;
import com.hospital.entity.MedicalCertificate;
import com.hospital.entity.Prescription;
import com.hospital.service.PrintService;
import com.hospital.service.PatientService;
import com.hospital.service.MedicalCertificateService;
import com.hospital.service.PharmacyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/print")
@RequiredArgsConstructor
@Tag(name = "Impression", description = "APIs pour l'impression des documents et cartes")
public class PrintController {

    private final PrintService printService;
    private final PatientService patientService;
    private final MedicalCertificateService certificateService;
    private final PharmacyService pharmacyService;

    @GetMapping("/patient-card/{patientId}")
    @Operation(summary = "Générer une carte patient pour impression")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('NURSE')")
    public ResponseEntity<Map<String, Object>> generatePatientCard(
            @Parameter(description = "ID du patient")
            @PathVariable Long patientId) {
        try {
            Patient patient = patientService.findById(patientId);
            if (patient == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Patient non trouvé");
                return ResponseEntity.notFound().build();
            }

            String cardHtml = printService.generatePatientCard(patient);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("html", cardHtml);
            response.put("patientName", patient.getFirstName() + " " + patient.getLastName());
            response.put("patientNumber", patient.getPatientNumber());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la génération de la carte: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping(value = "/patient-photo/{patientId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload photo du patient pour la carte")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('NURSE')")
    public ResponseEntity<Map<String, String>> uploadPatientPhoto(
            @Parameter(description = "ID du patient")
            @PathVariable Long patientId,
            @Parameter(description = "Fichier photo (max 2MB)")
            @RequestParam("photo") MultipartFile photoFile) {
        try {
            if (photoFile == null || photoFile.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Le fichier photo ne peut pas être vide");
                return ResponseEntity.badRequest().body(response);
            }

            // Validation du type de fichier
            String contentType = photoFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Le fichier doit être une image");
                return ResponseEntity.badRequest().body(response);
            }

            // Validation de la taille (max 2MB)
            if (photoFile.getSize() > 2 * 1024 * 1024) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "La taille du fichier ne peut pas dépasser 2MB");
                return ResponseEntity.badRequest().body(response);
            }

            Patient patient = patientService.findById(patientId);
            if (patient == null) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Patient non trouvé");
                return ResponseEntity.notFound().build();
            }

            // Conversion en Base64
            String photoBase64 = Base64.getEncoder().encodeToString(photoFile.getBytes());
            patient.setPhotoBase64("data:" + contentType + ";base64," + photoBase64);
            
            patientService.updatePatient(patientId, patient);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Photo uploadée avec succès");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de l'upload de la photo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/prescription/{prescriptionId}")
    @Operation(summary = "Générer une ordonnance pour impression")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<Map<String, Object>> generatePrescription(
            @Parameter(description = "ID de la prescription")
            @PathVariable Long prescriptionId) {
        try {
            Prescription prescription = pharmacyService.findPrescriptionById(prescriptionId);
            if (prescription == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Prescription non trouvée");
                return ResponseEntity.notFound().build();
            }

            String prescriptionHtml = printService.generatePrescription(prescription);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("html", prescriptionHtml);
            response.put("patientName", prescription.getPatient().getFirstName() + " " + prescription.getPatient().getLastName());
            response.put("doctorName", "Dr. " + prescription.getDoctor().getFirstName() + " " + prescription.getDoctor().getLastName());
            response.put("prescriptionDate", prescription.getCreatedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la génération de l'ordonnance: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/certificate/{certificateId}")
    @Operation(summary = "Générer un certificat médical pour impression")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<Map<String, Object>> generateMedicalCertificate(
            @Parameter(description = "ID du certificat médical")
            @PathVariable Long certificateId) {
        try {
            MedicalCertificate certificate = certificateService.findById(certificateId);
            if (certificate == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Certificat médical non trouvé");
                return ResponseEntity.notFound().build();
            }

            String certificateHtml = printService.generateMedicalCertificate(certificate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("html", certificateHtml);
            response.put("patientName", certificate.getPatient().getFirstName() + " " + certificate.getPatient().getLastName());
            response.put("doctorName", "Dr. " + certificate.getDoctor().getFirstName() + " " + certificate.getDoctor().getLastName());
            response.put("certificateDate", certificate.getCreatedAt());
            response.put("certificateType", certificate.getCertificateType());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la génération du certificat: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/patient-card/{patientId}/download")
    @Operation(summary = "Télécharger la carte patient au format HTML")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('NURSE')")
    public ResponseEntity<String> downloadPatientCard(
            @Parameter(description = "ID du patient")
            @PathVariable Long patientId) {
        try {
            Patient patient = patientService.findById(patientId);
            if (patient == null) {
                return ResponseEntity.notFound().build();
            }

            String cardHtml = printService.generatePatientCard(patient);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=carte_patient_" + patient.getPatientNumber() + ".html");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(cardHtml);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/prescription/{prescriptionId}/download")
    @Operation(summary = "Télécharger l'ordonnance au format HTML")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<String> downloadPrescription(
            @Parameter(description = "ID de la prescription")
            @PathVariable Long prescriptionId) {
        try {
            Prescription prescription = pharmacyService.findPrescriptionById(prescriptionId);
            if (prescription == null) {
                return ResponseEntity.notFound().build();
            }

            String prescriptionHtml = printService.generatePrescription(prescription);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=ordonnance_" + prescription.getId() + ".html");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(prescriptionHtml);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/certificate/{certificateId}/download")
    @Operation(summary = "Télécharger le certificat médical au format HTML")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<String> downloadMedicalCertificate(
            @Parameter(description = "ID du certificat médical")
            @PathVariable Long certificateId) {
        try {
            MedicalCertificate certificate = certificateService.findById(certificateId);
            if (certificate == null) {
                return ResponseEntity.notFound().build();
            }

            String certificateHtml = printService.generateMedicalCertificate(certificate);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=certificat_medical_" + certificate.getId() + ".html");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(certificateHtml);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/templates")
    @Operation(summary = "Récupérer la liste des templates d'impression disponibles")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<Map<String, Object>> getAvailableTemplates() {
        Map<String, Object> templates = new HashMap<>();
        
        // Templates de cartes
        Map<String, String> cardTemplates = new HashMap<>();
        cardTemplates.put("default", "Carte patient standard");
        cardTemplates.put("compact", "Carte patient compacte");
        templates.put("patientCards", cardTemplates);
        
        // Templates d'ordonnances
        Map<String, String> prescriptionTemplates = new HashMap<>();
        prescriptionTemplates.put("default", "Ordonnance standard");
        prescriptionTemplates.put("detailed", "Ordonnance détaillée");
        templates.put("prescriptions", prescriptionTemplates);
        
        // Templates de certificats
        Map<String, String> certificateTemplates = new HashMap<>();
        certificateTemplates.put("default", "Certificat médical standard");
        certificateTemplates.put("work", "Certificat d'arrêt de travail");
        certificateTemplates.put("sport", "Certificat médical sport");
        templates.put("certificates", certificateTemplates);
        
        return ResponseEntity.ok(templates);
    }

    @GetMapping("/status")
    @Operation(summary = "Vérifier le statut du service d'impression")
    public ResponseEntity<Map<String, Object>> getPrintServiceStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("serviceStatus", "active");
        status.put("supportedFormats", new String[]{"HTML", "CSS"});
        status.put("availableDocuments", new String[]{"patient-card", "prescription", "medical-certificate"});
        
        // Statistiques factices
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalPrintRequests", 0);
        stats.put("patientCardsGenerated", 0);
        stats.put("prescriptionsGenerated", 0);
        stats.put("certificatesGenerated", 0);
        status.put("statistics", stats);
        
        return ResponseEntity.ok(status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Erreur interne: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}