package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "bill_items")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BillItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    @NotNull(message = "La facture est obligatoire")
    private Bill bill;

    @Column(name = "item_name", nullable = false, length = 200)
    @NotBlank(message = "Le nom de l'élément ne peut pas être vide")
    private String itemName;

    @Column(name = "item_code", length = 50)
    private String itemCode;

    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    @NotNull(message = "Le type d'élément est obligatoire")
    private ItemType itemType;

    @Column(name = "quantity", nullable = false)
    @Min(value = 1, message = "La quantité doit être d'au moins 1")
    @NotNull(message = "La quantité est obligatoire")
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    @DecimalMin(value = "0.0", message = "Le prix unitaire ne peut pas être négatif")
    @NotNull(message = "Le prix unitaire est obligatoire")
    private Double unitPrice;

    @Column(name = "total_price", nullable = false)
    @DecimalMin(value = "0.0", message = "Le prix total ne peut pas être négatif")
    @NotNull(message = "Le prix total est obligatoire")
    private Double totalPrice;

    @Column(name = "discount_amount")
    @DecimalMin(value = "0.0", message = "Le montant de la remise ne peut pas être négatif")
    private Double discountAmount = 0.0;

    @Column(name = "discount_rate")
    @DecimalMin(value = "0.0", message = "Le taux de remise ne peut pas être négatif")
    @DecimalMax(value = "100.0", message = "Le taux de remise ne peut pas dépasser 100%")
    private Double discountRate = 0.0;

    @Column(name = "tax_amount")
    @DecimalMin(value = "0.0", message = "Le montant de la taxe ne peut pas être négatif")
    private Double taxAmount = 0.0;

    @Column(name = "tax_rate")
    @DecimalMin(value = "0.0", message = "Le taux de taxe ne peut pas être négatif")
    @DecimalMax(value = "100.0", message = "Le taux de taxe ne peut pas dépasser 100%")
    private Double taxRate = 0.0;

    @Column(name = "net_amount")
    @DecimalMin(value = "0.0", message = "Le montant net ne peut pas être négatif")
    private Double netAmount;

    @Column(name = "service_date")
    private java.time.LocalDateTime serviceDate;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "performed_by", length = 100)
    private String performedBy;

    @Column(name = "notes", length = 1000)
    private String notes;

    @PrePersist
    public void prePersist() {
        calculateTotalPrice();
    }

    @PreUpdate
    public void preUpdate() {
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            totalPrice = unitPrice * quantity;
            
            double net = totalPrice;
            if (discountAmount != null) {
                net -= discountAmount;
            }
            if (taxAmount != null) {
                net += taxAmount;
            }
            netAmount = net;
        }
    }

    public enum ItemType {
        CONSULTATION("Consultation"),
        PROCEDURE("Procédure"),
        MEDICATION("Médicament"),
        LABORATORY("Laboratoire"),
        IMAGING("Imagerie"),
        SURGERY("Chirurgie"),
        ROOM_CHARGE("Frais de chambre"),
        NURSING_CARE("Soins infirmiers"),
        THERAPY("Thérapie"),
        EQUIPMENT("Équipement"),
        SUPPLIES("Fournitures"),
        AMBULANCE("Ambulance"),
        MISCELLANEOUS("Divers");

        private final String displayName;

        ItemType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}