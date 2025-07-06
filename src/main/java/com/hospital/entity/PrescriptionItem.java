package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "prescription_items")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PrescriptionItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    @NotNull(message = "La prescription est obligatoire")
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false)
    @NotNull(message = "Le médicament est obligatoire")
    private Medication medication;

    @Column(name = "dosage", nullable = false, length = 100)
    @NotBlank(message = "Le dosage ne peut pas être vide")
    private String dosage;

    @Column(name = "frequency", nullable = false, length = 100)
    @NotBlank(message = "La fréquence ne peut pas être vide")
    private String frequency;

    @Column(name = "duration", nullable = false, length = 100)
    @NotBlank(message = "La durée ne peut pas être vide")
    private String duration;

    @Column(name = "route", length = 100)
    private String route; // oral, IV, IM, etc.

    @Column(name = "quantity", nullable = false)
    @Min(value = 1, message = "La quantité doit être d'au moins 1")
    @NotNull(message = "La quantité est obligatoire")
    private Integer quantity;

    @Column(name = "unit", length = 50)
    private String unit; // comprimés, ml, mg, etc.

    @Column(name = "instructions", length = 1000)
    private String instructions;

    @Column(name = "unit_price")
    @DecimalMin(value = "0.0", message = "Le prix unitaire ne peut pas être négatif")
    private Double unitPrice;

    @Column(name = "total_price")
    @DecimalMin(value = "0.0", message = "Le prix total ne peut pas être négatif")
    private Double totalPrice;

    @Column(name = "is_substitutable")
    private Boolean isSubstitutable = true;

    @Column(name = "is_dispensed")
    private Boolean isDispensed = false;

    @Column(name = "dispensed_quantity")
    @Min(value = 0, message = "La quantité dispensée ne peut pas être négative")
    private Integer dispensedQuantity = 0;

    @Column(name = "notes", length = 1000)
    private String notes;

    @PrePersist
    public void prePersist() {
        if (totalPrice == null && unitPrice != null && quantity != null) {
            totalPrice = unitPrice * quantity;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (totalPrice == null && unitPrice != null && quantity != null) {
            totalPrice = unitPrice * quantity;
        }
    }
}