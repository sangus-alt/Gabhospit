package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admissions")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Admission extends BaseEntity {

    @Column(name = "admission_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le numéro d'admission ne peut pas être vide")
    private String admissionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Le médecin est obligatoire")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    @NotNull(message = "Le département est obligatoire")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bed_id")
    private Bed bed;

    @Column(name = "admission_date", nullable = false)
    @NotNull(message = "La date d'admission est obligatoire")
    private LocalDateTime admissionDate;

    @Column(name = "discharge_date")
    private LocalDateTime dischargeDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "admission_type", nullable = false)
    @NotNull(message = "Le type d'admission est obligatoire")
    private AdmissionType admissionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AdmissionStatus status = AdmissionStatus.ADMITTED;

    @Column(name = "chief_complaint", length = 1000)
    private String chiefComplaint;

    @Column(name = "diagnosis", length = 1000)
    private String diagnosis;

    @Column(name = "treatment_plan", length = 2000)
    private String treatmentPlan;

    @Column(name = "discharge_summary", length = 2000)
    private String dischargeSummary;

    @Column(name = "discharge_instructions", length = 2000)
    private String dischargeInstructions;

    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    @Column(name = "insurance_approval", length = 100)
    private String insuranceApproval;

    @Column(name = "emergency_contact_notified")
    private Boolean emergencyContactNotified = false;

    @Column(name = "estimated_cost")
    @DecimalMin(value = "0.0", message = "Le coût estimé ne peut pas être négatif")
    private Double estimatedCost;

    @Column(name = "actual_cost")
    @DecimalMin(value = "0.0", message = "Le coût réel ne peut pas être négatif")
    private Double actualCost;

    @Column(name = "notes", length = 2000)
    private String notes;

    @PrePersist
    public void prePersist() {
        if (admissionDate == null) {
            admissionDate = LocalDateTime.now();
        }
    }

    public enum AdmissionType {
        EMERGENCY("Urgence"),
        ELECTIVE("Programmée"),
        ROUTINE("Routine"),
        TRANSFER("Transfert");

        private final String displayName;

        AdmissionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum AdmissionStatus {
        ADMITTED("Admis"),
        DISCHARGED("Sorti"),
        TRANSFERRED("Transféré"),
        DECEASED("Décédé");

        private final String displayName;

        AdmissionStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}