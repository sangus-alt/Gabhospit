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
@Table(name = "doctors")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Doctor extends BaseEntity {

    @Column(name = "doctor_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le numéro médecin ne peut pas être vide")
    private String doctorNumber; // Numéro unique du médecin

    @Column(name = "first_name", nullable = false, length = 100)
    @NotBlank(message = "Le prénom ne peut pas être vide")
    @Size(min = 2, max = 100, message = "Le prénom doit contenir entre 2 et 100 caractères")
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String lastName;

    @Column(name = "title", length = 50)
    private String title; // Dr., Prof., etc.

    @Column(name = "specialization", nullable = false, length = 200)
    @NotBlank(message = "La spécialisation est obligatoire")
    private String specialization;

    @Column(name = "phone", length = 20)
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Format de téléphone invalide")
    private String phone;

    @Column(name = "email", length = 100)
    @Email(message = "Format d'email invalide")
    private String email;

    @Column(name = "license_number", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Le numéro de licence est obligatoire")
    private String licenseNumber;

    @Column(name = "date_of_birth")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Patient.Gender gender;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "qualification", length = 500)
    private String qualification;

    @Column(name = "experience_years")
    @Min(value = 0, message = "L'expérience ne peut pas être négative")
    private Integer experienceYears;

    @Column(name = "consultation_fee")
    @DecimalMin(value = "0.0", message = "Les honoraires ne peuvent pas être négatifs")
    private Double consultationFee;

    @Column(name = "joining_date", nullable = false)
    private LocalDateTime joiningDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DoctorStatus status = DoctorStatus.ACTIVE;

    @Column(name = "room_number", length = 20)
    private String roomNumber;

    @Column(name = "available_days", length = 100)
    private String availableDays; // Lundi,Mardi,Mercredi...

    @Column(name = "available_time_from", length = 20)
    private String availableTimeFrom; // 09:00

    @Column(name = "available_time_to", length = 20)
    private String availableTimeTo; // 17:00

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "bio", length = 1000)
    private String bio;

    @Column(name = "languages", length = 200)
    private String languages;

    @Column(name = "emergency_contact", length = 20)
    private String emergencyContact;

    @Column(name = "notes", length = 2000)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Appointment> appointments;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Consultation> consultations;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Prescription> prescriptions;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Surgery> surgeries;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MedicalRecord> medicalRecords;

    @PrePersist
    public void prePersist() {
        if (joiningDate == null) {
            joiningDate = LocalDateTime.now();
        }
    }

    public enum DoctorStatus {
        ACTIVE("Actif"),
        INACTIVE("Inactif"),
        ON_LEAVE("En congé"),
        SUSPENDED("Suspendu");

        private final String displayName;

        DoctorStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public String getFullName() {
        return (title != null ? title + " " : "") + firstName + " " + lastName;
    }
}