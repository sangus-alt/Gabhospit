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
@Table(name = "insurances")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Insurance extends BaseEntity {

    @Column(name = "policy_number", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Le numéro de police ne peut pas être vide")
    private String policyNumber;

    @Column(name = "insurance_company", nullable = false, length = 200)
    @NotBlank(message = "La compagnie d'assurance ne peut pas être vide")
    private String insuranceCompany;

    @Column(name = "company_code", length = 20)
    private String companyCode;

    @Column(name = "company_address", length = 500)
    private String companyAddress;

    @Column(name = "company_phone", length = 20)
    private String companyPhone;

    @Column(name = "company_email", length = 100)
    private String companyEmail;

    @Column(name = "company_website", length = 200)
    private String companyWebsite;

    @Column(name = "contact_person", length = 200)
    private String contactPerson;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_type", nullable = false)
    @NotNull(message = "Le type d'assurance est obligatoire")
    private InsuranceType insuranceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "coverage_level", nullable = false)
    @NotNull(message = "Le niveau de couverture est obligatoire")
    private CoverageLevel coverageLevel;

    @Column(name = "group_number", length = 100)
    private String groupNumber;

    @Column(name = "employer_name", length = 200)
    private String employerName;

    @Column(name = "effective_date", nullable = false)
    @NotNull(message = "La date d'effet est obligatoire")
    private LocalDate effectiveDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "premium_amount")
    @DecimalMin(value = "0.0", message = "Le montant de la prime ne peut pas être négatif")
    private Double premiumAmount;

    @Column(name = "deductible_amount")
    @DecimalMin(value = "0.0", message = "Le montant de la franchise ne peut pas être négatif")
    private Double deductibleAmount;

    @Column(name = "copay_amount")
    @DecimalMin(value = "0.0", message = "Le montant du copaiement ne peut pas être négatif")
    private Double copayAmount;

    @Column(name = "coinsurance_percentage")
    @DecimalMin(value = "0.0", message = "Le pourcentage de coassurance ne peut pas être négatif")
    @DecimalMax(value = "100.0", message = "Le pourcentage de coassurance ne peut pas dépasser 100")
    private Double coinsurancePercentage;

    @Column(name = "out_of_pocket_max")
    @DecimalMin(value = "0.0", message = "Le maximum débours ne peut pas être négatif")
    private Double outOfPocketMax;

    @Column(name = "lifetime_max")
    @DecimalMin(value = "0.0", message = "Le maximum à vie ne peut pas être négatif")
    private Double lifetimeMax;

    @Column(name = "annual_max")
    @DecimalMin(value = "0.0", message = "Le maximum annuel ne peut pas être négatif")
    private Double annualMax;

    @Column(name = "currency", length = 10)
    private String currency = "EUR";

    @Column(name = "pre_authorization_required")
    private Boolean preAuthorizationRequired = false;

    @Column(name = "referral_required")
    private Boolean referralRequired = false;

    @Column(name = "emergency_coverage")
    private Boolean emergencyCoverage = true;

    @Column(name = "prescription_coverage")
    private Boolean prescriptionCoverage = true;

    @Column(name = "dental_coverage")
    private Boolean dentalCoverage = false;

    @Column(name = "vision_coverage")
    private Boolean visionCoverage = false;

    @Column(name = "mental_health_coverage")
    private Boolean mentalHealthCoverage = true;

    @Column(name = "maternity_coverage")
    private Boolean maternityCoverage = true;

    @Column(name = "preventive_care_coverage")
    private Boolean preventiveCareCoverage = true;

    @Column(name = "specialist_coverage")
    private Boolean specialistCoverage = true;

    @Column(name = "hospital_coverage")
    private Boolean hospitalCoverage = true;

    @Column(name = "surgery_coverage")
    private Boolean surgeryCoverage = true;

    @Column(name = "laboratory_coverage")
    private Boolean laboratoryCoverage = true;

    @Column(name = "radiology_coverage")
    private Boolean radiologyCoverage = true;

    @Column(name = "pharmacy_coverage")
    private Boolean pharmacyCoverage = true;

    @Column(name = "physical_therapy_coverage")
    private Boolean physicalTherapyCoverage = false;

    @Column(name = "alternative_medicine_coverage")
    private Boolean alternativeMedicineCoverage = false;

    @Column(name = "home_care_coverage")
    private Boolean homeCareCoverage = false;

    @Column(name = "long_term_care_coverage")
    private Boolean longTermCareCoverage = false;

    @Column(name = "exclusions", length = 2000)
    private String exclusions;

    @Column(name = "limitations", length = 2000)
    private String limitations;

    @Column(name = "network_restrictions", length = 1000)
    private String networkRestrictions;

    @Column(name = "geographical_restrictions", length = 1000)
    private String geographicalRestrictions;

    @Column(name = "waiting_period_days")
    private Integer waitingPeriodDays;

    @Column(name = "grace_period_days")
    private Integer gracePeriodDays = 30;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InsuranceStatus status = InsuranceStatus.ACTIVE;

    @Column(name = "verification_date")
    private LocalDateTime verificationDate;

    @Column(name = "last_verified_by", length = 100)
    private String lastVerifiedBy;

    @Column(name = "verification_method", length = 100)
    private String verificationMethod;

    @Column(name = "eligibility_checked")
    private Boolean eligibilityChecked = false;

    @Column(name = "eligibility_check_date")
    private LocalDateTime eligibilityCheckDate;

    @Column(name = "eligibility_response", length = 1000)
    private String eligibilityResponse;

    @Column(name = "claims_submission_method", length = 100)
    private String claimsSubmissionMethod;

    @Column(name = "electronic_claims_supported")
    private Boolean electronicClaimsSupported = true;

    @Column(name = "real_time_eligibility_supported")
    private Boolean realTimeEligibilitySupported = false;

    @Column(name = "prior_authorization_phone", length = 20)
    private String priorAuthorizationPhone;

    @Column(name = "prior_authorization_fax", length = 20)
    private String priorAuthorizationFax;

    @Column(name = "prior_authorization_website", length = 200)
    private String priorAuthorizationWebsite;

    @Column(name = "member_services_phone", length = 20)
    private String memberServicesPhone;

    @Column(name = "provider_services_phone", length = 20)
    private String providerServicesPhone;

    @Column(name = "customer_service_hours", length = 200)
    private String customerServiceHours;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "is_primary")
    private Boolean isPrimary = true;

    @Column(name = "coordination_of_benefits")
    private Boolean coordinationOfBenefits = false;

    @Column(name = "assignment_of_benefits")
    private Boolean assignmentOfBenefits = true;

    @Column(name = "accepts_assignment")
    private Boolean acceptsAssignment = true;

    @Column(name = "plan_name", length = 200)
    private String planName;

    @Column(name = "plan_type", length = 100)
    private String planType;

    @Column(name = "network_name", length = 200)
    private String networkName;

    @Column(name = "pcp_required")
    private Boolean pcpRequired = false;

    @Column(name = "pcp_name", length = 200)
    private String pcpName;

    @Column(name = "pcp_phone", length = 20)
    private String pcpPhone;

    @OneToMany(mappedBy = "insurance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PatientInsurance> patientInsurances;

    @OneToMany(mappedBy = "insurance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<InsuranceClaim> insuranceClaims;

    public enum InsuranceType {
        HEALTH("Santé"),
        DENTAL("Dentaire"),
        VISION("Vision"),
        LIFE("Vie"),
        DISABILITY("Invalidité"),
        WORKERS_COMPENSATION("Accident du travail"),
        TRAVEL("Voyage"),
        SUPPLEMENTAL("Complémentaire"),
        MEDICARE("Medicare"),
        MEDICAID("Medicaid"),
        PRIVATE("Privé"),
        GOVERNMENT("Gouvernemental"),
        MILITARY("Militaire"),
        STUDENT("Étudiant"),
        INTERNATIONAL("International");

        private final String displayName;

        InsuranceType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum CoverageLevel {
        BASIC("Basique"),
        STANDARD("Standard"),
        PREMIUM("Premium"),
        COMPREHENSIVE("Complet"),
        CATASTROPHIC("Catastrophique"),
        BRONZE("Bronze"),
        SILVER("Argent"),
        GOLD("Or"),
        PLATINUM("Platine");

        private final String displayName;

        CoverageLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum InsuranceStatus {
        ACTIVE("Actif"),
        INACTIVE("Inactif"),
        SUSPENDED("Suspendu"),
        EXPIRED("Expiré"),
        CANCELLED("Annulé"),
        TERMINATED("Terminé"),
        PENDING("En attente"),
        UNDER_REVIEW("En révision");

        private final String displayName;

        InsuranceStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}