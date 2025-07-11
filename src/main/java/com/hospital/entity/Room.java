package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Set;

@Entity
@Table(name = "rooms")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Room extends BaseEntity {

    @Column(name = "room_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le numéro de chambre ne peut pas être vide")
    private String roomNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    @NotNull(message = "Le département est obligatoire")
    private Department department;

    @Column(name = "room_name", length = 200)
    private String roomName;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    @NotNull(message = "Le type de chambre est obligatoire")
    private RoomType roomType;

    @Column(name = "floor", length = 20)
    private String floor;

    @Column(name = "building", length = 100)
    private String building;

    @Column(name = "wing", length = 100)
    private String wing;

    @Column(name = "capacity")
    @Min(value = 1, message = "La capacité doit être d'au moins 1")
    private Integer capacity;

    @Column(name = "occupied_beds")
    @Min(value = 0, message = "Le nombre de lits occupés ne peut pas être négatif")
    private Integer occupiedBeds = 0;

    @Column(name = "available_beds")
    @Min(value = 0, message = "Le nombre de lits disponibles ne peut pas être négatif")
    private Integer availableBeds;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RoomStatus status = RoomStatus.AVAILABLE;

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

    @Column(name = "area_sqm")
    @DecimalMin(value = "0.0", message = "La superficie ne peut pas être négative")
    private Double areaSqm;

    @Column(name = "is_private")
    private Boolean isPrivate = false;

    @Column(name = "is_isolation")
    private Boolean isIsolation = false;

    @Column(name = "is_icu")
    private Boolean isIcu = false;

    @Column(name = "is_emergency")
    private Boolean isEmergency = false;

    @Column(name = "is_operating_room")
    private Boolean isOperatingRoom = false;

    @Column(name = "is_consultation_room")
    private Boolean isConsultationRoom = false;

    @Column(name = "is_laboratory")
    private Boolean isLaboratory = false;

    @Column(name = "is_radiology")
    private Boolean isRadiology = false;

    @Column(name = "is_pharmacy")
    private Boolean isPharmacy = false;

    @Column(name = "is_storage")
    private Boolean isStorage = false;

    @Column(name = "is_office")
    private Boolean isOffice = false;

    @Column(name = "has_oxygen")
    private Boolean hasOxygen = false;

    @Column(name = "has_suction")
    private Boolean hasSuction = false;

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

    @Column(name = "has_window")
    private Boolean hasWindow = false;

    @Column(name = "has_balcony")
    private Boolean hasBalcony = false;

    @Column(name = "temperature_controlled")
    private Boolean temperatureControlled = false;

    @Column(name = "humidity_controlled")
    private Boolean humidityControlled = false;

    @Column(name = "sterile_environment")
    private Boolean sterileEnvironment = false;

    @Column(name = "maintenance_date")
    private java.time.LocalDateTime maintenanceDate;

    @Column(name = "last_cleaned")
    private java.time.LocalDateTime lastCleaned;

    @Column(name = "cleaned_by", length = 100)
    private String cleanedBy;

    @Column(name = "housekeeping_schedule", length = 500)
    private String housekeepingSchedule;

    @Column(name = "notes", length = 2000)
    private String notes;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Bed> beds;

    @PreUpdate
    public void preUpdate() {
        if (capacity != null && occupiedBeds != null) {
            availableBeds = capacity - occupiedBeds;
        }
    }

    @PrePersist
    public void prePersist() {
        if (capacity != null && occupiedBeds != null) {
            availableBeds = capacity - occupiedBeds;
        }
    }

    public enum RoomType {
        PATIENT_ROOM("Chambre patient"),
        ICU("Soins intensifs"),
        EMERGENCY("Urgence"),
        OPERATING_ROOM("Salle d'opération"),
        CONSULTATION_ROOM("Salle de consultation"),
        LABORATORY("Laboratoire"),
        RADIOLOGY("Radiologie"),
        PHARMACY("Pharmacie"),
        STORAGE("Stockage"),
        OFFICE("Bureau"),
        CONFERENCE_ROOM("Salle de conférence"),
        WAITING_ROOM("Salle d'attente"),
        RECOVERY_ROOM("Salle de réveil"),
        ISOLATION_ROOM("Chambre d'isolement"),
        MATERNITY_ROOM("Chambre de maternité"),
        PEDIATRIC_ROOM("Chambre pédiatrique"),
        PSYCHIATRIC_ROOM("Chambre psychiatrique"),
        CAFETERIA("Cafétéria"),
        CHAPEL("Chapelle"),
        UTILITY_ROOM("Salle utilitaire"),
        CLEANING_ROOM("Salle de nettoyage"),
        OTHER("Autre");

        private final String displayName;

        RoomType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum RoomStatus {
        AVAILABLE("Disponible"),
        OCCUPIED("Occupé"),
        RESERVED("Réservé"),
        MAINTENANCE("Maintenance"),
        CLEANING("Nettoyage"),
        OUT_OF_ORDER("Hors service"),
        QUARANTINE("Quarantaine");

        private final String displayName;

        RoomStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}