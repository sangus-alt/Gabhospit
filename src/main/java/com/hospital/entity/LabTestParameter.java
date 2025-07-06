package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "lab_test_parameters")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LabTestParameter extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_test_id", nullable = false)
    @NotNull(message = "Le test de laboratoire est obligatoire")
    private LabTest labTest;

    @Column(name = "parameter_name", nullable = false, length = 200)
    @NotBlank(message = "Le nom du paramètre ne peut pas être vide")
    private String parameterName;

    @Column(name = "parameter_code", length = 50)
    private String parameterCode;

    @Column(name = "parameter_type", nullable = false, length = 50)
    @NotBlank(message = "Le type de paramètre ne peut pas être vide")
    private String parameterType; // NUMERIC, TEXT, OPTION_LIST, BOOLEAN

    @Column(name = "unit", length = 50)
    private String unit;

    @Column(name = "normal_range_min")
    private Double normalRangeMin;

    @Column(name = "normal_range_max")
    private Double normalRangeMax;

    @Column(name = "normal_range_text", length = 100)
    private String normalRangeText;

    @Column(name = "critical_low")
    private Double criticalLow;

    @Column(name = "critical_high")
    private Double criticalHigh;

    @Column(name = "panic_low")
    private Double panicLow;

    @Column(name = "panic_high")
    private Double panicHigh;

    @Column(name = "decimal_places")
    @Min(value = 0, message = "Le nombre de décimales ne peut pas être négatif")
    private Integer decimalPlaces = 2;

    @Column(name = "option_values", length = 1000)
    private String optionValues; // For option list type

    @Column(name = "default_value", length = 200)
    private String defaultValue;

    @Column(name = "is_mandatory")
    private Boolean isMandatory = true;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "calculation_formula", length = 500)
    private String calculationFormula;

    @Column(name = "validation_rules", length = 1000)
    private String validationRules;

    @Column(name = "interpretation_notes", length = 1000)
    private String interpretationNotes;

    @Column(name = "age_dependent")
    private Boolean ageDependent = false;

    @Column(name = "gender_dependent")
    private Boolean genderDependent = false;
}