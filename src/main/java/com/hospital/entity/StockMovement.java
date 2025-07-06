package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StockMovement extends BaseEntity {

    @Column(name = "movement_number", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Le numéro de mouvement ne peut pas être vide")
    private String movementNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    @NotNull(message = "L'article d'inventaire est obligatoire")
    private InventoryItem inventoryItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    @NotNull(message = "Le type de mouvement est obligatoire")
    private MovementType movementType;

    @Column(name = "quantity", nullable = false)
    @NotNull(message = "La quantité est obligatoire")
    private Integer quantity;

    @Column(name = "unit_cost")
    @DecimalMin(value = "0.0", message = "Le coût unitaire ne peut pas être négatif")
    private Double unitCost;

    @Column(name = "total_cost")
    @DecimalMin(value = "0.0", message = "Le coût total ne peut pas être négatif")
    private Double totalCost;

    @Column(name = "movement_date", nullable = false)
    @NotNull(message = "La date de mouvement est obligatoire")
    private LocalDateTime movementDate;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @Column(name = "lot_number", length = 100)
    private String lotNumber;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber; // PO, Invoice, etc.

    @Column(name = "supplier_name", length = 200)
    private String supplierName;

    @Column(name = "location_from", length = 200)
    private String locationFrom;

    @Column(name = "location_to", length = 200)
    private String locationTo;

    @Column(name = "performed_by", length = 100)
    private String performedBy;

    @Column(name = "authorized_by", length = 100)
    private String authorizedBy;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "stock_before")
    private Integer stockBefore;

    @Column(name = "stock_after")
    private Integer stockAfter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient; // For patient-specific movements

    @PrePersist
    public void prePersist() {
        if (movementDate == null) {
            movementDate = LocalDateTime.now();
        }
        if (totalCost == null && unitCost != null) {
            totalCost = unitCost * quantity;
        }
    }

    public enum MovementType {
        PURCHASE("Achat"),
        RECEIPT("Réception"),
        ISSUE("Sortie"),
        RETURN("Retour"),
        TRANSFER("Transfert"),
        ADJUSTMENT("Ajustement"),
        DISPOSAL("Élimination"),
        EXPIRY("Expiration"),
        DAMAGE("Dommage"),
        THEFT("Vol"),
        CONSUMPTION("Consommation"),
        ALLOCATION("Allocation"),
        DEALLOCATION("Désallocation"),
        RESERVATION("Réservation"),
        RELEASE("Libération"),
        INVENTORY_COUNT("Comptage d'inventaire"),
        WRITE_OFF("Radiation"),
        SAMPLE("Échantillon"),
        MAINTENANCE("Maintenance"),
        CALIBRATION("Calibration"),
        LOAN("Prêt"),
        LOAN_RETURN("Retour de prêt");

        private final String displayName;

        MovementType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}