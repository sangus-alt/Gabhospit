package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "surgeries")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Surgery extends BaseEntity {

    @Column(name = "surgery_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le numéro de chirurgie ne peut pas être vide")
    private String surgeryNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surgeon_id", nullable = false)
    @NotNull(message = "Le chirurgien est obligatoire")
    private Doctor surgeon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anesthesiologist_id")
    private Doctor anesthesiologist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admission_id")
    private Admission admission;

    @Column(name = "surgery_name", nullable = false, length = 200)
    @NotBlank(message = "Le nom de la chirurgie ne peut pas être vide")
    private String surgeryName;

    @Column(name = "surgery_code", length = 50)
    private String surgeryCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "surgery_type", nullable = false)
    @NotNull(message = "Le type de chirurgie est obligatoire")
    private SurgeryType surgeryType;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency_level", nullable = false)
    @NotNull(message = "Le niveau d'urgence est obligatoire")
    private UrgencyLevel urgencyLevel;

    @Column(name = "scheduled_date", nullable = false)
    @NotNull(message = "La date prévue est obligatoire")
    private LocalDateTime scheduledDate;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @Column(name = "duration_minutes")
    @Min(value = 1, message = "La durée doit être d'au moins 1 minute")
    private Integer durationMinutes;

    @Column(name = "estimated_duration_minutes")
    @Min(value = 1, message = "La durée estimée doit être d'au moins 1 minute")
    private Integer estimatedDurationMinutes;

    @Column(name = "operating_room", length = 50)
    private String operatingRoom;

    @Column(name = "pre_operative_diagnosis", length = 1000)
    private String preOperativeDiagnosis;

    @Column(name = "post_operative_diagnosis", length = 1000)
    private String postOperativeDiagnosis;

    @Column(name = "procedure_description", length = 2000)
    private String procedureDescription;

    @Column(name = "surgical_approach", length = 500)
    private String surgicalApproach;

    @Column(name = "anesthesia_type", length = 100)
    private String anesthesiaType;

    @Column(name = "complications", length = 1000)
    private String complications;

    @Column(name = "blood_loss_ml")
    @Min(value = 0, message = "La perte de sang ne peut pas être négative")
    private Integer bloodLossMl;

    @Column(name = "transfusion_required")
    private Boolean transfusionRequired = false;

    @Column(name = "units_transfused")
    @Min(value = 0, message = "Le nombre d'unités transfusées ne peut pas être négatif")
    private Integer unitsTransfused = 0;

    @Column(name = "surgical_team", length = 1000)
    private String surgicalTeam;

    @Column(name = "instruments_used", length = 1000)
    private String instrumentsUsed;

    @Column(name = "implants_used", length = 1000)
    private String implantsUsed;

    @Column(name = "specimens_sent", length = 1000)
    private String specimensSent;

    @Column(name = "post_operative_instructions", length = 2000)
    private String postOperativeInstructions;

    @Column(name = "recovery_notes", length = 2000)
    private String recoveryNotes;

    @Column(name = "discharge_instructions", length = 2000)
    private String dischargeInstructions;

    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SurgeryStatus status = SurgeryStatus.SCHEDULED;

    @Column(name = "cost")
    @DecimalMin(value = "0.0", message = "Le coût ne peut pas être négatif")
    private Double cost;

    @Column(name = "currency", length = 10)
    private String currency = "EUR";

    @Column(name = "insurance_coverage")
    @DecimalMin(value = "0.0", message = "La couverture d'assurance ne peut pas être négative")
    private Double insuranceCoverage = 0.0;

    @Column(name = "patient_responsibility")
    @DecimalMin(value = "0.0", message = "La responsabilité du patient ne peut pas être négative")
    private Double patientResponsibility;

    @Column(name = "consent_obtained")
    private Boolean consentObtained = false;

    @Column(name = "consent_date")
    private LocalDateTime consentDate;

    @Column(name = "consent_witness", length = 100)
    private String consentWitness;

    @Column(name = "pre_operative_assessment", length = 1000)
    private String preOperativeAssessment;

    @Column(name = "post_operative_assessment", length = 1000)
    private String postOperativeAssessment;

    @Column(name = "pathology_report", length = 2000)
    private String pathologyReport;

    @Column(name = "operative_report", length = 3000)
    private String operativeReport;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "is_emergency")
    private Boolean isEmergency = false;

    @Column(name = "cancelled_reason", length = 500)
    private String cancelledReason;

    @Column(name = "cancelled_by", length = 100)
    private String cancelledBy;

    @Column(name = "cancelled_date")
    private LocalDateTime cancelledDate;

    @PrePersist
    public void prePersist() {
        if (estimatedDurationMinutes == null) {
            estimatedDurationMinutes = 120; // 2 heures par défaut
        }
    }

    public enum SurgeryType {
        CARDIAC("Cardiaque"),
        ORTHOPEDIC("Orthopédique"),
        NEUROLOGICAL("Neurologique"),
        GENERAL("Générale"),
        PLASTIC("Plastique"),
        VASCULAR("Vasculaire"),
        THORACIC("Thoracique"),
        UROLOGICAL("Urologique"),
        GYNECOLOGICAL("Gynécologique"),
        OPHTHALMIC("Ophtalmologique"),
        ENT("ORL"),
        PEDIATRIC("Pédiatrique"),
        EMERGENCY("Urgence"),
        TRANSPLANT("Transplantation"),
        ONCOLOGICAL("Oncologique"),
        BARIATRIC("Bariatrique"),
        DENTAL("Dentaire"),
        DERMATOLOGICAL("Dermatologique"),
        ENDOCRINE("Endocrinienne"),
        GASTROINTESTINAL("Gastro-intestinale"),
        OTHER("Autre");

        private final String displayName;

        SurgeryType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum UrgencyLevel {
        ELECTIVE("Programmée"),
        URGENT("Urgente"),
        EMERGENCY("Urgence"),
        IMMEDIATE("Immédiate");

        private final String displayName;

        UrgencyLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum SurgeryStatus {
        SCHEDULED("Planifiée"),
        CONFIRMED("Confirmée"),
        IN_PROGRESS("En cours"),
        COMPLETED("Terminée"),
        CANCELLED("Annulée"),
        POSTPONED("Reportée"),
        RECOVERED("Récupération"),
        DISCHARGED("Sortie");

        private final String displayName;

        SurgeryStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}