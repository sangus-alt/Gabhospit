package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultations")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Consultation extends BaseEntity {

    @Column(name = "consultation_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le numéro de consultation ne peut pas être vide")
    private String consultationNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    @NotNull(message = "Le rendez-vous est obligatoire")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Le médecin est obligatoire")
    private Doctor doctor;

    @Column(name = "consultation_date", nullable = false)
    @NotNull(message = "La date de consultation est obligatoire")
    private LocalDateTime consultationDate;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_minutes")
    @Min(value = 1, message = "La durée doit être d'au moins 1 minute")
    private Integer durationMinutes;

    @Column(name = "chief_complaint", length = 1000)
    private String chiefComplaint;

    @Column(name = "history_of_present_illness", length = 2000)
    private String historyOfPresentIllness;

    @Column(name = "vital_signs", length = 500)
    private String vitalSigns;

    @Column(name = "blood_pressure", length = 20)
    private String bloodPressure;

    @Column(name = "heart_rate", length = 20)
    private String heartRate;

    @Column(name = "temperature", length = 20)
    private String temperature;

    @Column(name = "respiratory_rate", length = 20)
    private String respiratoryRate;

    @Column(name = "oxygen_saturation", length = 20)
    private String oxygenSaturation;

    @Column(name = "weight", length = 20)
    private String weight;

    @Column(name = "height", length = 20)
    private String height;

    @Column(name = "bmi", length = 20)
    private String bmi;

    @Column(name = "physical_examination", length = 2000)
    private String physicalExamination;

    @Column(name = "assessment", length = 2000)
    private String assessment;

    @Column(name = "diagnosis", length = 1000)
    private String diagnosis;

    @Column(name = "icd_codes", length = 500)
    private String icdCodes;

    @Column(name = "differential_diagnosis", length = 1000)
    private String differentialDiagnosis;

    @Column(name = "treatment_plan", length = 2000)
    private String treatmentPlan;

    @Column(name = "medications_prescribed", length = 1000)
    private String medicationsPrescribed;

    @Column(name = "tests_ordered", length = 1000)
    private String testsOrdered;

    @Column(name = "referrals", length = 1000)
    private String referrals;

    @Column(name = "follow_up_instructions", length = 1000)
    private String followUpInstructions;

    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    @Column(name = "patient_education", length = 1000)
    private String patientEducation;

    @Column(name = "special_instructions", length = 1000)
    private String specialInstructions;

    @Column(name = "consultation_fee")
    @DecimalMin(value = "0.0", message = "Les honoraires ne peuvent pas être négatifs")
    private Double consultationFee;

    @Column(name = "room_number", length = 20)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "consultation_type", nullable = false)
    @NotNull(message = "Le type de consultation est obligatoire")
    private ConsultationType consultationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConsultationStatus status = ConsultationStatus.IN_PROGRESS;

    @Column(name = "is_emergency")
    private Boolean isEmergency = false;

    @Column(name = "is_follow_up")
    private Boolean isFollowUp = false;

    @Column(name = "previous_consultation_id")
    private Long previousConsultationId;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "clinical_notes", length = 2000)
    private String clinicalNotes;

    @Column(name = "is_signed")
    private Boolean isSigned = false;

    @Column(name = "signed_by", length = 100)
    private String signedBy;

    @Column(name = "signed_date")
    private LocalDateTime signedDate;

    // Nouveaux champs pour corriger les erreurs de compilation
    @Column(name = "present_illness", length = 2000)
    private String presentIllness;

    @Column(name = "is_urgent")
    private Boolean isUrgent = false;

    @Column(name = "requires_follow_up")
    private Boolean requiresFollowUp = false;

    @PrePersist
    public void prePersist() {
        if (consultationDate == null) {
            consultationDate = LocalDateTime.now();
        }
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        if (isUrgent == null) {
            isUrgent = false;
        }
        if (requiresFollowUp == null) {
            requiresFollowUp = false;
        }
        if (presentIllness == null) {
            presentIllness = historyOfPresentIllness;
        }
    }

    public enum ConsultationType {
        INITIAL("Consultation initiale"),
        FOLLOW_UP("Suivi"),
        ROUTINE("Consultation de routine"),
        URGENT("Consultation urgente"),
        EMERGENCY("Consultation d'urgence"),
        SPECIALIST("Consultation spécialisée"),
        SECOND_OPINION("Deuxième avis"),
        TELEMEDICINE("Télémédecine"),
        PRE_OPERATIVE("Pré-opératoire"),
        POST_OPERATIVE("Post-opératoire");

        private final String displayName;

        ConsultationType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ConsultationStatus {
        SCHEDULED("Planifié"),
        IN_PROGRESS("En cours"),
        COMPLETED("Terminé"),
        CANCELLED("Annulé"),
        NO_SHOW("Absent");

        private final String displayName;

        ConsultationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}