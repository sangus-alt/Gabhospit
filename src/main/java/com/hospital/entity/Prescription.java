package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "prescriptions")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Prescription extends BaseEntity {

    @Column(name = "prescription_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le numéro de prescription ne peut pas être vide")
    private String prescriptionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Le médecin est obligatoire")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id")
    private MedicalRecord medicalRecord;

    @Column(name = "prescription_date", nullable = false)
    @NotNull(message = "La date de prescription est obligatoire")
    private LocalDateTime prescriptionDate;

    @Column(name = "valid_until")
    @Future(message = "La date d'expiration doit être dans le futur")
    private LocalDate validUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PrescriptionStatus status = PrescriptionStatus.ACTIVE;

    @Column(name = "diagnosis", length = 1000)
    private String diagnosis;

    @Column(name = "instructions", length = 2000)
    private String instructions;

    @Column(name = "special_instructions", length = 1000)
    private String specialInstructions;

    @Column(name = "total_cost")
    @DecimalMin(value = "0.0", message = "Le coût total ne peut pas être négatif")
    private Double totalCost;

    @Column(name = "is_dispensed")
    private Boolean isDispensed = false;

    @Column(name = "dispensed_date")
    private LocalDateTime dispensedDate;

    @Column(name = "dispensed_by", length = 100)
    private String dispensedBy;

    @Column(name = "pharmacy_name", length = 200)
    private String pharmacyName;

    @Column(name = "pharmacy_address", length = 500)
    private String pharmacyAddress;

    @Column(name = "pharmacy_contact", length = 50)
    private String pharmacyContact;

    @Column(name = "is_emergency")
    private Boolean isEmergency = false;

    @Column(name = "refill_count")
    @Min(value = 0, message = "Le nombre de renouvellements ne peut pas être négatif")
    private Integer refillCount = 0;

    @Column(name = "max_refills")
    @Min(value = 0, message = "Le nombre maximum de renouvellements ne peut pas être négatif")
    private Integer maxRefills = 0;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "electronic_signature", length = 500)
    private String electronicSignature;

    @Column(name = "is_signed")
    private Boolean isSigned = false;

    @Column(name = "signed_date")
    private LocalDateTime signedDate;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PrescriptionItem> prescriptionItems;

    @PrePersist
    public void prePersist() {
        if (prescriptionDate == null) {
            prescriptionDate = LocalDateTime.now();
        }
        if (validUntil == null) {
            validUntil = LocalDate.now().plusMonths(3); // Valide 3 mois par défaut
        }
    }

    public enum PrescriptionStatus {
        ACTIVE("Actif"),
        EXPIRED("Expiré"),
        CANCELLED("Annulé"),
        COMPLETED("Terminé"),
        PARTIALLY_DISPENSED("Partiellement dispensé");

        private final String displayName;

        PrescriptionStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}