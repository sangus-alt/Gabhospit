package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Staff extends BaseEntity {

    @Column(name = "staff_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le numéro du personnel ne peut pas être vide")
    private String staffNumber;

    @Column(name = "first_name", nullable = false, length = 100)
    @NotBlank(message = "Le prénom ne peut pas être vide")
    @Size(min = 2, max = 100, message = "Le prénom doit contenir entre 2 et 100 caractères")
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String lastName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "date_of_birth")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Patient.Gender gender;

    @Column(name = "phone", length = 20)
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Format de téléphone invalide")
    private String phone;

    @Column(name = "email", length = 100)
    @Email(message = "Format d'email invalide")
    private String email;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "national_id", length = 50)
    private String nationalId;

    @Column(name = "social_security_number", length = 50)
    private String socialSecurityNumber;

    @Column(name = "employee_id", unique = true, nullable = false, length = 50)
    @NotBlank(message = "L'ID employé ne peut pas être vide")
    private String employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false)
    @NotNull(message = "Le poste est obligatoire")
    private StaffPosition position;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @NotNull(message = "Le rôle est obligatoire")
    private StaffRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    @NotNull(message = "Le département est obligatoire")
    private Department department;

    @Column(name = "hire_date", nullable = false)
    @NotNull(message = "La date d'embauche est obligatoire")
    private LocalDateTime hireDate;

    @Column(name = "termination_date")
    private LocalDateTime terminationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false)
    private EmploymentStatus employmentStatus = EmploymentStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false)
    private EmploymentType employmentType = EmploymentType.FULL_TIME;

    @Column(name = "salary")
    @DecimalMin(value = "0.0", message = "Le salaire ne peut pas être négatif")
    private Double salary;

    @Column(name = "hourly_rate")
    @DecimalMin(value = "0.0", message = "Le taux horaire ne peut pas être négatif")
    private Double hourlyRate;

    @Column(name = "currency", length = 10)
    private String currency = "EUR";

    @Column(name = "qualification", length = 500)
    private String qualification;

    @Column(name = "certification", length = 500)
    private String certification;

    @Column(name = "license_number", length = 100)
    private String licenseNumber;

    @Column(name = "license_expiry_date")
    private LocalDate licenseExpiryDate;

    @Column(name = "experience_years")
    @Min(value = 0, message = "L'expérience ne peut pas être négative")
    private Integer experienceYears;

    @Column(name = "shift", length = 50)
    private String shift; // Morning, Evening, Night

    @Column(name = "work_schedule", length = 500)
    private String workSchedule;

    @Column(name = "supervisor_id")
    private Long supervisorId;

    @Column(name = "emergency_contact_name", length = 200)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(name = "emergency_contact_relation", length = 100)
    private String emergencyContactRelation;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "bio", length = 1000)
    private String bio;

    @Column(name = "languages", length = 200)
    private String languages;

    @Column(name = "specialization", length = 200)
    private String specialization;

    @Column(name = "access_level")
    @Min(value = 1, message = "Le niveau d'accès doit être d'au moins 1")
    @Max(value = 5, message = "Le niveau d'accès ne peut pas dépasser 5")
    private Integer accessLevel = 1;

    @Column(name = "is_on_duty")
    private Boolean isOnDuty = false;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "login_count")
    private Integer loginCount = 0;

    @Column(name = "notes", length = 2000)
    private String notes;

    @PrePersist
    public void prePersist() {
        if (hireDate == null) {
            hireDate = LocalDateTime.now();
        }
    }

    public enum StaffPosition {
        NURSE("Infirmier/Infirmière"),
        DOCTOR("Médecin"),
        SURGEON("Chirurgien"),
        SPECIALIST("Spécialiste"),
        RESIDENT("Résident"),
        INTERN("Interne"),
        PHARMACIST("Pharmacien"),
        LABORATORY_TECHNICIAN("Technicien de laboratoire"),
        RADIOLOGIST("Radiologue"),
        RADIOLOGIC_TECHNOLOGIST("Technologue en radiologie"),
        PHYSICAL_THERAPIST("Physiothérapeute"),
        OCCUPATIONAL_THERAPIST("Ergothérapeute"),
        RESPIRATORY_THERAPIST("Inhalothérapeute"),
        DIETITIAN("Diététicien"),
        SOCIAL_WORKER("Travailleur social"),
        PSYCHOLOGIST("Psychologue"),
        PSYCHIATRIST("Psychiatre"),
        ANESTHESIOLOGIST("Anesthésiste"),
        ADMINISTRATOR("Administrateur"),
        RECEPTIONIST("Réceptionniste"),
        SECURITY_GUARD("Agent de sécurité"),
        MAINTENANCE_WORKER("Ouvrier de maintenance"),
        CLEANER("Agent d'entretien"),
        CHEF("Chef cuisinier"),
        DRIVER("Chauffeur"),
        IT_SPECIALIST("Spécialiste IT"),
        ACCOUNTANT("Comptable"),
        HR_SPECIALIST("Spécialiste RH"),
        OTHER("Autre");

        private final String displayName;

        StaffPosition(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum StaffRole {
        MEDICAL_STAFF("Personnel médical"),
        NURSING_STAFF("Personnel infirmier"),
        ADMINISTRATIVE_STAFF("Personnel administratif"),
        TECHNICAL_STAFF("Personnel technique"),
        SUPPORT_STAFF("Personnel de soutien"),
        MANAGEMENT("Direction"),
        SECURITY("Sécurité"),
        MAINTENANCE("Maintenance"),
        HOUSEKEEPING("Entretien"),
        FOOD_SERVICE("Service alimentaire"),
        TRANSPORT("Transport"),
        IT_SUPPORT("Support IT"),
        FINANCE("Finance"),
        HUMAN_RESOURCES("Ressources humaines"),
        OTHER("Autre");

        private final String displayName;

        StaffRole(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum EmploymentStatus {
        ACTIVE("Actif"),
        INACTIVE("Inactif"),
        ON_LEAVE("En congé"),
        SUSPENDED("Suspendu"),
        TERMINATED("Licencié"),
        RETIRED("Retraité");

        private final String displayName;

        EmploymentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum EmploymentType {
        FULL_TIME("Temps plein"),
        PART_TIME("Temps partiel"),
        CONTRACT("Contrat"),
        TEMPORARY("Temporaire"),
        INTERN("Stagiaire"),
        CONSULTANT("Consultant"),
        VOLUNTEER("Bénévole");

        private final String displayName;

        EmploymentType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}