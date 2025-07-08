package com.hospital.service;

import com.hospital.entity.Patient;
import com.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientImportService {

    private final PatientRepository patientRepository;
    private final PatientService patientService;

    /**
     * Import des patients depuis un fichier CSV
     */
    public ImportResult importPatientsFromCsv(MultipartFile csvFile) {
        ImportResult result = new ImportResult();
        
        try {
            validateCsvFile(csvFile);
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(csvFile.getInputStream(), StandardCharsets.UTF_8)
            );
            
            CSVFormat csvFormat = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim();
                
            CSVParser parser = new CSVParser(reader, csvFormat);
            
            Map<String, Integer> headerMap = parser.getHeaderMap();
            validateHeaders(headerMap);
            
            int lineNumber = 1; // Start at 1 because header is line 0
            
            for (CSVRecord record : parser) {
                lineNumber++;
                try {
                    Patient patient = parsePatientFromRecord(record);
                    
                    // Vérifier si le patient existe déjà
                    if (patientExists(patient)) {
                        result.addSkipped(lineNumber, "Patient déjà existant: " + patient.getFirstName() + " " + patient.getLastName());
                        continue;
                    }
                    
                    // Générer un numéro patient unique
                    patient.setPatientNumber(patientService.generateUniquePatientNumber());
                    patient.setRegistrationDate(LocalDateTime.now());
                    
                    Patient savedPatient = patientRepository.save(patient);
                    result.addSuccess(lineNumber, savedPatient);
                    
                } catch (Exception e) {
                    result.addError(lineNumber, "Erreur lors du traitement: " + e.getMessage());
                    log.error("Erreur ligne {}: {}", lineNumber, e.getMessage());
                }
            }
            
            parser.close();
            reader.close();
            
        } catch (IOException e) {
            log.error("Erreur lors de la lecture du fichier CSV: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la lecture du fichier CSV: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erreur lors de l'import: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'import: " + e.getMessage());
        }
        
        log.info("Import terminé - Succès: {}, Erreurs: {}, Ignorés: {}", 
            result.getSuccessCount(), result.getErrorCount(), result.getSkippedCount());
            
        return result;
    }

    /**
     * Valide le fichier CSV
     */
    private void validateCsvFile(MultipartFile csvFile) {
        if (csvFile == null || csvFile.isEmpty()) {
            throw new IllegalArgumentException("Le fichier CSV ne peut pas être vide");
        }
        
        String originalFilename = csvFile.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("Le fichier doit être au format CSV");
        }
        
        // Limite de taille: 10MB
        if (csvFile.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Le fichier ne peut pas dépasser 10MB");
        }
    }

    /**
     * Valide les en-têtes du CSV
     */
    private void validateHeaders(Map<String, Integer> headerMap) {
        List<String> requiredHeaders = List.of("prenom", "nom", "date_naissance", "sexe");
        List<String> missingHeaders = new ArrayList<>();
        
        for (String required : requiredHeaders) {
            if (!headerMap.containsKey(required.toLowerCase()) && 
                !headerMap.containsKey(required) && 
                !headerMap.containsKey(required.toUpperCase())) {
                missingHeaders.add(required);
            }
        }
        
        if (!missingHeaders.isEmpty()) {
            throw new IllegalArgumentException("En-têtes manquants dans le CSV: " + missingHeaders);
        }
    }

    /**
     * Parse un patient depuis un enregistrement CSV
     */
    private Patient parsePatientFromRecord(CSVRecord record) {
        Patient patient = new Patient();
        
        try {
            // Champs obligatoires
            patient.setFirstName(getRecordValue(record, "prenom", "firstName", "first_name"));
            patient.setLastName(getRecordValue(record, "nom", "lastName", "last_name"));
            
            // Date de naissance
            String birthDateStr = getRecordValue(record, "date_naissance", "dateOfBirth", "date_of_birth", "birthDate");
            patient.setDateOfBirth(parseDate(birthDateStr));
            
            // Sexe
            String genderStr = getRecordValue(record, "sexe", "gender", "genre");
            patient.setGender(parseGender(genderStr));
            
            // Champs optionnels
            patient.setPhone(getRecordValue(record, "telephone", "phone", "tel"));
            patient.setEmail(getRecordValue(record, "email", "courriel", "mail"));
            patient.setAddress(getRecordValue(record, "adresse", "address"));
            patient.setCity(getRecordValue(record, "ville", "city"));
            patient.setPostalCode(getRecordValue(record, "code_postal", "postalCode", "postal_code", "cp"));
            patient.setCountry(getRecordValue(record, "pays", "country"));
            patient.setNationalId(getRecordValue(record, "carte_identite", "nationalId", "national_id", "cin"));
            patient.setSocialSecurityNumber(getRecordValue(record, "securite_sociale", "socialSecurityNumber", "social_security_number", "ss"));
            patient.setProfession(getRecordValue(record, "profession", "metier", "job"));
            patient.setOccupation(getRecordValue(record, "occupation", "activite"));
            patient.setMaritalStatus(getRecordValue(record, "situation_familiale", "maritalStatus", "marital_status"));
            patient.setPreferredLanguage(getRecordValue(record, "langue", "language", "preferred_language"));
            patient.setAllergies(getRecordValue(record, "allergies", "allergie"));
            patient.setMedicalHistory(getRecordValue(record, "antecedents", "medicalHistory", "medical_history"));
            patient.setCurrentMedications(getRecordValue(record, "medicaments", "medications", "current_medications"));
            patient.setInsuranceNumber(getRecordValue(record, "assurance_numero", "insuranceNumber", "insurance_number"));
            patient.setInsuranceProvider(getRecordValue(record, "assurance_nom", "insuranceProvider", "insurance_provider"));
            
            // Contact d'urgence
            patient.setEmergencyContactName(getRecordValue(record, "contact_urgence_nom", "emergencyContactName", "emergency_contact_name"));
            patient.setEmergencyContactPhone(getRecordValue(record, "contact_urgence_tel", "emergencyContactPhone", "emergency_contact_phone"));
            patient.setEmergencyContactRelation(getRecordValue(record, "contact_urgence_relation", "emergencyContactRelation", "emergency_contact_relation"));
            
            // Groupe sanguin
            String bloodTypeStr = getRecordValue(record, "groupe_sanguin", "bloodType", "blood_type");
            if (bloodTypeStr != null && !bloodTypeStr.trim().isEmpty()) {
                patient.setBloodType(parseBloodType(bloodTypeStr));
            }
            
            return patient;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du parsing de l'enregistrement: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère la valeur d'un champ en essayant plusieurs noms possibles
     */
    private String getRecordValue(CSVRecord record, String... possibleNames) {
        for (String name : possibleNames) {
            try {
                if (record.isMapped(name)) {
                    String value = record.get(name);
                    if (value != null && !value.trim().isEmpty()) {
                        return value.trim();
                    }
                }
            } catch (IllegalArgumentException e) {
                // Continue avec le nom suivant
            }
        }
        return null;
    }

    /**
     * Parse une date depuis une chaîne
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("La date de naissance est obligatoire");
        }
        
        List<DateTimeFormatter> formatters = List.of(
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yy"),
            DateTimeFormatter.ofPattern("dd-MM-yy")
        );
        
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateStr.trim(), formatter);
            } catch (DateTimeParseException e) {
                // Continue avec le format suivant
            }
        }
        
        throw new IllegalArgumentException("Format de date invalide: " + dateStr + 
            ". Formats acceptés: dd/MM/yyyy, dd-MM-yyyy, yyyy-MM-dd");
    }

    /**
     * Parse le sexe depuis une chaîne
     */
    private Patient.Gender parseGender(String genderStr) {
        if (genderStr == null || genderStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Le sexe est obligatoire");
        }
        
        String gender = genderStr.trim().toUpperCase();
        
        switch (gender) {
            case "M":
            case "MALE":
            case "HOMME":
            case "H":
                return Patient.Gender.MALE;
            case "F":
            case "FEMALE":
            case "FEMME":
                return Patient.Gender.FEMALE;
            case "AUTRE":
            case "OTHER":
            case "A":
                return Patient.Gender.OTHER;
            default:
                throw new IllegalArgumentException("Sexe invalide: " + genderStr + 
                    ". Valeurs acceptées: M, F, H, HOMME, FEMME, MALE, FEMALE, AUTRE, OTHER");
        }
    }

    /**
     * Parse le groupe sanguin depuis une chaîne
     */
    private Patient.BloodType parseBloodType(String bloodTypeStr) {
        if (bloodTypeStr == null || bloodTypeStr.trim().isEmpty()) {
            return null;
        }
        
        String bloodType = bloodTypeStr.trim().toUpperCase().replace(" ", "");
        
        try {
            switch (bloodType) {
                case "A+":
                case "APLUS":
                    return Patient.BloodType.A_POSITIVE;
                case "A-":
                case "AMINUS":
                    return Patient.BloodType.A_NEGATIVE;
                case "B+":
                case "BPLUS":
                    return Patient.BloodType.B_POSITIVE;
                case "B-":
                case "BMINUS":
                    return Patient.BloodType.B_NEGATIVE;
                case "AB+":
                case "ABPLUS":
                    return Patient.BloodType.AB_POSITIVE;
                case "AB-":
                case "ABMINUS":
                    return Patient.BloodType.AB_NEGATIVE;
                case "O+":
                case "OPLUS":
                    return Patient.BloodType.O_POSITIVE;
                case "O-":
                case "OMINUS":
                    return Patient.BloodType.O_NEGATIVE;
                default:
                    log.warn("Groupe sanguin non reconnu: {}", bloodTypeStr);
                    return null;
            }
        } catch (Exception e) {
            log.warn("Erreur lors du parsing du groupe sanguin: {}", bloodTypeStr);
            return null;
        }
    }

    /**
     * Vérifie si un patient existe déjà
     */
    private boolean patientExists(Patient patient) {
        return patientRepository.existsByFirstNameAndLastNameAndDateOfBirth(
            patient.getFirstName(), 
            patient.getLastName(), 
            patient.getDateOfBirth()
        );
    }

    /**
     * Génère un template CSV pour l'import
     */
    public String generateCsvTemplate() {
        StringBuilder template = new StringBuilder();
        template.append("prenom,nom,date_naissance,sexe,telephone,email,adresse,ville,code_postal,pays,")
               .append("carte_identite,securite_sociale,profession,situation_familiale,langue,allergies,")
               .append("antecedents,medicaments,assurance_numero,assurance_nom,contact_urgence_nom,")
               .append("contact_urgence_tel,contact_urgence_relation,groupe_sanguin\n");
        
        // Ligne d'exemple
        template.append("Jean,Dupont,15/03/1985,M,0123456789,jean.dupont@email.com,123 Rue de la Paix,")
               .append("Paris,75001,France,1234567890123,1850312345678,Ingénieur,Marié,FR,Aucune,")
               .append("Hypertension,Aspirine,123456789,Sécurité Sociale,Marie Dupont,")
               .append("0987654321,Épouse,A+\n");
        
        return template.toString();
    }

    /**
     * Classe pour le résultat de l'import
     */
    public static class ImportResult {
        private final List<SuccessRecord> successRecords = new ArrayList<>();
        private final List<ErrorRecord> errorRecords = new ArrayList<>();
        private final List<SkippedRecord> skippedRecords = new ArrayList<>();

        public void addSuccess(int lineNumber, Patient patient) {
            successRecords.add(new SuccessRecord(lineNumber, patient));
        }

        public void addError(int lineNumber, String error) {
            errorRecords.add(new ErrorRecord(lineNumber, error));
        }

        public void addSkipped(int lineNumber, String reason) {
            skippedRecords.add(new SkippedRecord(lineNumber, reason));
        }

        public int getSuccessCount() { return successRecords.size(); }
        public int getErrorCount() { return errorRecords.size(); }
        public int getSkippedCount() { return skippedRecords.size(); }
        public int getTotalCount() { return getSuccessCount() + getErrorCount() + getSkippedCount(); }

        public List<SuccessRecord> getSuccessRecords() { return successRecords; }
        public List<ErrorRecord> getErrorRecords() { return errorRecords; }
        public List<SkippedRecord> getSkippedRecords() { return skippedRecords; }

        public static class SuccessRecord {
            private final int lineNumber;
            private final Patient patient;

            public SuccessRecord(int lineNumber, Patient patient) {
                this.lineNumber = lineNumber;
                this.patient = patient;
            }

            public int getLineNumber() { return lineNumber; }
            public Patient getPatient() { return patient; }
        }

        public static class ErrorRecord {
            private final int lineNumber;
            private final String error;

            public ErrorRecord(int lineNumber, String error) {
                this.lineNumber = lineNumber;
                this.error = error;
            }

            public int getLineNumber() { return lineNumber; }
            public String getError() { return error; }
        }

        public static class SkippedRecord {
            private final int lineNumber;
            private final String reason;

            public SkippedRecord(int lineNumber, String reason) {
                this.lineNumber = lineNumber;
                this.reason = reason;
            }

            public int getLineNumber() { return lineNumber; }
            public String getReason() { return reason; }
        }
    }
}