package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Set;

@Entity
@Table(name = "beds")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Bed extends BaseEntity {

    @Column(name = "bed_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le numéro de lit ne peut pas être vide")
    private String bedNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @NotNull(message = "La chambre est obligatoire")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    @NotNull(message = "Le département est obligatoire")
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(name = "bed_type", nullable = false)
    @NotNull(message = "Le type de lit est obligatoire")
    private BedType bedType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BedStatus status = BedStatus.AVAILABLE;

    @Column(name = "is_occupied")
    private Boolean isOccupied = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_patient_id")
    private Patient currentPatient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_admission_id")
    private Admission currentAdmission;

    @Column(name = "daily_rate")
    @DecimalMin(value = "0.0", message = "Le tarif journalier ne peut pas être négatif")
    private Double dailyRate;

    @Column(name = "currency", length = 10)
    private String currency = "EUR";

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "features", length = 1000)
    private String features;

    @Column(name = "equipment", length = 1000)
    private String equipment;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "is_private")
    private Boolean isPrivate = false;

    @Column(name = "is_icu")
    private Boolean isIcu = false;

    @Column(name = "is_emergency")
    private Boolean isEmergency = false;

    @Column(name = "is_pediatric")
    private Boolean isPediatric = false;

    @Column(name = "is_maternity")
    private Boolean isMaternity = false;

    @Column(name = "is_quarantine")
    private Boolean isQuarantine = false;

    @Column(name = "is_isolation")
    private Boolean isIsolation = false;

    @Column(name = "has_oxygen")
    private Boolean hasOxygen = false;

    @Column(name = "has_ventilator")
    private Boolean hasVentilator = false;

    @Column(name = "has_monitor")
    private Boolean hasMonitor = false;

    @Column(name = "has_tv")
    private Boolean hasTv = false;

    @Column(name = "has_phone")
    private Boolean hasPhone = false;

    @Column(name = "has_internet")
    private Boolean hasInternet = false;

    @Column(name = "has_bathroom")
    private Boolean hasBathroom = false;

    @Column(name = "has_air_conditioning")
    private Boolean hasAirConditioning = false;

    @Column(name = "maintenance_date")
    private java.time.LocalDateTime maintenanceDate;

    @Column(name = "last_cleaned")
    private java.time.LocalDateTime lastCleaned;

    @Column(name = "cleaned_by", length = 100)
    private String cleanedBy;

    @Column(name = "notes", length = 2000)
    private String notes;

    @OneToMany(mappedBy = "bed", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Admission> admissions;

    public enum BedType {
        STANDARD("Standard"),
        ELECTRIC("Électrique"),
        ICU("Soins intensifs"),
        PEDIATRIC("Pédiatrique"),
        MATERNITY("Maternité"),
        PSYCHIATRIC("Psychiatrique"),
        ISOLATION("Isolement"),
        EMERGENCY("Urgence"),
        RECOVERY("Récupération"),
        CARDIAC("Cardiaque"),
        ORTHOPEDIC("Orthopédique"),
        BARIATRIC("Bariatrique");

        private final String displayName;

        BedType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum BedStatus {
        AVAILABLE("Disponible"),
        OCCUPIED("Occupé"),
        RESERVED("Réservé"),
        MAINTENANCE("Maintenance"),
        CLEANING("Nettoyage"),
        OUT_OF_ORDER("Hors service"),
        QUARANTINE("Quarantaine");

        private final String displayName;

        BedStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}