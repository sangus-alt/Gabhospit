package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "imaging_results")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImagingResult extends BaseEntity {

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

    @Column(name = "study_name", nullable = false, length = 200)
    @NotBlank(message = "Le nom de l'étude ne peut pas être vide")
    private String studyName;

    @Column(name = "study_code", length = 50)
    private String studyCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "imaging_type", nullable = false)
    @NotNull(message = "Le type d'imagerie est obligatoire")
    private ImagingType imagingType;

    @Column(name = "body_part", length = 100)
    private String bodyPart;

    @Column(name = "contrast_used")
    private Boolean contrastUsed = false;

    @Column(name = "contrast_type", length = 100)
    private String contrastType;

    @Column(name = "study_date", nullable = false)
    @NotNull(message = "La date de l'étude est obligatoire")
    private LocalDateTime studyDate;

    @Column(name = "result_date")
    private LocalDateTime resultDate;

    @Column(name = "findings", length = 2000)
    private String findings;

    @Column(name = "impression", length = 1000)
    private String impression;

    @Column(name = "conclusion", length = 1000)
    private String conclusion;

    @Column(name = "recommendations", length = 1000)
    private String recommendations;

    @Enumerated(EnumType.STRING)
    @Column(name = "result_status", nullable = false)
    private ResultStatus resultStatus = ResultStatus.PENDING;

    @Column(name = "is_urgent")
    private Boolean isUrgent = false;

    @Column(name = "is_abnormal")
    private Boolean isAbnormal = false;

    @Column(name = "radiologist_name", length = 100)
    private String radiologistName;

    @Column(name = "technician_name", length = 100)
    private String technicianName;

    @Column(name = "imaging_center", length = 200)
    private String imagingCenter;

    @Column(name = "imaging_center_address", length = 500)
    private String imagingCenterAddress;

    @Column(name = "imaging_center_contact", length = 50)
    private String imagingCenterContact;

    @Column(name = "equipment_used", length = 200)
    private String equipmentUsed;

    @Column(name = "technique", length = 500)
    private String technique;

    @Column(name = "image_count")
    @Min(value = 0, message = "Le nombre d'images ne peut pas être négatif")
    private Integer imageCount = 0;

    @Column(name = "dicom_url", length = 500)
    private String dicomUrl;

    @Column(name = "report_url", length = 500)
    private String reportUrl;

    @Column(name = "verified_by", length = 100)
    private String verifiedBy;

    @Column(name = "verified_date")
    private LocalDateTime verifiedDate;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "cost")
    @DecimalMin(value = "0.0", message = "Le coût ne peut pas être négatif")
    private Double cost;

    @Column(name = "radiation_dose", length = 100)
    private String radiationDose;

    @Column(name = "allergic_reaction")
    private Boolean allergicReaction = false;

    @Column(name = "complications", length = 1000)
    private String complications;

    @Column(name = "notes", length = 2000)
    private String notes;

    @PrePersist
    public void prePersist() {
        if (studyDate == null) {
            studyDate = LocalDateTime.now();
        }
    }

    public enum ImagingType {
        XRAY("Radiographie"),
        CT_SCAN("Scanner CT"),
        MRI("IRM"),
        ULTRASOUND("Échographie"),
        MAMMOGRAPHY("Mammographie"),
        PET_SCAN("TEP-Scan"),
        NUCLEAR_MEDICINE("Médecine nucléaire"),
        FLUOROSCOPY("Fluoroscopie"),
        ANGIOGRAPHY("Angiographie"),
        ECHOCARDIOGRAPHY("Échocardiographie"),
        EEG("EEG"),
        ECG("ECG"),
        BONE_DENSITOMETRY("Densitométrie osseuse"),
        ENDOSCOPY("Endoscopie"),
        COLONOSCOPY("Coloscopie"),
        BRONCHOSCOPY("Bronchoscopie"),
        OTHER("Autre");

        private final String displayName;

        ImagingType(String displayName) {
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