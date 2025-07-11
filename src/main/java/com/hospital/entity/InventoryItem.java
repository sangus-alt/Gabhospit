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
@Table(name = "inventory_items")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InventoryItem extends BaseEntity {

    @Column(name = "item_code", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Le code de l'article ne peut pas être vide")
    private String itemCode;

    @Column(name = "barcode", length = 100)
    private String barcode;

    @Column(name = "item_name", nullable = false, length = 200)
    @NotBlank(message = "Le nom de l'article ne peut pas être vide")
    private String itemName;

    @Column(name = "item_name_en", length = 200)
    private String itemNameEn;

    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    @NotNull(message = "La catégorie est obligatoire")
    private ItemCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "subcategory")
    private ItemSubcategory subcategory;

    @Column(name = "manufacturer", length = 200)
    private String manufacturer;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "model_number", length = 100)
    private String modelNumber;

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    @Column(name = "unit_of_measure", nullable = false, length = 50)
    @NotBlank(message = "L'unité de mesure ne peut pas être vide")
    private String unitOfMeasure;

    @Column(name = "package_size")
    @Min(value = 1, message = "La taille du paquet doit être d'au moins 1")
    private Integer packageSize = 1;

    @Column(name = "current_stock")
    @Min(value = 0, message = "Le stock actuel ne peut pas être négatif")
    private Integer currentStock = 0;

    @Column(name = "allocated_stock")
    @Min(value = 0, message = "Le stock alloué ne peut pas être négatif")
    private Integer allocatedStock = 0;

    @Column(name = "available_stock")
    @Min(value = 0, message = "Le stock disponible ne peut pas être négatif")
    private Integer availableStock = 0;

    @Column(name = "reserved_stock")
    @Min(value = 0, message = "Le stock réservé ne peut pas être négatif")
    private Integer reservedStock = 0;

    @Column(name = "minimum_stock_level")
    @Min(value = 0, message = "Le niveau minimum de stock ne peut pas être négatif")
    private Integer minimumStockLevel = 0;

    @Column(name = "maximum_stock_level")
    @Min(value = 0, message = "Le niveau maximum de stock ne peut pas être négatif")
    private Integer maximumStockLevel = 0;

    @Column(name = "reorder_level")
    @Min(value = 0, message = "Le niveau de réapprovisionnement ne peut pas être négatif")
    private Integer reorderLevel = 0;

    @Column(name = "reorder_quantity")
    @Min(value = 1, message = "La quantité de réapprovisionnement doit être d'au moins 1")
    private Integer reorderQuantity = 1;

    @Column(name = "unit_cost")
    @DecimalMin(value = "0.0", message = "Le coût unitaire ne peut pas être négatif")
    private Double unitCost;

    @Column(name = "purchase_price")
    @DecimalMin(value = "0.0", message = "Le prix d'achat ne peut pas être négatif")
    private Double purchasePrice;

    @Column(name = "selling_price")
    @DecimalMin(value = "0.0", message = "Le prix de vente ne peut pas être négatif")
    private Double sellingPrice;

    @Column(name = "currency", length = 10)
    private String currency = "EUR";

    @Column(name = "supplier_name", length = 200)
    private String supplierName;

    @Column(name = "supplier_code", length = 50)
    private String supplierCode;

    @Column(name = "supplier_contact", length = 100)
    private String supplierContact;

    @Column(name = "lead_time_days")
    @Min(value = 0, message = "Le délai de livraison ne peut pas être négatif")
    private Integer leadTimeDays = 0;

    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @Column(name = "lot_number", length = 100)
    private String lotNumber;

    @Column(name = "storage_location", length = 200)
    private String storageLocation;

    @Column(name = "storage_conditions", length = 500)
    private String storageConditions;

    @Column(name = "storage_temperature_min")
    private Double storageTemperatureMin;

    @Column(name = "storage_temperature_max")
    private Double storageTemperatureMax;

    @Column(name = "storage_humidity_min")
    private Double storageHumidityMin;

    @Column(name = "storage_humidity_max")
    private Double storageHumidityMax;

    @Column(name = "is_hazardous")
    private Boolean isHazardous = false;

    @Column(name = "hazard_class", length = 100)
    private String hazardClass;

    @Column(name = "safety_instructions", length = 1000)
    private String safetyInstructions;

    @Column(name = "disposal_instructions", length = 1000)
    private String disposalInstructions;

    @Column(name = "is_sterile")
    private Boolean isSterile = false;

    @Column(name = "sterilization_method", length = 200)
    private String sterilizationMethod;

    @Column(name = "is_single_use")
    private Boolean isSingleUse = false;

    @Column(name = "is_reusable")
    private Boolean isReusable = true;

    @Column(name = "cleaning_instructions", length = 1000)
    private String cleaningInstructions;

    @Column(name = "maintenance_required")
    private Boolean maintenanceRequired = false;

    @Column(name = "maintenance_interval_days")
    private Integer maintenanceIntervalDays;

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    @Column(name = "warranty_start_date")
    private LocalDate warrantyStartDate;

    @Column(name = "warranty_end_date")
    private LocalDate warrantyEndDate;

    @Column(name = "warranty_provider", length = 200)
    private String warrantyProvider;

    @Column(name = "asset_tag", length = 100)
    private String assetTag;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "purchase_order_number", length = 100)
    private String purchaseOrderNumber;

    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;

    @Column(name = "depreciation_rate")
    private Double depreciationRate;

    @Column(name = "current_value")
    private Double currentValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ItemStatus status = ItemStatus.ACTIVE;

    @Column(name = "is_consumable")
    private Boolean isConsumable = true;

    @Column(name = "is_trackable")
    private Boolean isTrackable = true;

    @Column(name = "requires_prescription")
    private Boolean requiresPrescription = false;

    @Column(name = "controlled_substance")
    private Boolean controlledSubstance = false;

    @Column(name = "fda_approved")
    private Boolean fdaApproved = true;

    @Column(name = "ce_marked")
    private Boolean ceMarked = true;

    @Column(name = "iso_certified")
    private Boolean isoCertified = false;

    @Column(name = "quality_certificate", length = 500)
    private String qualityCertificate;

    @Column(name = "usage_instructions", length = 2000)
    private String usageInstructions;

    @Column(name = "contraindications", length = 1000)
    private String contraindications;

    @Column(name = "side_effects", length = 1000)
    private String sideEffects;

    @Column(name = "dimensions", length = 100)
    private String dimensions;

    @Column(name = "weight", length = 50)
    private String weight;

    @Column(name = "volume", length = 50)
    private String volume;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "material", length = 100)
    private String material;

    @Column(name = "country_of_origin", length = 100)
    private String countryOfOrigin;

    @Column(name = "last_updated_by", length = 100)
    private String lastUpdatedBy;

    @Column(name = "last_stock_update")
    private LocalDateTime lastStockUpdate;

    @Column(name = "last_counted_date")
    private LocalDate lastCountedDate;

    @Column(name = "notes", length = 2000)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "inventoryItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<StockMovement> stockMovements;

    @PreUpdate
    public void preUpdate() {
        lastStockUpdate = LocalDateTime.now();
        availableStock = currentStock - allocatedStock - reservedStock;
    }

    @PrePersist
    public void prePersist() {
        lastStockUpdate = LocalDateTime.now();
        availableStock = currentStock - allocatedStock - reservedStock;
    }

    public enum ItemCategory {
        MEDICAL_SUPPLIES("Fournitures médicales"),
        SURGICAL_INSTRUMENTS("Instruments chirurgicaux"),
        DIAGNOSTIC_EQUIPMENT("Équipement de diagnostic"),
        MONITORING_EQUIPMENT("Équipement de surveillance"),
        THERAPEUTIC_EQUIPMENT("Équipement thérapeutique"),
        LABORATORY_SUPPLIES("Fournitures de laboratoire"),
        PHARMACY_SUPPLIES("Fournitures pharmaceutiques"),
        PERSONAL_PROTECTIVE_EQUIPMENT("Équipement de protection individuelle"),
        DISPOSABLE_SUPPLIES("Fournitures jetables"),
        CLEANING_SUPPLIES("Fournitures de nettoyage"),
        OFFICE_SUPPLIES("Fournitures de bureau"),
        FURNITURE("Mobilier"),
        IT_EQUIPMENT("Équipement informatique"),
        MAINTENANCE_SUPPLIES("Fournitures de maintenance"),
        UTILITIES("Services publics"),
        OTHER("Autre");

        private final String displayName;

        ItemCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ItemSubcategory {
        SYRINGES("Seringues"),
        NEEDLES("Aiguilles"),
        CATHETERS("Cathéters"),
        BANDAGES("Bandages"),
        GLOVES("Gants"),
        MASKS("Masques"),
        SCALPELS("Scalpels"),
        FORCEPS("Forceps"),
        STETHOSCOPES("Stéthoscopes"),
        THERMOMETERS("Thermomètres"),
        BLOOD_PRESSURE_MONITORS("Tensiomètres"),
        ECG_MACHINES("Machines ECG"),
        X_RAY_EQUIPMENT("Équipement de radiographie"),
        ULTRASOUND_MACHINES("Machines à ultrasons"),
        DEFIBRILLATORS("Défibrillateurs"),
        VENTILATORS("Ventilateurs"),
        INFUSION_PUMPS("Pompes à perfusion"),
        WHEELCHAIRS("Fauteuils roulants"),
        HOSPITAL_BEDS("Lits d'hôpital"),
        COMPUTERS("Ordinateurs"),
        PRINTERS("Imprimantes"),
        SOFTWARE("Logiciels");

        private final String displayName;

        ItemSubcategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ItemStatus {
        ACTIVE("Actif"),
        INACTIVE("Inactif"),
        DISCONTINUED("Discontinué"),
        OUT_OF_STOCK("Rupture de stock"),
        EXPIRED("Expiré"),
        RECALLED("Rappelé"),
        UNDER_MAINTENANCE("En maintenance"),
        DAMAGED("Endommagé"),
        QUARANTINED("En quarantaine");

        private final String displayName;

        ItemStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}