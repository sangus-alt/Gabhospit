package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "patient_insurances")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PatientInsurance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_id", nullable = false)
    @NotNull(message = "L'assurance est obligatoire")
    private Insurance insurance;

    @Column(name = "member_id", nullable = false, length = 100)
    @NotBlank(message = "L'ID du membre ne peut pas être vide")
    private String memberId;

    @Column(name = "subscriber_id", length = 100)
    private String subscriberId;

    @Column(name = "relationship_to_subscriber", length = 50)
    private String relationshipToSubscriber;

    @Column(name = "subscriber_first_name", length = 100)
    private String subscriberFirstName;

    @Column(name = "subscriber_last_name", length = 100)
    private String subscriberLastName;

    @Column(name = "subscriber_date_of_birth")
    private LocalDate subscriberDateOfBirth;

    @Column(name = "subscriber_gender", length = 10)
    private String subscriberGender;

    @Column(name = "subscriber_ssn", length = 20)
    private String subscriberSsn;

    @Column(name = "subscriber_address", length = 500)
    private String subscriberAddress;

    @Column(name = "subscriber_phone", length = 20)
    private String subscriberPhone;

    @Column(name = "subscriber_email", length = 100)
    private String subscriberEmail;

    @Column(name = "subscriber_employer", length = 200)
    private String subscriberEmployer;

    @Column(name = "effective_date", nullable = false)
    @NotNull(message = "La date d'effet est obligatoire")
    private LocalDate effectiveDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Column(name = "is_primary")
    private Boolean isPrimary = true;

    @Column(name = "priority_order")
    @Min(value = 1, message = "L'ordre de priorité doit être d'au moins 1")
    private Integer priorityOrder = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InsuranceStatus status = InsuranceStatus.ACTIVE;

    @Column(name = "verification_date")
    private LocalDate verificationDate;

    @Column(name = "last_verified_by", length = 100)
    private String lastVerifiedBy;

    @Column(name = "eligibility_verified")
    private Boolean eligibilityVerified = false;

    @Column(name = "eligibility_verification_date")
    private LocalDate eligibilityVerificationDate;

    @Column(name = "benefits_verified")
    private Boolean benefitsVerified = false;

    @Column(name = "benefits_verification_date")
    private LocalDate benefitsVerificationDate;

    @Column(name = "copay_amount")
    @DecimalMin(value = "0.0", message = "Le montant du copaiement ne peut pas être négatif")
    private Double copayAmount;

    @Column(name = "deductible_amount")
    @DecimalMin(value = "0.0", message = "Le montant de la franchise ne peut pas être négatif")
    private Double deductibleAmount;

    @Column(name = "deductible_met_amount")
    @DecimalMin(value = "0.0", message = "Le montant de la franchise acquittée ne peut pas être négatif")
    private Double deductibleMetAmount;

    @Column(name = "out_of_pocket_max")
    @DecimalMin(value = "0.0", message = "Le maximum débours ne peut pas être négatif")
    private Double outOfPocketMax;

    @Column(name = "out_of_pocket_met_amount")
    @DecimalMin(value = "0.0", message = "Le montant débours acquitté ne peut pas être négatif")
    private Double outOfPocketMetAmount;

    @Column(name = "coinsurance_percentage")
    @DecimalMin(value = "0.0", message = "Le pourcentage de coassurance ne peut pas être négatif")
    @DecimalMax(value = "100.0", message = "Le pourcentage de coassurance ne peut pas dépasser 100")
    private Double coinsurancePercentage;

    @Column(name = "assignment_of_benefits")
    private Boolean assignmentOfBenefits = true;

    @Column(name = "authorization_required")
    private Boolean authorizationRequired = false;

    @Column(name = "referral_required")
    private Boolean referralRequired = false;

    @Column(name = "pcp_required")
    private Boolean pcpRequired = false;

    @Column(name = "pcp_name", length = 200)
    private String pcpName;

    @Column(name = "pcp_phone", length = 20)
    private String pcpPhone;

    @Column(name = "pcp_npi", length = 20)
    private String pcpNpi;

    @Column(name = "notes", length = 2000)
    private String notes;

    public enum InsuranceStatus {
        ACTIVE("Actif"),
        INACTIVE("Inactif"),
        SUSPENDED("Suspendu"),
        EXPIRED("Expiré"),
        CANCELLED("Annulé"),
        TERMINATED("Terminé"),
        PENDING_VERIFICATION("En attente de vérification");

        private final String displayName;

        InsuranceStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}