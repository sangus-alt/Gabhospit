package com.hospital.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "hospital_configuration")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HospitalConfiguration extends BaseEntity {

    @Column(name = "hospital_name", nullable = false, length = 200)
    @NotBlank(message = "Le nom de l'hôpital ne peut pas être vide")
    @Size(max = 200, message = "Le nom de l'hôpital ne peut dépasser 200 caractères")
    private String hospitalName;

    @Column(name = "hospital_address", length = 500)
    private String hospitalAddress;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "phone", length = 20)
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Format de téléphone invalide")
    private String phone;

    @Column(name = "fax", length = 20)
    private String fax;

    @Column(name = "email", length = 100)
    @Email(message = "Format d'email invalide")
    private String email;

    @Column(name = "website", length = 200)
    private String website;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "logo_base64", columnDefinition = "LONGTEXT")
    private String logoBase64;

    @Column(name = "hospital_type", length = 100)
    private String hospitalType; // PUBLIC, PRIVATE, UNIVERSITY, SPECIALIZED

    @Column(name = "license_number", length = 100)
    private String licenseNumber;

    @Column(name = "director_name", length = 200)
    private String directorName;

    @Column(name = "medical_director_name", length = 200)
    private String medicalDirectorName;

    @Column(name = "established_date")
    private LocalDateTime establishedDate;

    @Column(name = "total_beds")
    private Integer totalBeds;

    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone;

    @Column(name = "appointment_phone", length = 20)
    private String appointmentPhone;

    @Column(name = "working_hours", length = 200)
    private String workingHours;

    @Column(name = "emergency_hours", length = 200)
    private String emergencyHours;

    @Column(name = "language", length = 10)
    private String language; // FR, EN, ES, etc.

    @Column(name = "timezone", length = 50)
    private String timezone;

    @Column(name = "currency", length = 10)
    private String currency; // EUR, USD, etc.

    @Column(name = "tax_rate")
    private Double taxRate;

    @Column(name = "patient_number_prefix", length = 10)
    private String patientNumberPrefix; // P, PAT, etc.

    @Column(name = "doctor_number_prefix", length = 10)
    private String doctorNumberPrefix; // D, DR, etc.

    @Column(name = "bill_number_prefix", length = 10)
    private String billNumberPrefix; // BILL, FAC, etc.

    @Column(name = "enable_sms_notifications")
    private Boolean enableSmsNotifications = false;

    @Column(name = "enable_email_notifications")
    private Boolean enableEmailNotifications = true;

    @Column(name = "auto_backup_enabled")
    private Boolean autoBackupEnabled = true;

    @Column(name = "backup_frequency_hours")
    private Integer backupFrequencyHours = 24;

    @Column(name = "session_timeout_minutes")
    private Integer sessionTimeoutMinutes = 30;

    @Column(name = "max_login_attempts")
    private Integer maxLoginAttempts = 5;

    @Column(name = "patient_card_template", length = 50)
    private String patientCardTemplate = "DEFAULT";

    @Column(name = "prescription_template", length = 50)
    private String prescriptionTemplate = "DEFAULT";

    @Column(name = "certificate_template", length = 50)
    private String certificateTemplate = "DEFAULT";

    @Column(name = "footer_text", length = 500)
    private String footerText;

    @Column(name = "header_text", length = 500)
    private String headerText;

    @Column(name = "signature_image_url", length = 500)
    private String signatureImageUrl;

    @Column(name = "signature_base64", columnDefinition = "LONGTEXT")
    private String signatureBase64;

    @Column(name = "system_version", length = 20)
    private String systemVersion = "1.0.0";

    @Column(name = "last_backup_date")
    private LocalDateTime lastBackupDate;

    @Column(name = "maintenance_mode")
    private Boolean maintenanceMode = false;

    @Column(name = "maintenance_message", length = 500)
    private String maintenanceMessage;

    @PrePersist
    public void prePersist() {
        if (establishedDate == null) {
            establishedDate = LocalDateTime.now();
        }
        if (language == null) {
            language = "FR";
        }
        if (timezone == null) {
            timezone = "Europe/Paris";
        }
        if (currency == null) {
            currency = "EUR";
        }
        if (patientNumberPrefix == null) {
            patientNumberPrefix = "P";
        }
        if (doctorNumberPrefix == null) {
            doctorNumberPrefix = "D";
        }
        if (billNumberPrefix == null) {
            billNumberPrefix = "BILL";
        }
    }
}