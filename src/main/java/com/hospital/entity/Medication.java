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
@Table(name = "medications")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Medication extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    @NotBlank(message = "Le nom du médicament ne peut pas être vide")
    @Size(min = 2, max = 200, message = "Le nom doit contenir entre 2 et 200 caractères")
    private String name;

    @Column(name = "generic_name", length = 200)
    private String genericName;

    @Column(name = "brand_name", length = 200)
    private String brandName;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Le code du médicament ne peut pas être vide")
    private String code;

    @Column(name = "barcode", length = 100)
    private String barcode;

    @Column(name = "manufacturer", length = 200)
    private String manufacturer;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "active_ingredient", length = 500)
    private String activeIngredient;

    @Column(name = "strength", length = 100)
    private String strength;

    @Column(name = "dosage_form", length = 100)
    private String dosageForm; // tablet, capsule, liquid, etc.

    @Column(name = "route_of_administration", length = 200)
    private String routeOfAdministration;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    @NotNull(message = "La catégorie est obligatoire")
    private MedicationCategory category;

    @Column(name = "therapeutic_class", length = 200)
    private String therapeuticClass;

    @Column(name = "unit_price")
    @DecimalMin(value = "0.0", message = "Le prix unitaire ne peut pas être négatif")
    private Double unitPrice;

    @Column(name = "currency", length = 10)
    private String currency = "EUR";

    @Column(name = "stock_quantity")
    @Min(value = 0, message = "La quantité en stock ne peut pas être négative")
    private Integer stockQuantity = 0;

    @Column(name = "min_stock_level")
    @Min(value = 0, message = "Le niveau minimum de stock ne peut pas être négatif")
    private Integer minStockLevel = 0;

    @Column(name = "max_stock_level")
    @Min(value = 0, message = "Le niveau maximum de stock ne peut pas être négatif")
    private Integer maxStockLevel = 0;

    @Column(name = "expiry_date")
    @Future(message = "La date d'expiration doit être dans le futur")
    private LocalDate expiryDate;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @Column(name = "storage_conditions", length = 500)
    private String storageConditions;

    @Column(name = "contraindications", length = 1000)
    private String contraindications;

    @Column(name = "side_effects", length = 1000)
    private String sideEffects;

    @Column(name = "drug_interactions", length = 1000)
    private String drugInteractions;

    @Column(name = "allergic_reactions", length = 1000)
    private String allergicReactions;

    @Column(name = "warnings", length = 1000)
    private String warnings;

    @Column(name = "is_controlled_substance")
    private Boolean isControlledSubstance = false;

    @Column(name = "schedule_class", length = 20)
    private String scheduleClass;

    @Column(name = "requires_prescription")
    private Boolean requiresPrescription = true;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "is_discontinued")
    private Boolean isDiscontinued = false;

    @Column(name = "supplier", length = 200)
    private String supplier;

    @Column(name = "supplier_contact", length = 100)
    private String supplierContact;

    @Column(name = "notes", length = 2000)
    private String notes;

    // Nouveaux champs pour corriger les erreurs de compilation
    @Column(name = "medication_code", length = 50)
    private String medicationCode;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "price")
    private Double price;

    @Column(name = "current_stock")
    private Integer currentStock;

    @Column(name = "minimum_stock")
    private Integer minimumStock;

    @Column(name = "dosage", length = 100)
    private String dosage;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PrescriptionItem> prescriptionItems;

    @PrePersist
    public void prePersist() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
        if (medicationCode == null) {
            medicationCode = code;
        }
        if (price == null) {
            price = unitPrice;
        }
        if (currentStock == null) {
            currentStock = stockQuantity;
        }
        if (minimumStock == null) {
            minimumStock = minStockLevel;
        }
        if (dosage == null) {
            dosage = strength;
        }
        if (lastUpdated == null) {
            lastUpdated = LocalDateTime.now();
        }
    }

    public enum MedicationCategory {
        ANTIBIOTIC("Antibiotique"),
        ANALGESIC("Analgésique"),
        ANTIHISTAMINE("Antihistaminique"),
        ANTIHYPERTENSIVE("Antihypertenseur"),
        DIABETIC("Diabétique"),
        CARDIAC("Cardiaque"),
        NEUROLOGICAL("Neurologique"),
        PSYCHIATRIC("Psychiatrique"),
        ONCOLOGY("Oncologie"),
        GASTROENTEROLOGY("Gastroentérologie"),
        RESPIRATORY("Respiratoire"),
        DERMATOLOGY("Dermatologie"),
        OPHTHALMOLOGY("Ophtalmologie"),
        ORTHOPEDIC("Orthopédique"),
        PEDIATRIC("Pédiatrique"),
        GERIATRIC("Gériatrique"),
        EMERGENCY("Urgence"),
        ANESTHESIA("Anesthésie"),
        VACCINE("Vaccin"),
        SUPPLEMENT("Supplément"),
        OTHER("Autre");

        private final String displayName;

        MedicationCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}