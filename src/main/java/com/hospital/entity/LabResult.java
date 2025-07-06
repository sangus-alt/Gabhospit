package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_results")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LabResult extends BaseEntity {

    @Column(name = "result_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le numéro de résultat ne peut pas être vide")
    private String resultNumber;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_order_id")
    private LabOrder labOrder;

    @Column(name = "test_name", nullable = false, length = 200)
    @NotBlank(message = "Le nom du test ne peut pas être vide")
    private String testName;

    @Column(name = "test_code", length = 50)
    private String testCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "test_category", nullable = false)
    @NotNull(message = "La catégorie du test est obligatoire")
    private TestCategory testCategory;

    @Column(name = "specimen_type", length = 100)
    private String specimenType;

    @Column(name = "specimen_collection_date")
    private LocalDateTime specimenCollectionDate;

    @Column(name = "test_date", nullable = false)
    @NotNull(message = "La date du test est obligatoire")
    private LocalDateTime testDate;

    @Column(name = "result_date")
    private LocalDateTime resultDate;

    @Column(name = "result_value", length = 100)
    private String resultValue;

    @Column(name = "unit", length = 50)
    private String unit;

    @Column(name = "reference_range", length = 100)
    private String referenceRange;

    @Column(name = "normal_range", length = 100)
    private String normalRange;

    @Enumerated(EnumType.STRING)
    @Column(name = "result_status", nullable = false)
    private ResultStatus resultStatus = ResultStatus.PENDING;

    @Column(name = "abnormal_flag", length = 20)
    private String abnormalFlag; // H (High), L (Low), N (Normal)

    @Column(name = "critical_flag")
    private Boolean criticalFlag = false;

    @Column(name = "comments", length = 1000)
    private String comments;

    @Column(name = "interpretation", length = 1000)
    private String interpretation;

    @Column(name = "recommendations", length = 1000)
    private String recommendations;

    @Column(name = "technician_name", length = 100)
    private String technicianName;

    @Column(name = "lab_name", length = 200)
    private String labName;

    @Column(name = "lab_address", length = 500)
    private String labAddress;

    @Column(name = "lab_contact", length = 50)
    private String labContact;

    @Column(name = "verified_by", length = 100)
    private String verifiedBy;

    @Column(name = "verified_date")
    private LocalDateTime verifiedDate;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "report_url", length = 500)
    private String reportUrl;

    @Column(name = "cost")
    @DecimalMin(value = "0.0", message = "Le coût ne peut pas être négatif")
    private Double cost;

    @Column(name = "is_urgent")
    private Boolean isUrgent = false;

    @Column(name = "notes", length = 2000)
    private String notes;

    @PrePersist
    public void prePersist() {
        if (testDate == null) {
            testDate = LocalDateTime.now();
        }
        if (specimenCollectionDate == null) {
            specimenCollectionDate = LocalDateTime.now();
        }
    }

    public enum TestCategory {
        BLOOD_TEST("Analyse sanguine"),
        URINE_TEST("Analyse urinaire"),
        BIOCHEMISTRY("Biochimie"),
        HEMATOLOGY("Hématologie"),
        MICROBIOLOGY("Microbiologie"),
        IMMUNOLOGY("Immunologie"),
        PATHOLOGY("Pathologie"),
        GENETICS("Génétique"),
        TOXICOLOGY("Toxicologie"),
        ENDOCRINOLOGY("Endocrinologie"),
        CARDIOLOGY("Cardiologie"),
        ONCOLOGY("Oncologie"),
        SEROLOGY("Sérologie"),
        VIROLOGY("Virologie"),
        PARASITOLOGY("Parasitologie"),
        OTHER("Autre");

        private final String displayName;

        TestCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ResultStatus {
        PENDING("En attente"),
        IN_PROGRESS("En cours"),
        COMPLETED("Terminé"),
        CANCELLED("Annulé"),
        REJECTED("Rejeté");

        private final String displayName;

        ResultStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}