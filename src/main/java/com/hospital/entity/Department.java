package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Set;

@Entity
@Table(name = "departments")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Department extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    @NotBlank(message = "Le nom du département ne peut pas être vide")
    @Size(min = 2, max = 200, message = "Le nom du département doit contenir entre 2 et 200 caractères")
    private String name;

    @Column(name = "code", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le code du département ne peut pas être vide")
    private String code;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "floor", length = 20)
    private String floor;

    @Column(name = "building", length = 100)
    private String building;

    @Column(name = "phone", length = 20)
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Format de téléphone invalide")
    private String phone;

    @Column(name = "email", length = 100)
    @Email(message = "Format d'email invalide")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_of_department_id")
    private Doctor headOfDepartment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DepartmentStatus status = DepartmentStatus.ACTIVE;

    @Column(name = "capacity")
    @Min(value = 0, message = "La capacité ne peut pas être négative")
    private Integer capacity;

    @Column(name = "budget")
    @DecimalMin(value = "0.0", message = "Le budget ne peut pas être négatif")
    private Double budget;

    @Column(name = "operating_hours", length = 100)
    private String operatingHours;

    @Column(name = "emergency_contact", length = 20)
    private String emergencyContact;

    @Column(name = "notes", length = 2000)
    private String notes;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Doctor> doctors;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Bed> beds;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Room> rooms;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Staff> staff;

    public enum DepartmentStatus {
        ACTIVE("Actif"),
        INACTIVE("Inactif"),
        UNDER_MAINTENANCE("En maintenance"),
        CLOSED("Fermé");

        private final String displayName;

        DepartmentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}