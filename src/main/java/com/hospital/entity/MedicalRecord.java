package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "medical_records")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MedicalRecord extends BaseEntity {

    @Column(name = "record_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le numéro du dossier médical ne peut pas être vide")
    private String recordNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Le médecin est obligatoire")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "record_date", nullable = false)
    @NotNull(message = "La date du dossier est obligatoire")
    private LocalDateTime recordDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false)
    @NotNull(message = "Le type de dossier est obligatoire")
    private RecordType recordType;

    @Column(name = "chief_complaint", length = 1000)
    private String chiefComplaint;

    @Column(name = "history_of_present_illness", length = 2000)
    private String historyOfPresentIllness;

    @Column(name = "past_medical_history", length = 2000)
    private String pastMedicalHistory;

    @Column(name = "family_history", length = 1000)
    private String familyHistory;

    @Column(name = "social_history", length = 1000)
    private String socialHistory;

    @Column(name = "allergies", length = 1000)
    private String allergies;

    @Column(name = "current_medications", length = 1000)
    private String currentMedications;

    @Column(name = "physical_examination", length = 2000)
    private String physicalExamination;

    @Column(name = "vital_signs", length = 500)
    private String vitalSigns;

    @Column(name = "assessment", length = 2000)
    private String assessment;

    @Column(name = "plan", length = 2000)
    private String plan;

    @Column(name = "diagnosis", length = 1000)
    private String diagnosis;

    @Column(name = "icd_codes", length = 500)
    private String icdCodes;

    @Column(name = "differential_diagnosis", length = 1000)
    private String differentialDiagnosis;

    @Column(name = "treatment_provided", length = 2000)
    private String treatmentProvided;

    @Column(name = "follow_up_instructions", length = 1000)
    private String followUpInstructions;

    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    @Column(name = "prognosis", length = 500)
    private String prognosis;

    @Column(name = "laboratory_results", length = 1000)
    private String laboratoryResults;

    @Column(name = "imaging_results", length = 1000)
    private String imagingResults;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "is_confidential")
    private Boolean isConfidential = false;

    @Column(name = "access_level")
    @Min(value = 1, message = "Le niveau d'accès doit être d'au moins 1")
    @Max(value = 5, message = "Le niveau d'accès ne peut pas dépasser 5")
    private Integer accessLevel = 1;

    @Column(name = "signed_by", length = 100)
    private String signedBy;

    @Column(name = "signed_date")
    private LocalDateTime signedDate;

    @Column(name = "is_signed")
    private Boolean isSigned = false;

    @Column(name = "template_used", length = 100)
    private String templateUsed;

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Prescription> prescriptions;

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LabResult> labResults;

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ImagingResult> imagingResults;

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MedicalAttachment> attachments;

    @PrePersist
    public void prePersist() {
        if (recordDate == null) {
            recordDate = LocalDateTime.now();
        }
    }

    public enum RecordType {
        CONSULTATION("Consultation"),
        ADMISSION("Admission"),
        EMERGENCY("Urgence"),
        SURGERY("Chirurgie"),
        FOLLOW_UP("Suivi"),
        DISCHARGE("Sortie"),
        TRANSFER("Transfert"),
        PROCEDURE("Procédure"),
        DIAGNOSTIC("Diagnostic"),
        TREATMENT("Traitement");

        private final String displayName;

        RecordType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}