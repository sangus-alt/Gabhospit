package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "lab_tests")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LabTest extends BaseEntity {

    @Column(name = "test_code", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le code de test ne peut pas être vide")
    private String testCode;

    @Column(name = "test_name", nullable = false, length = 200)
    @NotBlank(message = "Le nom du test ne peut pas être vide")
    private String testName;

    @Column(name = "test_name_en", length = 200)
    private String testNameEn;

    @Column(name = "category", nullable = false, length = 100)
    @NotBlank(message = "La catégorie ne peut pas être vide")
    private String category;

    @Column(name = "specimen_type", nullable = false, length = 100)
    @NotBlank(message = "Le type d'échantillon ne peut pas être vide")
    private String specimenType;

    @Column(name = "specimen_volume", length = 50)
    private String specimenVolume;

    @Column(name = "container_type", length = 100)
    private String containerType;

    @Column(name = "collection_instructions", length = 1000)
    private String collectionInstructions;

    @Column(name = "methodology", length = 500)
    private String methodology;

    @Column(name = "normal_range_male", length = 100)
    private String normalRangeMale;

    @Column(name = "normal_range_female", length = 100)
    private String normalRangeFemale;

    @Column(name = "normal_range_child", length = 100)
    private String normalRangeChild;

    @Column(name = "unit", length = 50)
    private String unit;

    @Column(name = "tat_hours")
    @Min(value = 0, message = "Le TAT ne peut pas être négatif")
    private Integer tatHours; // Turnaround Time

    @Column(name = "cost")
    @DecimalMin(value = "0.0", message = "Le coût ne peut pas être négatif")
    private Double cost;

    @Column(name = "is_stat_available")
    private Boolean isStatAvailable = false;

    @Column(name = "stat_tat_hours")
    private Integer statTatHours;

    @Column(name = "stat_cost")
    private Double statCost;

    @Column(name = "requires_fasting")
    private Boolean requiresFasting = false;

    @Column(name = "fasting_hours")
    private Integer fastingHours;

    @Column(name = "special_requirements", length = 1000)
    private String specialRequirements;

    @Column(name = "clinical_significance", length = 2000)
    private String clinicalSignificance;

    @Column(name = "interference_factors", length = 1000)
    private String interferenceFactors;

    @Column(name = "critical_values", length = 200)
    private String criticalValues;

    @Column(name = "panic_values", length = 200)
    private String panicValues;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TestStatus status = TestStatus.ACTIVE;

    @Column(name = "department_code", length = 20)
    private String departmentCode;

    @Column(name = "equipment_required", length = 500)
    private String equipmentRequired;

    @Column(name = "interpretation_guidelines", length = 2000)
    private String interpretationGuidelines;

    @Column(name = "related_tests", length = 500)
    private String relatedTests;

    @Column(name = "minimum_age_months")
    private Integer minimumAgeMonths;

    @Column(name = "maximum_age_months")
    private Integer maximumAgeMonths;

    @Column(name = "gender_specific")
    private Boolean genderSpecific = false;

    @Column(name = "pregnancy_safe")
    private Boolean pregnancySafe = true;

    @Column(name = "home_collection_available")
    private Boolean homeCollectionAvailable = false;

    @Column(name = "appointment_required")
    private Boolean appointmentRequired = false;

    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    // Nouveaux champs pour corriger les erreurs de compilation
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "sample_type", length = 100)
    private String sampleType;

    @Column(name = "price")
    private Double price;

    @Column(name = "processing_time_hours")
    private Integer processingTimeHours;

    @Column(name = "preparation_instructions", length = 1000)
    private String preparationInstructions;

    @Column(name = "normal_ranges", length = 500)
    private String normalRanges;

    @OneToMany(mappedBy = "labTest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LabTestParameter> parameters;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_order_id")
    private LabOrder labOrder;

    @PrePersist
    public void prePersist() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
        if (name == null) {
            name = testName;
        }
        if (description == null) {
            description = clinicalSignificance;
        }
        if (sampleType == null) {
            sampleType = specimenType;
        }
        if (price == null) {
            price = cost;
        }
        if (processingTimeHours == null) {
            processingTimeHours = tatHours;
        }
        if (preparationInstructions == null) {
            preparationInstructions = collectionInstructions;
        }
        if (normalRanges == null) {
            normalRanges = normalRangeMale + " / " + normalRangeFemale;
        }
    }

    public enum TestStatus {
        ACTIVE("Actif"),
        INACTIVE("Inactif"),
        DISCONTINUED("Discontinué"),
        UNDER_REVISION("En révision");

        private final String displayName;

        TestStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}