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
@Table(name = "bills")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Bill extends BaseEntity {

    @Column(name = "bill_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le numéro de facture ne peut pas être vide")
    private String billNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admission_id")
    private Admission admission;

    @Column(name = "bill_date", nullable = false)
    @NotNull(message = "La date de facture est obligatoire")
    private LocalDateTime billDate;

    @Column(name = "due_date")
    @Future(message = "La date d'échéance doit être dans le futur")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "bill_type", nullable = false)
    @NotNull(message = "Le type de facture est obligatoire")
    private BillType billType;

    @Column(name = "subtotal", nullable = false)
    @DecimalMin(value = "0.0", message = "Le sous-total ne peut pas être négatif")
    @NotNull(message = "Le sous-total est obligatoire")
    private Double subtotal;

    @Column(name = "tax_amount")
    @DecimalMin(value = "0.0", message = "Le montant de la taxe ne peut pas être négatif")
    private Double taxAmount = 0.0;

    @Column(name = "tax_rate")
    @DecimalMin(value = "0.0", message = "Le taux de taxe ne peut pas être négatif")
    @DecimalMax(value = "100.0", message = "Le taux de taxe ne peut pas dépasser 100%")
    private Double taxRate = 0.0;

    @Column(name = "discount_amount")
    @DecimalMin(value = "0.0", message = "Le montant de la remise ne peut pas être négatif")
    private Double discountAmount = 0.0;

    @Column(name = "discount_rate")
    @DecimalMin(value = "0.0", message = "Le taux de remise ne peut pas être négatif")
    @DecimalMax(value = "100.0", message = "Le taux de remise ne peut pas dépasser 100%")
    private Double discountRate = 0.0;

    @Column(name = "total_amount", nullable = false)
    @DecimalMin(value = "0.0", message = "Le montant total ne peut pas être négatif")
    @NotNull(message = "Le montant total est obligatoire")
    private Double totalAmount;

    @Column(name = "paid_amount")
    @DecimalMin(value = "0.0", message = "Le montant payé ne peut pas être négatif")
    private Double paidAmount = 0.0;

    @Column(name = "balance_amount")
    @DecimalMin(value = "0.0", message = "Le solde ne peut pas être négatif")
    private Double balanceAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "payment_method", length = 100)
    private String paymentMethod;

    @Column(name = "currency", length = 10)
    private String currency = "EUR";

    @Column(name = "insurance_claim_number", length = 100)
    private String insuranceClaimNumber;

    @Column(name = "insurance_coverage_percentage")
    @DecimalMin(value = "0.0", message = "Le pourcentage de couverture ne peut pas être négatif")
    @DecimalMax(value = "100.0", message = "Le pourcentage de couverture ne peut pas dépasser 100%")
    private Double insuranceCoveragePercentage = 0.0;

    @Column(name = "insurance_amount")
    @DecimalMin(value = "0.0", message = "Le montant de l'assurance ne peut pas être négatif")
    private Double insuranceAmount = 0.0;

    @Column(name = "patient_amount")
    @DecimalMin(value = "0.0", message = "Le montant du patient ne peut pas être négatif")
    private Double patientAmount;

    @Column(name = "generated_by", length = 100)
    private String generatedBy;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "is_approved")
    private Boolean isApproved = false;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "receipt_number", length = 100)
    private String receiptNumber;

    @Column(name = "is_refunded")
    private Boolean isRefunded = false;

    @Column(name = "refund_amount")
    @DecimalMin(value = "0.0", message = "Le montant du remboursement ne peut pas être négatif")
    private Double refundAmount = 0.0;

    @Column(name = "refund_date")
    private LocalDateTime refundDate;

    @Column(name = "refund_reason", length = 500)
    private String refundReason;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BillItem> billItems;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Payment> payments;

    @PrePersist
    public void prePersist() {
        if (billDate == null) {
            billDate = LocalDateTime.now();
        }
        if (dueDate == null) {
            dueDate = LocalDate.now().plusDays(30); // 30 jours par défaut
        }
        calculateTotalAmount();
    }

    @PreUpdate
    public void preUpdate() {
        calculateTotalAmount();
    }

    private void calculateTotalAmount() {
        if (subtotal != null) {
            double total = subtotal;
            if (taxAmount != null) {
                total += taxAmount;
            }
            if (discountAmount != null) {
                total -= discountAmount;
            }
            totalAmount = total;
            
            if (paidAmount != null) {
                balanceAmount = totalAmount - paidAmount;
            } else {
                balanceAmount = totalAmount;
            }
            
            if (insuranceAmount != null) {
                patientAmount = totalAmount - insuranceAmount;
            } else {
                patientAmount = totalAmount;
            }
        }
    }

    public enum BillType {
        CONSULTATION("Consultation"),
        ADMISSION("Admission"),
        EMERGENCY("Urgence"),
        SURGERY("Chirurgie"),
        LABORATORY("Laboratoire"),
        IMAGING("Imagerie"),
        PHARMACY("Pharmacie"),
        ROOM_CHARGES("Frais de chambre"),
        PROCEDURE("Procédure"),
        TREATMENT("Traitement"),
        MISCELLANEOUS("Divers");

        private final String displayName;

        BillType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum PaymentStatus {
        PENDING("En attente"),
        PARTIAL("Partiel"),
        PAID("Payé"),
        OVERDUE("En retard"),
        REFUNDED("Remboursé"),
        CANCELLED("Annulé");

        private final String displayName;

        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}