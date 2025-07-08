package com.hospital.controller;

import com.hospital.entity.HospitalConfiguration;
import com.hospital.service.HospitalConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/configuration")
@RequiredArgsConstructor
@Tag(name = "Configuration", description = "APIs pour la gestion de la configuration de l'hôpital")
public class HospitalConfigurationController {

    private final HospitalConfigurationService configurationService;

    @GetMapping
    @Operation(summary = "Récupérer la configuration active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('NURSE')")
    public ResponseEntity<HospitalConfiguration> getActiveConfiguration() {
        return configurationService.getActiveConfiguration()
                .map(config -> ResponseEntity.ok(config))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer ou mettre à jour la configuration")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HospitalConfiguration> saveConfiguration(
            @Valid @RequestBody HospitalConfiguration configuration) {
        HospitalConfiguration savedConfig = configurationService.saveConfiguration(configuration);
        return ResponseEntity.ok(savedConfig);
    }

    @PostMapping(value = "/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload du logo de l'hôpital")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> uploadLogo(
            @Parameter(description = "Fichier image du logo (max 5MB)")
            @RequestParam("logo") MultipartFile logoFile) {
        try {
            configurationService.uploadLogo(logoFile);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Logo uploadé avec succès");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping(value = "/signature", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de la signature")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> uploadSignature(
            @Parameter(description = "Fichier image de la signature")
            @RequestParam("signature") MultipartFile signatureFile) {
        try {
            configurationService.uploadSignature(signatureFile);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Signature uploadée avec succès");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/hospital-name")
    @Operation(summary = "Récupérer le nom de l'hôpital")
    public ResponseEntity<Map<String, String>> getHospitalName() {
        Map<String, String> response = new HashMap<>();
        response.put("hospitalName", configurationService.getHospitalName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logo")
    @Operation(summary = "Récupérer le logo de l'hôpital")
    public ResponseEntity<Map<String, String>> getHospitalLogo() {
        String logo = configurationService.getHospitalLogo();
        Map<String, String> response = new HashMap<>();
        response.put("logo", logo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/signature")
    @Operation(summary = "Récupérer la signature")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<Map<String, String>> getSignature() {
        String signature = configurationService.getSignature();
        Map<String, String> response = new HashMap<>();
        response.put("signature", signature);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/prefixes")
    @Operation(summary = "Récupérer tous les préfixes de numérotation")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('NURSE')")
    public ResponseEntity<Map<String, String>> getAllPrefixes() {
        Map<String, String> prefixes = new HashMap<>();
        prefixes.put("patientPrefix", configurationService.getPatientNumberPrefix());
        prefixes.put("doctorPrefix", configurationService.getDoctorNumberPrefix());
        prefixes.put("billPrefix", configurationService.getBillNumberPrefix());
        return ResponseEntity.ok(prefixes);
    }

    @GetMapping("/financial")
    @Operation(summary = "Récupérer les paramètres financiers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BILLING')")
    public ResponseEntity<Map<String, Object>> getFinancialSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("taxRate", configurationService.getTaxRate());
        settings.put("currency", configurationService.getCurrency());
        return ResponseEntity.ok(settings);
    }

    @GetMapping("/localization")
    @Operation(summary = "Récupérer les paramètres de localisation")
    public ResponseEntity<Map<String, String>> getLocalizationSettings() {
        Map<String, String> settings = new HashMap<>();
        settings.put("language", configurationService.getLanguage());
        settings.put("timezone", configurationService.getTimezone());
        return ResponseEntity.ok(settings);
    }

    @GetMapping("/texts")
    @Operation(summary = "Récupérer les textes d'en-tête et pied de page")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<Map<String, String>> getTexts() {
        Map<String, String> texts = new HashMap<>();
        texts.put("headerText", configurationService.getHeaderText());
        texts.put("footerText", configurationService.getFooterText());
        return ResponseEntity.ok(texts);
    }

    @PostMapping("/backup")
    @Operation(summary = "Effectuer une sauvegarde manuelle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> performBackup() {
        try {
            configurationService.performBackup();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Sauvegarde effectuée avec succès");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/maintenance")
    @Operation(summary = "Activer/Désactiver le mode maintenance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> setMaintenanceMode(
            @Parameter(description = "Activer ou désactiver le mode maintenance")
            @RequestParam boolean enabled,
            @Parameter(description = "Message à afficher en mode maintenance")
            @RequestParam(required = false, defaultValue = "Système en maintenance") String message) {
        try {
            configurationService.setMaintenanceMode(enabled, message);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Mode maintenance " + (enabled ? "activé" : "désactivé"));
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/reset")
    @Operation(summary = "Réinitialiser la configuration aux valeurs par défaut")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HospitalConfiguration> resetToDefault() {
        try {
            HospitalConfiguration defaultConfig = configurationService.resetToDefault();
            return ResponseEntity.ok(defaultConfig);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/summary")
    @Operation(summary = "Récupérer un résumé de la configuration")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getConfigurationSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // Informations de base
        summary.put("hospitalName", configurationService.getHospitalName());
        summary.put("currency", configurationService.getCurrency());
        summary.put("language", configurationService.getLanguage());
        summary.put("timezone", configurationService.getTimezone());
        
        // Préfixes
        Map<String, String> prefixes = new HashMap<>();
        prefixes.put("patient", configurationService.getPatientNumberPrefix());
        prefixes.put("doctor", configurationService.getDoctorNumberPrefix());
        prefixes.put("bill", configurationService.getBillNumberPrefix());
        summary.put("prefixes", prefixes);
        
        // Paramètres financiers
        summary.put("taxRate", configurationService.getTaxRate());
        
        // Statut des uploads
        Map<String, Boolean> uploads = new HashMap<>();
        uploads.put("hasLogo", configurationService.getHospitalLogo() != null);
        uploads.put("hasSignature", configurationService.getSignature() != null);
        summary.put("uploads", uploads);
        
        return ResponseEntity.ok(summary);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        response.put("status", "error");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Erreur interne: " + e.getMessage());
        response.put("status", "error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}