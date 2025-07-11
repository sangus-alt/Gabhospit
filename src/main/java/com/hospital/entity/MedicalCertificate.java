package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_certificates")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MedicalCertificate extends BaseEntity {

    @Column(name = "certificate_number", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Le numéro de certificat ne peut pas être vide")
    private String certificateNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Le médecin est obligatoire")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;

    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_type", nullable = false)
    @NotNull(message = "Le type de certificat est obligatoire")
    private CertificateType certificateType;

    @Column(name = "issue_date", nullable = false)
    @NotNull(message = "La date d'émission est obligatoire")
    private LocalDateTime issueDate;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Column(name = "diagnosis", length = 1000)
    private String diagnosis;

    @Column(name = "medical_findings", length = 2000)
    private String medicalFindings;

    @Column(name = "recommendations", length = 2000)
    private String recommendations;

    @Column(name = "work_restrictions", length = 1000)
    private String workRestrictions;

    @Column(name = "activity_limitations", length = 1000)
    private String activityLimitations;

    @Column(name = "fitness_for_work")
    private Boolean fitnessForWork = true;

    @Column(name = "fitness_for_sport")
    private Boolean fitnessForSport = true;

    @Column(name = "fitness_for_travel")
    private Boolean fitnessForTravel = true;

    @Column(name = "fitness_for_driving")
    private Boolean fitnessForDriving = true;

    @Column(name = "sick_leave_days")
    @Min(value = 0, message = "Le nombre de jours d'arrêt ne peut pas être négatif")
    private Integer sickLeaveDays;

    @Column(name = "sick_leave_from")
    private LocalDate sickLeaveFrom;

    @Column(name = "sick_leave_until")
    private LocalDate sickLeaveUntil;

    @Column(name = "partial_work_capacity")
    private Boolean partialWorkCapacity = false;

    @Column(name = "work_capacity_percentage")
    @Min(value = 0, message = "Le pourcentage de capacité de travail ne peut pas être négatif")
    @Max(value = 100, message = "Le pourcentage de capacité de travail ne peut pas dépasser 100")
    private Integer workCapacityPercentage;

    @Column(name = "return_to_work_date")
    private LocalDate returnToWorkDate;

    @Column(name = "follow_up_required")
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "follow_up_instructions", length = 1000)
    private String followUpInstructions;

    @Column(name = "special_instructions", length = 2000)
    private String specialInstructions;

    @Column(name = "issued_to", length = 200)
    private String issuedTo;

    @Column(name = "purpose", length = 500)
    private String purpose;

    @Column(name = "employer_name", length = 200)
    private String employerName;

    @Column(name = "insurance_company", length = 200)
    private String insuranceCompany;

    @Column(name = "third_party_name", length = 200)
    private String thirdPartyName;

    @Column(name = "confidentiality_level", length = 50)
    private String confidentialityLevel = "STANDARD";

    @Column(name = "language", length = 10)
    private String language = "fr";

    @Column(name = "template_used", length = 100)
    private String templateUsed;

    @Column(name = "printed")
    private Boolean printed = false;

    @Column(name = "print_date")
    private LocalDateTime printDate;

    @Column(name = "printed_by", length = 100)
    private String printedBy;

    @Column(name = "email_sent")
    private Boolean emailSent = false;

    @Column(name = "email_sent_date")
    private LocalDateTime emailSentDate;

    @Column(name = "email_sent_to", length = 200)
    private String emailSentTo;

    @Column(name = "digital_signature")
    private String digitalSignature;

    @Column(name = "verification_code", length = 50)
    private String verificationCode;

    @Column(name = "qr_code")
    private String qrCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CertificateStatus status = CertificateStatus.ACTIVE;

    @Column(name = "cancelled_date")
    private LocalDateTime cancelledDate;

    @Column(name = "cancelled_by", length = 100)
    private String cancelledBy;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "amended_date")
    private LocalDateTime amendedDate;

    @Column(name = "amended_by", length = 100)
    private String amendedBy;

    @Column(name = "amendment_reason", length = 500)
    private String amendmentReason;

    @Column(name = "original_certificate_id")
    private Long originalCertificateId;

    @Column(name = "notes", length = 2000)
    private String notes;

    @PrePersist
    public void prePersist() {
        if (issueDate == null) {
            issueDate = LocalDateTime.now();
        }
        if (validFrom == null) {
            validFrom = LocalDate.now();
        }
    }

    public enum CertificateType {
        MEDICAL_FITNESS("Certificat médical d'aptitude"),
        SICK_LEAVE("Certificat d'arrêt de travail"),
        WORK_ACCIDENT("Certificat d'accident de travail"),
        SPORT_FITNESS("Certificat médical sportif"),
        TRAVEL_FITNESS("Certificat médical de voyage"),
        DRIVING_FITNESS("Certificat médical de conduite"),
        SCHOOL_FITNESS("Certificat médical scolaire"),
        VACCINATION("Certificat de vaccination"),
        MEDICAL_EXEMPTION("Certificat d'exemption médicale"),
        DISABILITY("Certificat d'invalidité"),
        PREGNANCY("Certificat de grossesse"),
        MATERNITY_LEAVE("Certificat de congé maternité"),
        PATERNITY_LEAVE("Certificat de congé paternité"),
        DEATH("Certificat de décès"),
        BIRTH("Certificat de naissance"),
        MEDICAL_REPORT("Rapport médical"),
        EXPERT_OPINION("Avis d'expert médical"),
        INSURANCE_CLAIM("Certificat pour assurance"),
        LEGAL_PROCEEDING("Certificat pour procédure judiciaire"),
        SECOND_OPINION("Certificat de deuxième avis"),
        OTHER("Autre");

        private final String displayName;

        CertificateType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum CertificateStatus {
        ACTIVE("Actif"),
        EXPIRED("Expiré"),
        CANCELLED("Annulé"),
        AMENDED("Modifié"),
        SUPERSEDED("Remplacé"),
        DRAFT("Brouillon"),
        PENDING_APPROVAL("En attente d'approbation"),
        REJECTED("Rejeté");

        private final String displayName;

        CertificateStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}