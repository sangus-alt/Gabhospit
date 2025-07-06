package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "vaccines")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Vaccine extends BaseEntity {

    @Column(name = "vaccine_code", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le code du vaccin ne peut pas être vide")
    private String vaccineCode;

    @Column(name = "vaccine_name", nullable = false, length = 200)
    @NotBlank(message = "Le nom du vaccin ne peut pas être vide")
    private String vaccineName;

    @Column(name = "vaccine_name_en", length = 200)
    private String vaccineNameEn;

    @Column(name = "manufacturer", nullable = false, length = 200)
    @NotBlank(message = "Le fabricant ne peut pas être vide")
    private String manufacturer;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @Column(name = "lot_number", length = 100)
    private String lotNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "vaccine_type", nullable = false)
    @NotNull(message = "Le type de vaccin est obligatoire")
    private VaccineType vaccineType;

    @Column(name = "disease_prevented", nullable = false, length = 500)
    @NotBlank(message = "La maladie prévenue ne peut pas être vide")
    private String diseasePrevented;

    @Column(name = "route_of_administration", nullable = false, length = 100)
    @NotBlank(message = "La voie d'administration ne peut pas être vide")
    private String routeOfAdministration;

    @Column(name = "dosage", nullable = false, length = 50)
    @NotBlank(message = "Le dosage ne peut pas être vide")
    private String dosage;

    @Column(name = "volume", length = 20)
    private String volume;

    @Column(name = "minimum_age_months")
    @Min(value = 0, message = "L'âge minimum ne peut pas être négatif")
    private Integer minimumAgeMonths;

    @Column(name = "maximum_age_months")
    private Integer maximumAgeMonths;

    @Column(name = "doses_required")
    @Min(value = 1, message = "Le nombre de doses doit être d'au moins 1")
    private Integer dosesRequired = 1;

    @Column(name = "interval_between_doses_days")
    private Integer intervalBetweenDosesDays;

    @Column(name = "booster_required")
    private Boolean boosterRequired = false;

    @Column(name = "booster_interval_months")
    private Integer boosterIntervalMonths;

    @Column(name = "contraindications", length = 1000)
    private String contraindications;

    @Column(name = "precautions", length = 1000)
    private String precautions;

    @Column(name = "side_effects", length = 1000)
    private String sideEffects;

    @Column(name = "storage_temperature_min")
    private Double storageTemperatureMin;

    @Column(name = "storage_temperature_max")
    private Double storageTemperatureMax;

    @Column(name = "storage_conditions", length = 500)
    private String storageConditions;

    @Column(name = "cold_chain_required")
    private Boolean coldChainRequired = true;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "expiry_date")
    @Future(message = "La date d'expiration doit être dans le futur")
    private LocalDate expiryDate;

    @Column(name = "vial_size")
    @Min(value = 1, message = "La taille du flacon doit être d'au moins 1")
    private Integer vialSize;

    @Column(name = "doses_per_vial")
    @Min(value = 1, message = "Le nombre de doses par flacon doit être d'au moins 1")
    private Integer dosesPerVial;

    @Column(name = "stock_quantity")
    @Min(value = 0, message = "La quantité en stock ne peut pas être négative")
    private Integer stockQuantity = 0;

    @Column(name = "reserved_quantity")
    @Min(value = 0, message = "La quantité réservée ne peut pas être négative")
    private Integer reservedQuantity = 0;

    @Column(name = "min_stock_level")
    @Min(value = 0, message = "Le niveau minimum de stock ne peut pas être négatif")
    private Integer minStockLevel = 0;

    @Column(name = "unit_cost")
    @DecimalMin(value = "0.0", message = "Le coût unitaire ne peut pas être négatif")
    private Double unitCost;

    @Column(name = "currency", length = 10)
    private String currency = "EUR";

    @Column(name = "who_recommended")
    private Boolean whoRecommended = false;

    @Column(name = "national_immunization_schedule")
    private Boolean nationalImmunizationSchedule = false;

    @Column(name = "pregnancy_safe")
    private Boolean pregnancySafe = false;

    @Column(name = "immunocompromised_safe")
    private Boolean immunocompromisedSafe = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VaccineStatus status = VaccineStatus.ACTIVE;

    @Column(name = "ndc_number", length = 20)
    private String ndcNumber; // National Drug Code

    @Column(name = "cvx_code", length = 10)
    private String cvxCode; // CDC Vaccine Code

    @Column(name = "mvx_code", length = 10)
    private String mvxCode; // Manufacturer Code

    @Column(name = "composition", length = 1000)
    private String composition;

    @Column(name = "adjuvant", length = 200)
    private String adjuvant;

    @Column(name = "preservative", length = 200)
    private String preservative;

    @Column(name = "latex_free")
    private Boolean latexFree = true;

    @Column(name = "thimerosal_free")
    private Boolean thimerosalFree = true;

    @Column(name = "special_instructions", length = 1000)
    private String specialInstructions;

    @Column(name = "notes", length = 2000)
    private String notes;

    @OneToMany(mappedBy = "vaccine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<VaccinationRecord> vaccinationRecords;

    public enum VaccineType {
        LIVE_ATTENUATED("Virus vivant atténué"),
        INACTIVATED("Inactivé"),
        SUBUNIT("Sous-unité"),
        TOXOID("Anatoxine"),
        CONJUGATE("Conjugué"),
        MRNA("ARNm"),
        VIRAL_VECTOR("Vecteur viral"),
        RECOMBINANT("Recombinant"),
        COMBINATION("Combiné");

        private final String displayName;

        VaccineType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum VaccineStatus {
        ACTIVE("Actif"),
        INACTIVE("Inactif"),
        DISCONTINUED("Discontinué"),
        RECALLED("Rappelé"),
        EXPIRED("Expiré"),
        OUT_OF_STOCK("Rupture de stock");

        private final String displayName;

        VaccineStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}