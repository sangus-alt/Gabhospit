package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_attachments")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MedicalAttachment extends BaseEntity {

    @Column(name = "attachment_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le numéro de pièce jointe ne peut pas être vide")
    private String attachmentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    @NotNull(message = "Le dossier médical est obligatoire")
    private MedicalRecord medicalRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    @Column(name = "file_name", nullable = false, length = 500)
    @NotBlank(message = "Le nom de fichier ne peut pas être vide")
    private String fileName;

    @Column(name = "original_file_name", length = 500)
    private String originalFileName;

    @Column(name = "file_path", nullable = false, length = 1000)
    @NotBlank(message = "Le chemin de fichier ne peut pas être vide")
    private String filePath;

    @Column(name = "file_url", length = 1000)
    private String fileUrl;

    @Column(name = "file_size")
    @Min(value = 0, message = "La taille du fichier ne peut pas être négative")
    private Long fileSize;

    @Column(name = "file_type", length = 100)
    private String fileType;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "attachment_type", nullable = false)
    @NotNull(message = "Le type de pièce jointe est obligatoire")
    private AttachmentType attachmentType;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "uploaded_date", nullable = false)
    @NotNull(message = "La date de téléversement est obligatoire")
    private LocalDateTime uploadedDate;

    @Column(name = "uploaded_by", length = 100)
    private String uploadedBy;

    @Column(name = "is_confidential")
    private Boolean isConfidential = false;

    @Column(name = "access_level")
    @Min(value = 1, message = "Le niveau d'accès doit être d'au moins 1")
    @Max(value = 5, message = "Le niveau d'accès ne peut pas dépasser 5")
    private Integer accessLevel = 1;

    @Column(name = "is_encrypted")
    private Boolean isEncrypted = false;

    @Column(name = "encryption_key", length = 500)
    private String encryptionKey;

    @Column(name = "checksum", length = 100)
    private String checksum;

    @Column(name = "version")
    @Min(value = 1, message = "La version doit être d'au moins 1")
    private Integer version = 1;

    @Column(name = "parent_attachment_id")
    private Long parentAttachmentId;

    @Column(name = "is_latest_version")
    private Boolean isLatestVersion = true;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "viewed_count")
    @Min(value = 0, message = "Le nombre de vues ne peut pas être négatif")
    private Integer viewedCount = 0;

    @Column(name = "last_viewed_date")
    private LocalDateTime lastViewedDate;

    @Column(name = "last_viewed_by", length = 100)
    private String lastViewedBy;

    @Column(name = "download_count")
    @Min(value = 0, message = "Le nombre de téléchargements ne peut pas être négatif")
    private Integer downloadCount = 0;

    @Column(name = "last_downloaded_date")
    private LocalDateTime lastDownloadedDate;

    @Column(name = "last_downloaded_by", length = 100)
    private String lastDownloadedBy;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    @Column(name = "deletion_reason", length = 500)
    private String deletionReason;

    @Column(name = "tags", length = 1000)
    private String tags;

    @Column(name = "keywords", length = 1000)
    private String keywords;

    @Column(name = "notes", length = 2000)
    private String notes;

    @PrePersist
    public void prePersist() {
        if (uploadedDate == null) {
            uploadedDate = LocalDateTime.now();
        }
    }

    public enum AttachmentType {
        MEDICAL_IMAGE("Image médicale"),
        LAB_REPORT("Rapport de laboratoire"),
        RADIOLOGY_REPORT("Rapport de radiologie"),
        PATHOLOGY_REPORT("Rapport de pathologie"),
        PRESCRIPTION("Prescription"),
        SURGICAL_REPORT("Rapport chirurgical"),
        CONSULTATION_NOTES("Notes de consultation"),
        DISCHARGE_SUMMARY("Résumé de sortie"),
        INSURANCE_DOCUMENT("Document d'assurance"),
        CONSENT_FORM("Formulaire de consentement"),
        MEDICAL_CERTIFICATE("Certificat médical"),
        REFERRAL_LETTER("Lettre de référence"),
        TEST_RESULT("Résultat de test"),
        TREATMENT_PLAN("Plan de traitement"),
        PATIENT_PHOTO("Photo du patient"),
        X_RAY("Radiographie"),
        CT_SCAN("Scanner CT"),
        MRI_SCAN("IRM"),
        ULTRASOUND("Échographie"),
        ECG("ECG"),
        EEG("EEG"),
        BLOOD_TEST_RESULT("Résultat d'analyse sanguine"),
        URINE_TEST_RESULT("Résultat d'analyse urinaire"),
        BIOPSY_REPORT("Rapport de biopsie"),
        VACCINATION_RECORD("Carnet de vaccination"),
        ALLERGY_RECORD("Dossier d'allergie"),
        MEDICATION_LIST("Liste de médicaments"),
        VITAL_SIGNS("Signes vitaux"),
        NURSING_NOTES("Notes infirmières"),
        THERAPY_NOTES("Notes de thérapie"),
        REHABILITATION_REPORT("Rapport de réhabilitation"),
        OTHER("Autre");

        private final String displayName;

        AttachmentType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}