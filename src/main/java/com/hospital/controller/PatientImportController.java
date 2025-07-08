package com.hospital.controller;

import com.hospital.service.PatientImportService;
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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/patients/import")
@RequiredArgsConstructor
@Tag(name = "Import Patients", description = "APIs pour l'import en masse des patients")
public class PatientImportController {

    private final PatientImportService importService;

    @PostMapping(value = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importer des patients depuis un fichier CSV")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NURSE')")
    public ResponseEntity<Map<String, Object>> importPatientsFromCsv(
            @Parameter(description = "Fichier CSV contenant les données des patients (max 10MB)")
            @RequestParam("file") MultipartFile csvFile) {
        
        try {
            PatientImportService.ImportResult result = importService.importPatientsFromCsv(csvFile);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "completed");
            response.put("totalProcessed", result.getTotalCount());
            response.put("successCount", result.getSuccessCount());
            response.put("errorCount", result.getErrorCount());
            response.put("skippedCount", result.getSkippedCount());
            
            // Détails des succès
            if (!result.getSuccessRecords().isEmpty()) {
                response.put("successDetails", result.getSuccessRecords().stream()
                    .map(record -> Map.of(
                        "line", record.getLineNumber(),
                        "patientNumber", record.getPatient().getPatientNumber(),
                        "name", record.getPatient().getFirstName() + " " + record.getPatient().getLastName()
                    ))
                    .toList());
            }
            
            // Détails des erreurs
            if (!result.getErrorRecords().isEmpty()) {
                response.put("errorDetails", result.getErrorRecords().stream()
                    .map(record -> Map.of(
                        "line", record.getLineNumber(),
                        "error", record.getError()
                    ))
                    .toList());
            }
            
            // Détails des éléments ignorés
            if (!result.getSkippedRecords().isEmpty()) {
                response.put("skippedDetails", result.getSkippedRecords().stream()
                    .map(record -> Map.of(
                        "line", record.getLineNumber(),
                        "reason", record.getReason()
                    ))
                    .toList());
            }
            
            // Message de résumé
            StringBuilder message = new StringBuilder();
            message.append("Import terminé: ");
            message.append(result.getSuccessCount()).append(" succès");
            if (result.getErrorCount() > 0) {
                message.append(", ").append(result.getErrorCount()).append(" erreurs");
            }
            if (result.getSkippedCount() > 0) {
                message.append(", ").append(result.getSkippedCount()).append(" ignorés");
            }
            
            response.put("message", message.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de l'import: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/template")
    @Operation(summary = "Télécharger le template CSV pour l'import des patients")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NURSE')")
    public ResponseEntity<String> downloadCsvTemplate() {
        try {
            String template = importService.generateCsvTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template_import_patients.csv");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(template);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/instructions")
    @Operation(summary = "Récupérer les instructions pour l'import CSV")
    public ResponseEntity<Map<String, Object>> getImportInstructions() {
        Map<String, Object> instructions = new HashMap<>();
        
        // Format de fichier
        Map<String, String> fileFormat = new HashMap<>();
        fileFormat.put("type", "CSV");
        fileFormat.put("encoding", "UTF-8");
        fileFormat.put("separator", ",");
        fileFormat.put("maxSize", "10MB");
        instructions.put("fileFormat", fileFormat);
        
        // Champs obligatoires
        instructions.put("requiredFields", new String[]{
            "prenom", "nom", "date_naissance", "sexe"
        });
        
        // Champs optionnels
        instructions.put("optionalFields", new String[]{
            "telephone", "email", "adresse", "ville", "code_postal", "pays",
            "carte_identite", "securite_sociale", "profession", "situation_familiale",
            "langue", "allergies", "antecedents", "medicaments", "assurance_numero",
            "assurance_nom", "contact_urgence_nom", "contact_urgence_tel",
            "contact_urgence_relation", "groupe_sanguin"
        });
        
        // Formats de dates acceptés
        instructions.put("dateFormats", new String[]{
            "dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd", "dd/MM/yy", "dd-MM-yy"
        });
        
        // Valeurs acceptées pour le sexe
        instructions.put("genderValues", new String[]{
            "M", "F", "H", "HOMME", "FEMME", "MALE", "FEMALE", "AUTRE", "OTHER"
        });
        
        // Groupes sanguins acceptés
        instructions.put("bloodTypes", new String[]{
            "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        });
        
        // Règles de validation
        Map<String, String> validationRules = new HashMap<>();
        validationRules.put("duplicates", "Les patients existants (même nom, prénom et date de naissance) seront ignorés");
        validationRules.put("phone", "Format: numéros uniquement, 10-15 chiffres, + optionnel au début");
        validationRules.put("email", "Format email standard requis");
        validationRules.put("generation", "Le numéro patient sera généré automatiquement");
        instructions.put("validationRules", validationRules);
        
        // Conseils
        instructions.put("tips", new String[]{
            "Utilisez le template CSV fourni pour éviter les erreurs de format",
            "Vérifiez que les dates sont au bon format avant l'import",
            "Les en-têtes peuvent être en français ou en anglais",
            "Les champs vides sont autorisés sauf pour les champs obligatoires",
            "L'import s'arrête en cas d'erreur critique mais traite les lignes valides"
        });
        
        return ResponseEntity.ok(instructions);
    }

    @GetMapping("/status")
    @Operation(summary = "Vérifier le statut du service d'import")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NURSE')")
    public ResponseEntity<Map<String, Object>> getImportStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("serviceStatus", "active");
        status.put("maxFileSize", "10MB");
        status.put("supportedFormats", new String[]{"CSV"});
        status.put("encoding", "UTF-8");
        
        // Statistiques factices (dans une vraie implémentation, vous pourriez stocker ces données)
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalImports", 0);
        stats.put("totalPatientsImported", 0);
        stats.put("lastImportSuccess", 1);
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