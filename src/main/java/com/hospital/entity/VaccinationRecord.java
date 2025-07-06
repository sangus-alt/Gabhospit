package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vaccination_records")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VaccinationRecord extends BaseEntity {

    @Column(name = "record_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le numéro d'enregistrement ne peut pas être vide")
    private String recordNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id", nullable = false)
    @NotNull(message = "Le vaccin est obligatoire")
    private Vaccine vaccine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administered_by_id", nullable = false)
    @NotNull(message = "L'administrateur est obligatoire")
    private Staff administeredBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordered_by_id")
    private Doctor orderedBy;

    @Column(name = "administration_date", nullable = false)
    @NotNull(message = "La date d'administration est obligatoire")
    private LocalDateTime administrationDate;

    @Column(name = "dose_number")
    @Min(value = 1, message = "Le numéro de dose doit être d'au moins 1")
    private Integer doseNumber;

    @Column(name = "dose_series_total")
    private Integer doseSeriesTotal;

    @Column(name = "lot_number", length = 100)
    private String lotNumber;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "site_of_administration", nullable = false, length = 100)
    @NotBlank(message = "Le site d'administration ne peut pas être vide")
    private String siteOfAdministration;

    @Column(name = "route_of_administration", nullable = false, length = 100)
    @NotBlank(message = "La voie d'administration ne peut pas être vide")
    private String routeOfAdministration;

    @Column(name = "dose_volume", length = 20)
    private String doseVolume;

    @Column(name = "manufacturer", length = 200)
    private String manufacturer;

    @Column(name = "vaccine_name_at_time", length = 200)
    private String vaccineNameAtTime;

    @Column(name = "cvx_code", length = 10)
    private String cvxCode;

    @Column(name = "mvx_code", length = 10)
    private String mvxCode;

    @Column(name = "ndc_number", length = 20)
    private String ndcNumber;

    @Column(name = "funding_source", length = 100)
    private String fundingSource;

    @Column(name = "vis_date")
    private LocalDate visDate; // Vaccine Information Statement date

    @Column(name = "vis_given")
    private Boolean visGiven = false;

    @Column(name = "consent_obtained")
    private Boolean consentObtained = true;

    @Column(name = "consent_date")
    private LocalDateTime consentDate;

    @Column(name = "guardian_consent")
    private Boolean guardianConsent = false;

    @Column(name = "guardian_name", length = 200)
    private String guardianName;

    @Column(name = "patient_age_at_vaccination")
    private String patientAgeAtVaccination;

    @Column(name = "reason_for_vaccination", length = 500)
    private String reasonForVaccination;

    @Column(name = "contraindications_checked")
    private Boolean contraindicationsChecked = true;

    @Column(name = "adverse_events_screening")
    private Boolean adverseEventsScreening = true;

    @Column(name = "immediate_reaction")
    private Boolean immediateReaction = false;

    @Column(name = "reaction_description", length = 1000)
    private String reactionDescription;

    @Column(name = "delayed_reaction_reported")
    private Boolean delayedReactionReported = false;

    @Column(name = "vaers_reported")
    private Boolean vaersReported = false; // Vaccine Adverse Event Reporting System

    @Column(name = "vaers_id", length = 50)
    private String vaersId;

    @Column(name = "next_dose_due_date")
    private LocalDate nextDoseDueDate;

    @Column(name = "booster_due_date")
    private LocalDate boosterDueDate;

    @Column(name = "administration_location", length = 200)
    private String administrationLocation;

    @Column(name = "clinic_address", length = 500)
    private String clinicAddress;

    @Column(name = "temperature_recorded", length = 20)
    private String temperatureRecorded;

    @Column(name = "weight_recorded", length = 20)
    private String weightRecorded;

    @Enumerated(EnumType.STRING)
    @Column(name = "vaccination_status", nullable = false)
    private VaccinationStatus vaccinationStatus = VaccinationStatus.COMPLETED;

    @Column(name = "patient_refused")
    private Boolean patientRefused = false;

    @Column(name = "medical_exemption")
    private Boolean medicalExemption = false;

    @Column(name = "religious_exemption")
    private Boolean religiousExemption = false;

    @Column(name = "philosophical_exemption")
    private Boolean philosophicalExemption = false;

    @Column(name = "exemption_reason", length = 1000)
    private String exemptionReason;

    @Column(name = "special_circumstances", length = 1000)
    private String specialCircumstances;

    @Column(name = "follow_up_required")
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "printed_certificate")
    private Boolean printedCertificate = false;

    @Column(name = "electronic_certificate_sent")
    private Boolean electronicCertificateSent = false;

    @Column(name = "reported_to_registry")
    private Boolean reportedToRegistry = false;

    @Column(name = "registry_name", length = 200)
    private String registryName;

    @PrePersist
    public void prePersist() {
        if (administrationDate == null) {
            administrationDate = LocalDateTime.now();
        }
        if (consentDate == null) {
            consentDate = LocalDateTime.now();
        }
    }

    public enum VaccinationStatus {
        COMPLETED("Terminé"),
        PARTIALLY_COMPLETED("Partiellement terminé"),
        REFUSED("Refusé"),
        DEFERRED("Reporté"),
        CONTRAINDICATED("Contre-indiqué"),
        NOT_GIVEN("Non administré");

        private final String displayName;

        VaccinationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}